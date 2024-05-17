package ru.polytech.cloudimagerepository.service.impl;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.model.GridFSFile;
import dev.brachtendorf.jimagehash.hash.Hash;
import dev.brachtendorf.jimagehash.hashAlgorithms.HashingAlgorithm;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.polytech.cloudimagerepository.exception.*;
import ru.polytech.cloudimagerepository.mapper.ClusterMapper;
import ru.polytech.cloudimagerepository.mapper.HashMapper;
import ru.polytech.cloudimagerepository.mapper.ImageMapper;
import ru.polytech.cloudimagerepository.model.Cluster;
import ru.polytech.cloudimagerepository.model.ImageData;
import ru.polytech.cloudimagerepository.repository.ClusterRepository;
import ru.polytech.cloudimagerepository.repository.GridFsRepository;
import ru.polytech.cloudimagerepository.service.ImageService;
import ru.polytech.cloudimagerepository.util.ImageUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ImageServiceImpl implements ImageService {

    private final double similarityScore;
    private final HashingAlgorithm hashingAlgorithm;
    private final GridFsRepository gridFsRepository;
    private final ClusterRepository clusterRepository;

    @Autowired
    public ImageServiceImpl(HashingAlgorithm hashingAlgorithm,
                            GridFsRepository gridFsRepository,
                            ClusterRepository clusterRepository) {
        similarityScore = .2;
        this.hashingAlgorithm = hashingAlgorithm;
        this.gridFsRepository = gridFsRepository;
        this.clusterRepository = clusterRepository;
    }

    @Override
    public ImageData uploadImage(MultipartFile file) throws IOException {
        validateMultipartFile(file);
        ImageData image = ImageMapper.toImage(file);
        Hash hash = hashingAlgorithm.hash(ImageUtil.createBufferedImageFromBytes(image.getImageData()));
        List<GridFSFile> files = gridFsRepository.findAllImagesMetadataByHashValue(hash.getHashValue().toString());

        boolean isNew = true;
        for (GridFSFile gridFSFile : files) {
            Document metadata = gridFSFile.getMetadata();
            if (metadata == null) {
                throw new ImageMetadataNotFoundException("Image metadata not found");
            }
            if (metadata.getString("_contentType").equals(file.getContentType())) {
                isNew = false;
                break;
            }
        }
        if (!isNew) {
            throw new ImageAlreadyExistsException("Image already exists");
        }

        Document metadata = new Document();
        metadata.append("hashValue", hash.getHashValue().toString());
        ObjectId id = gridFsRepository.storeImage(image, metadata);
        image.setId(id.toHexString());

        return image;
    }

    @Override
    public ImageData getImage(String imageId) throws IOException {
        GridFSFile gridFSFile = gridFsRepository.findImageMetadataByObjectId(validateObjectId(imageId))
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));
        if (gridFSFile.getMetadata() == null) {
            throw new ImageMetadataNotFoundException("Image metadata not found");
        }
        GridFsResource gridFsResource = gridFsRepository.findImageDataByGridFsFile(gridFSFile);
        return ImageMapper.toImage(gridFSFile, gridFsResource);
    }

    @Override
    public void deleteImage(String imageId) {
        ObjectId imageObjectId = validateObjectId(imageId);
        gridFsRepository.findImageMetadataByObjectId(imageObjectId)
                .orElseThrow(() -> new ImageNotFoundException("Image not found"));
        gridFsRepository.deleteImageById(validateObjectId(imageId));
        clusterRepository.removeImageFromClusters(imageId);
    }

    @Override
    public List<ImageData> findSimilarImages(ImageData image) throws IOException {
        Hash hash = hashingAlgorithm.hash(ImageUtil.createBufferedImageFromBytes(image.getImageData()));
        ObjectId id = findByHashValueOrStoreNew(hash, image);
        Set<ObjectId> targetImagesObjectIds = findSimilarImages(id, hash);

        List<ImageData> images = new ArrayList<>();
        List<GridFSFile> targetImagesMetadata = gridFsRepository.findAllImagesMetadataByIds(targetImagesObjectIds);
        for (GridFSFile gridFSFile : targetImagesMetadata) {
            images.add(ImageMapper.toImage(gridFSFile, gridFsRepository.findImageDataByGridFsFile(gridFSFile)));
        }

        return images;
    }

    @Override
    public List<ImageData> findSimilarImages(MultipartFile file) throws IOException {
        validateMultipartFile(file);
        ImageData image = ImageMapper.toImage(file);
        return findSimilarImages(image);
    }

    @Override
    public List<ImageData> findSimilarImages(String filename,
                                             String contentType,
                                             long contentSize,
                                             byte[] imageData) throws IOException {
        return findSimilarImages(new ImageData(filename, contentType, contentSize, imageData));
    }

    private void validateMultipartFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileIsEmptyException("Input file is empty");
        }
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new FileContentTypeNotFoundException("Input file's content type not found");
        }
        if (!contentType.startsWith("image/")) {
            throw new FileIsNotImageException("Input file is not MIME type image/");
        }
    }

    private ObjectId validateObjectId(String id) {
        try {
            return new ObjectId(id);
        } catch (IllegalArgumentException e) {
            throw new IncorrectIdFormatException("Incorrect id: " + e.getMessage());
        }
    }

    private ObjectId findByHashValueOrStoreNew(Hash hash, ImageData image) {
        ObjectId id;
        Document metadata = new Document();
        metadata.append("hashValue", hash.getHashValue().toString());
        List<GridFSFile> files = gridFsRepository.findAllImagesMetadataByHashValue(hash.getHashValue().toString());

        if (files.isEmpty()) {
            id = gridFsRepository.storeImage(image, metadata);
        } else {
            boolean isNew = true;
            ObjectId tempId = null;
            for (GridFSFile currentFile : files) {
                metadata = currentFile.getMetadata();
                if (metadata == null) {
                    throw new ImageMetadataNotFoundException("Image metadata not found");
                }
                if (metadata.getString("_contentType").equals(image.getContentType())) {
                    isNew = false;
                    tempId = currentFile.getObjectId();
                    break;
                }
            }
            if (isNew) {
                id = gridFsRepository.storeImage(image, metadata);
            } else {
                id = tempId;
            }
        }

        return id;
    }

    private Set<ObjectId> findSimilarImages(ObjectId id, Hash hash) {
        Set<String> targetImagesIds;
        Optional<Cluster> cluster = clusterRepository.findBySimilarImageIdsContaining(id.toHexString());

        if (cluster.isEmpty()) {
            Cluster targetCluster = null;
            try (MongoCursor<Document> cursor = clusterRepository.getCollection()) {
                while (cursor.hasNext()) {
                    Document currentClusterDoc = cursor.next();
                    Hash currentClusterHash = HashMapper.toHash(currentClusterDoc);
                    if (Double.compare(currentClusterHash.normalizedHammingDistance(hash), similarityScore) < 0) {
                        targetCluster = ClusterMapper.toCluster(currentClusterDoc);
                        break;
                    }
                }
            }
            if (targetCluster == null) {
                Cluster insertedCluster = clusterRepository
                        .saveCluster(ClusterMapper.toCluster(hash, Set.of(id.toHexString())));
                targetImagesIds = insertedCluster.getSimilarImagesIds();
            } else {
                Set<String> newSimilarImagesIds = targetCluster.getSimilarImagesIds();
                newSimilarImagesIds.add(id.toHexString());
                if (!clusterRepository.updateClusterSimilarImagesIds(
                        new ObjectId(targetCluster.getId()),
                        newSimilarImagesIds)) {
                    throw new ClusterUpdateException("Cluster update error.");
                }
                targetImagesIds = newSimilarImagesIds;
            }
        } else {
            targetImagesIds = cluster.get().getSimilarImagesIds();
        }

        return targetImagesIds.stream().map(ObjectId::new).collect(Collectors.toSet());
    }
}
