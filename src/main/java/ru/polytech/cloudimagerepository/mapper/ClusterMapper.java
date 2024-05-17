package ru.polytech.cloudimagerepository.mapper;

import dev.brachtendorf.jimagehash.hash.Hash;
import org.bson.Document;
import ru.polytech.cloudimagerepository.model.Cluster;

import java.util.HashSet;
import java.util.Set;

public class ClusterMapper {

    public static Cluster toCluster(Hash hash, Set<String> ids) {
        return new Cluster(hash.getHashValue().toString(),
                hash.getBitResolution(),
                hash.getAlgorithmId(),
                ids);
    }

    public static Cluster toCluster(Document document) {
        return new Cluster(document.getObjectId("_id").toHexString(),
                document.getString("hashValue"),
                document.getInteger("hashLength"),
                document.getInteger("hashAlgorithmId"),
                new HashSet<>(document.getList("similarImagesIds", String.class)));
    }
}
