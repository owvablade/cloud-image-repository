package ru.polytech.cloudimagerepository.repository;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import ru.polytech.cloudimagerepository.model.Cluster;

import java.util.Optional;
import java.util.Set;

public interface ClusterRepository {

    Cluster saveCluster(Cluster cluster);

    boolean updateClusterSimilarImagesIds(ObjectId clusterId, Set<String> ids);

    Optional<Cluster> findBySimilarImageIdsContaining(String imageId);

    void removeImageFromClusters(String imageId);

    MongoCursor<Document> getCollection();
}
