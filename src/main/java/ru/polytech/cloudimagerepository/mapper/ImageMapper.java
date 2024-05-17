package ru.polytech.cloudimagerepository.mapper;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;
import ru.polytech.cloudimagerepository.model.ImageData;

import java.io.IOException;
import java.util.Objects;

public class ImageMapper {

    private static final String CONTENT_TYPE = "_contentType";

    public static ImageData toImage(MultipartFile file) throws IOException {
        return new ImageData(file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                file.getBytes());
    }

    public static ImageData toImage(GridFSFile gridFSFile, GridFsResource gridFsResource) throws IOException {
        return new ImageData(gridFSFile.getObjectId().toHexString(),
                gridFSFile.getFilename(),
                Objects.requireNonNull(gridFSFile.getMetadata()).getString(CONTENT_TYPE),
                gridFSFile.getLength(),
                gridFsResource.getContentAsByteArray());
    }
}
