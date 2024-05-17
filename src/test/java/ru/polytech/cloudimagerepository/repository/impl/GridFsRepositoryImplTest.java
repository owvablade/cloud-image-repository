package ru.polytech.cloudimagerepository.repository.impl;

import com.mongodb.MongoGridFSException;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.assertj.core.util.DateUtil;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.polytech.cloudimagerepository.model.ImageData;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class GridFsRepositoryImplTest {

    private final GridFsTemplate gridFsTemplate;
    private GridFsRepositoryImpl gridFsRepository;

    private ImageData firstImage;
    private ImageData secondImage;
    private Document firstDocument;
    private Document secondDocument;

    @Autowired
    public GridFsRepositoryImplTest(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    @BeforeEach
    void setUp() {
        gridFsRepository = new GridFsRepositoryImpl(gridFsTemplate);

        firstDocument = new Document().append("hashValue", "firstImageHashValue");
        firstImage = new ImageData("first", "image/jpg", 1, new byte[]{1});

        secondDocument = new Document().append("hashValue", "secondImageHashValue");
        secondImage = new ImageData("second", "image/png", 2, new byte[]{2});
    }

    @Test
    void storeValidImage() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageObjectId = gridFsRepository.storeImage(secondImage, secondDocument);

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageObjectId)
        );
    }

    @Test
    void storeNullImage() {
        assertThrows(NullPointerException.class, () -> gridFsRepository.storeImage(null, firstDocument));
    }

    @Test
    void findImageMetadataByValidObjectId() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageObjectId = gridFsRepository.storeImage(secondImage, secondDocument);

        Optional<GridFSFile> firstImageGridFsFile = gridFsRepository.findImageMetadataByObjectId(firstImageObjectId);
        Optional<GridFSFile> secondImageGridFsFile = gridFsRepository.findImageMetadataByObjectId(secondImageObjectId);

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageObjectId),
                () -> assertTrue(firstImageGridFsFile.isPresent()),
                () -> assertTrue(secondImageGridFsFile.isPresent())
        );
    }

    @Test
    void findImageMetadataByInvalidObjectId() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);

        Optional<GridFSFile> firstImageGridFsFile = gridFsRepository
                .findImageMetadataByObjectId(new ObjectId("123456789123456789123456"));

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertFalse(firstImageGridFsFile.isPresent())
        );
    }

    @Test
    void findImageMetadataByValidHashValue() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageObjectId = gridFsRepository.storeImage(secondImage, secondDocument);

        Optional<GridFSFile> firstImageGridFsFile = gridFsRepository
                .findImageMetadataByHashValue(firstDocument.getString("hashValue"));
        Optional<GridFSFile> secondImageGridFsFile = gridFsRepository
                .findImageMetadataByHashValue(secondDocument.getString("hashValue"));

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageObjectId),
                () -> assertTrue(firstImageGridFsFile.isPresent()),
                () -> assertTrue(secondImageGridFsFile.isPresent())
        );
    }

    @Test
    void findImageMetadataByInvalidHashValue() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);

        Optional<GridFSFile> firstImageGridFsFile = gridFsRepository
                .findImageMetadataByHashValue("testHashValue");

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertFalse(firstImageGridFsFile.isPresent())
        );
    }

    @Test
    void findAllImagesMetadataByValidHashValue() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageWithFirstImageHashValueObjectId = gridFsRepository.storeImage(secondImage, firstDocument);

        List<GridFSFile> result = gridFsRepository
                .findAllImagesMetadataByHashValue(firstDocument.getString("hashValue"));

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageWithFirstImageHashValueObjectId),
                () -> assertFalse(result.isEmpty())
        );
    }

    @Test
    void findAllImagesMetadataByInvalidHashValue() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageWithFirstImageHashValueObjectId = gridFsRepository.storeImage(secondImage, firstDocument);

        List<GridFSFile> result = gridFsRepository
                .findAllImagesMetadataByHashValue("testHashValue");

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageWithFirstImageHashValueObjectId),
                () -> assertTrue(result.isEmpty())
        );
    }

    @Test
    void findAllImagesMetadataByValidIds() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageObjectId = gridFsRepository.storeImage(secondImage, secondDocument);

        List<GridFSFile> result = gridFsRepository
                .findAllImagesMetadataByIds(Set.of(firstImageObjectId, secondImageObjectId));

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageObjectId),
                () -> assertFalse(result.isEmpty())
        );
    }

    @Test
    void findAllImagesMetadataByInvalidIds() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageObjectId = gridFsRepository.storeImage(secondImage, secondDocument);

        List<GridFSFile> result = gridFsRepository
                .findAllImagesMetadataByIds(Set.of(new ObjectId("123456789123456789123456")));

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageObjectId),
                () -> assertTrue(result.isEmpty())
        );
    }

    @Test
    void findImageDataByValidGridFsFile() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageObjectId = gridFsRepository.storeImage(secondImage, secondDocument);

        Optional<GridFSFile> firstImageGridFSFile = gridFsRepository.findImageMetadataByObjectId(firstImageObjectId);
        Optional<GridFSFile> secondImageGridFSFile = gridFsRepository.findImageMetadataByObjectId(secondImageObjectId);

        GridFsResource firstImageGridFsResource = gridFsRepository
                .findImageDataByGridFsFile(firstImageGridFSFile.orElseThrow());
        GridFsResource secondImageGridFsResource = gridFsRepository
                .findImageDataByGridFsFile(secondImageGridFSFile.orElseThrow());

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageObjectId),
                () -> assertNotNull(firstImageGridFsResource),
                () -> assertNotNull(secondImageGridFsResource)
        );
    }

    @Test
    void findImageDataByInvalidGridFsFile() {
        GridFSFile gridFSFile = new GridFSFile(
                new BsonObjectId(new ObjectId("123456789123456789123456")),
                "filename",
                10,
                1,
                DateUtil.now(),
                firstDocument);

        assertThrows(MongoGridFSException.class, () -> gridFsRepository.findImageDataByGridFsFile(gridFSFile));
    }

    @Test
    void deleteImageByValidId() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);
        ObjectId secondImageObjectId = gridFsRepository.storeImage(secondImage, secondDocument);

        gridFsRepository.deleteImageById(firstImageObjectId);

        Optional<GridFSFile> firstImageGridFSFile = gridFsRepository.findImageMetadataByObjectId(firstImageObjectId);
        Optional<GridFSFile> secondImageGridFSFile = gridFsRepository.findImageMetadataByObjectId(secondImageObjectId);

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertNotNull(secondImageObjectId),
                () -> assertTrue(firstImageGridFSFile.isEmpty()),
                () -> assertTrue(secondImageGridFSFile.isPresent())
        );
    }

    @Test
    void deleteImageByInvalidId() {
        ObjectId firstImageObjectId = gridFsRepository.storeImage(firstImage, firstDocument);

        gridFsRepository.deleteImageById(new ObjectId("123456789123456789123456"));

        Optional<GridFSFile> firstImageGridFSFile = gridFsRepository.findImageMetadataByObjectId(firstImageObjectId);

        assertAll(
                () -> assertNotNull(firstImageObjectId),
                () -> assertTrue(firstImageGridFSFile.isPresent())
        );
    }
}