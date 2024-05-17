package ru.polytech.cloudimagerepository.repository.impl;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import ru.polytech.cloudimagerepository.model.ImageData;
import ru.polytech.cloudimagerepository.repository.GridFsRepository;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public class GridFsRepositoryImpl implements GridFsRepository {

    private final GridFsTemplate gridFsTemplate;

    @Autowired
    public GridFsRepositoryImpl(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @Override
    public ObjectId storeImage(ImageData image, Document metadata) {
        return gridFsTemplate.store(new ByteArrayInputStream(image.getImageData()),
                image.getFilename(),
                image.getContentType(),
                metadata);
    }

    @Override
    public Optional<GridFSFile> findImageMetadataByObjectId(ObjectId objectId) {
        return Optional.ofNullable(gridFsTemplate.findOne(new Query(Criteria.where("_id").is(objectId))));
    }

    @Override
    public Optional<GridFSFile> findImageMetadataByHashValue(String hashValue) {
        Criteria criteria = Criteria.where("metadata.hashValue").is(hashValue);
        return Optional.ofNullable(gridFsTemplate.findOne(new Query(criteria)));
    }

    @Override
    public List<GridFSFile> findAllImagesMetadataByHashValue(String hashValue) {
        Criteria criteria = Criteria.where("metadata.hashValue").is(hashValue);
        GridFSFindIterable gridFSFiles = gridFsTemplate.find(new Query(criteria));
        List<GridFSFile> result = new ArrayList<>();
        try (MongoCursor<GridFSFile> cursor = gridFSFiles.iterator()) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        }
        return result;
    }

    @Override
    public List<GridFSFile> findAllImagesMetadataByIds(Set<ObjectId> ids) {
        List<GridFSFile> result = new ArrayList<>();
        GridFSFindIterable gridFSFiles = gridFsTemplate.find(new Query(Criteria.where("_id").in(ids)));
        try (MongoCursor<GridFSFile> cursor = gridFSFiles.iterator()) {
            while (cursor.hasNext()) {
                result.add(cursor.next());
            }
        }
        return result;
    }

    @Override
    public GridFsResource findImageDataByGridFsFile(GridFSFile gridFSFile) {
        return gridFsTemplate.getResource(gridFSFile);
    }

    @Override
    public void deleteImageById(ObjectId objectId) {
        gridFsTemplate.delete(new Query(Criteria.where("_id").is(objectId)));
    }
}
