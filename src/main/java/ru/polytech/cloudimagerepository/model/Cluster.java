package ru.polytech.cloudimagerepository.model;

import java.util.Set;

public class Cluster {

    private String id;
    private String hashValue;
    private Integer hashLength;
    private Integer hashAlgorithmId;
    private Set<String> similarImagesIds;

    public Cluster() {
    }

    public Cluster(String hashValue, Integer hashLength, Integer hashAlgorithmId, Set<String> similarImagesIds) {
        this.hashValue = hashValue;
        this.hashLength = hashLength;
        this.hashAlgorithmId = hashAlgorithmId;
        this.similarImagesIds = similarImagesIds;
    }

    public Cluster(String id,
                   String hashValue,
                   Integer hashLength,
                   Integer hashAlgorithmId,
                   Set<String> similarImagesIds) {
        this.id = id;
        this.hashValue = hashValue;
        this.hashLength = hashLength;
        this.hashAlgorithmId = hashAlgorithmId;
        this.similarImagesIds = similarImagesIds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHashValue() {
        return hashValue;
    }

    public void setHashValue(String hashValue) {
        this.hashValue = hashValue;
    }

    public Integer getHashLength() {
        return hashLength;
    }

    public void setHashLength(Integer hashLength) {
        this.hashLength = hashLength;
    }

    public Integer getHashAlgorithmId() {
        return hashAlgorithmId;
    }

    public void setHashAlgorithmId(Integer hashAlgorithmId) {
        this.hashAlgorithmId = hashAlgorithmId;
    }

    public Set<String> getSimilarImagesIds() {
        return similarImagesIds;
    }

    public void setSimilarImagesIds(Set<String> similarImagesIds) {
        this.similarImagesIds = similarImagesIds;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "id='" + id + '\'' +
                ", hashValue='" + hashValue + '\'' +
                ", hashLength=" + hashLength +
                ", hashAlgorithmId=" + hashAlgorithmId +
                ", similarImagesIds=" + similarImagesIds +
                '}';
    }
}
