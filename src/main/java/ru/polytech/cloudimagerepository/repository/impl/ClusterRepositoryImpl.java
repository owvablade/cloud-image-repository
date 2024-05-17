package ru.polytech.cloudimagerepository.repository.impl;

import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import ru.polytech.cloudimagerepository.model.Cluster;
import ru.polytech.cloudimagerepository.repository.ClusterRepository;

import java.util.Optional;
import java.util.Set;

@Component
public class ClusterRepositoryImpl implements ClusterRepository {

    private final String collectionName;
    private final MongoTemplate mongoTemplate;

    public ClusterRepositoryImpl(@Value("${cluster-collection-name}") String collectionName,
                                 MongoTemplate mongoTemplate) {
        this.collectionName = collectionName;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Cluster saveCluster(Cluster cluster) {
        return mongoTemplate.insert(cluster, collectionName);
    }

    @Override
    public boolean updateClusterSimilarImagesIds(ObjectId clusterId, Set<String> ids) {
        Query query = new Query(Criteria.where("_id").is(clusterId));
        Update update = new Update().set("similarImagesIds", ids);
        return mongoTemplate.updateFirst(query, update, collectionName).getModifiedCount() > 0;
    }

    @Override
    public Optional<Cluster> findBySimilarImageIdsContaining(String imageId) {
        Criteria criteria = Criteria.where("similarImagesIds").elemMatch(Criteria.where("$eq").is(imageId));
        return Optional.ofNullable(mongoTemplate.findOne(new Query(criteria), Cluster.class, collectionName));
    }

    @Override
    public void removeImageFromClusters(String imageId) {
        Criteria criteria = Criteria.where("similarImagesIds").elemMatch(Criteria.where("$eq").is(imageId));
        mongoTemplate.updateMulti(new Query(criteria), new Update().pull("similarImagesIds", imageId), collectionName);
        mongoTemplate.remove(new Query(Criteria.where("similarImagesIds").size(0)), collectionName);
    }

    @Override
    public MongoCursor<Document> getCollection() {
        return mongoTemplate.getCollection(collectionName).find().iterator();
    }
}
