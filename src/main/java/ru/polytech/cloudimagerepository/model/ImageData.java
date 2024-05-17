package ru.polytech.cloudimagerepository.model;

import java.util.Arrays;

public class ImageData {

    private String id;
    private String filename;
    private String contentType;
    private long contentSize;
    private byte[] imageData;

    public ImageData(String filename, String contentType, long contentSize, byte[] imageData) {
        this.filename = filename;
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.imageData = imageData;
    }

    public ImageData(String id, String filename, String contentType, long contentSize, byte[] imageData) {
        this.id = id;
        this.filename = filename;
        this.contentType = contentType;
        this.contentSize = contentSize;
        this.imageData = imageData;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public long getContentSize() {
        return contentSize;
    }

    public void setContentSize(long contentSize) {
        this.contentSize = contentSize;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    @Override
    public String toString() {
        return "Image{" +
                "id='" + id + '\'' +
                ", filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", contentSize=" + contentSize +
                ", imageData=" + Arrays.toString(imageData) +
                '}';
    }
}
