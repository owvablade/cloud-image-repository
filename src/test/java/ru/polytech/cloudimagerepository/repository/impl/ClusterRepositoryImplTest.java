package ru.polytech.cloudimagerepository.repository.impl;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.polytech.cloudimagerepository.model.Cluster;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class ClusterRepositoryImplTest {

    private final MongoTemplate mongoTemplate;

    private Cluster cluster;
    private ClusterRepositoryImpl clusterRepository;

    @Autowired
    public ClusterRepositoryImplTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @BeforeEach
    void setUp() {
        clusterRepository = new ClusterRepositoryImpl("testClusters", mongoTemplate);

        cluster = new Cluster("firstImageHashValue",
                "firstImageHashValue".length(),
                1,
                Set.of("firstImageHashValue", "secondImageHashValue"));
    }

    @Test
    void saveValidCluster() {
        Cluster insertedCluster = clusterRepository.saveCluster(cluster);

        assertAll(
                () -> assertNotNull(insertedCluster),
                () -> assertNotNull(insertedCluster.getId()),
                () -> assertEquals(cluster.getHashValue(), insertedCluster.getHashValue()),
                () -> assertEquals(cluster.getHashLength(), insertedCluster.getHashLength()),
                () -> assertEquals(cluster.getHashAlgorithmId(), insertedCluster.getHashAlgorithmId()),
                () -> assertEquals(cluster.getSimilarImagesIds(), insertedCluster.getSimilarImagesIds())
        );
    }

    @Test
    void saveInvalidCluster() {
        assertThrows(IllegalArgumentException.class, () -> clusterRepository.saveCluster(null));
    }

    @Test
    void updateClusterSimilarImagesIds() {
        Cluster insertedCluster = clusterRepository.saveCluster(cluster);

        boolean isAccepted = clusterRepository.updateClusterSimilarImagesIds(new ObjectId(insertedCluster.getId()),
                Set.of("firstImageHashValue", "secondImageHashValue", "thirdImageHashValue"));
        Optional<Cluster> newInsertedCluster = clusterRepository.findBySimilarImageIdsContaining("thirdImageHashValue");

        assertAll(
                () -> assertTrue(isAccepted),
                () -> assertTrue(newInsertedCluster.isPresent())
        );
    }

    @Test
    void updateClusterSimilarImagesValidIds() {
        clusterRepository.saveCluster(cluster);

        boolean isAccepted = clusterRepository.updateClusterSimilarImagesIds(
                new ObjectId("123456789123456789123456"),
                Set.of("firstImageHashValue", "secondImageHashValue", "thirdImageHashValue"));
        Optional<Cluster> newInsertedCluster = clusterRepository.findBySimilarImageIdsContaining("thirdImageHashValue");

        assertAll(
                () -> assertFalse(isAccepted),
                () -> assertTrue(newInsertedCluster.isEmpty())
        );
    }

    @Test
    void findBySimilarImageValidIdsContaining() {
        Cluster insertedCluster = clusterRepository.saveCluster(cluster);

        Optional<Cluster> result = clusterRepository.findBySimilarImageIdsContaining("secondImageHashValue");

        assertAll(
                () -> assertNotNull(insertedCluster.getId()),
                () -> assertTrue(result.isPresent())
        );
    }

    @Test
    void findBySimilarImageInvalidIdsContaining() {
        Cluster insertedCluster = clusterRepository.saveCluster(cluster);

        Optional<Cluster> result = clusterRepository.findBySimilarImageIdsContaining("yetAnotherImageHashValue");

        assertAll(
                () -> assertNotNull(insertedCluster.getId()),
                () -> assertFalse(result.isPresent())
        );
    }

    @Test
    void removeImageFromClustersByValidId() {
        clusterRepository.saveCluster(cluster);

        clusterRepository.removeImageFromClusters("firstImageHashValue");
        clusterRepository.removeImageFromClusters("secondImageHashValue");

        Optional<Cluster> firstCluster = clusterRepository.findBySimilarImageIdsContaining("firstImageHashValue");
        Optional<Cluster> secondCluster = clusterRepository.findBySimilarImageIdsContaining("secondImageHashValue");

        assertAll(
                () -> assertFalse(firstCluster.isPresent()),
                () -> assertFalse(secondCluster.isPresent())
        );
    }

    @Test
    void removeImageFromClustersByInvalidId() {
        clusterRepository.saveCluster(cluster);

        clusterRepository.removeImageFromClusters("yetYetAnotherImageHashValue");

        Optional<Cluster> firstCluster = clusterRepository.findBySimilarImageIdsContaining("firstImageHashValue");
        Optional<Cluster> secondCluster = clusterRepository.findBySimilarImageIdsContaining("secondImageHashValue");

        assertAll(
                () -> assertTrue(firstCluster.isPresent()),
                () -> assertTrue(secondCluster.isPresent())
        );
    }

    @Test
    void getCollection() {
        assertNotNull(clusterRepository.getCollection());
    }
}