package ru.polytech.cloudimagerepository.repository;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import ru.polytech.cloudimagerepository.model.ImageData;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GridFsRepository {

    ObjectId storeImage(ImageData image, Document metadata);

    Optional<GridFSFile> findImageMetadataByObjectId(ObjectId objectId);

    Optional<GridFSFile> findImageMetadataByHashValue(String hashValue);

    List<GridFSFile> findAllImagesMetadataByHashValue(String hashValue);

    List<GridFSFile> findAllImagesMetadataByIds(Set<ObjectId> ids);

    GridFsResource findImageDataByGridFsFile(GridFSFile gridFSFile);

    void deleteImageById(ObjectId objectId);
}
