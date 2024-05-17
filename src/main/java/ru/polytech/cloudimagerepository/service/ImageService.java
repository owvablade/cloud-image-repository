package ru.polytech.cloudimagerepository.service;

import org.springframework.web.multipart.MultipartFile;
import ru.polytech.cloudimagerepository.model.ImageData;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    ImageData uploadImage(MultipartFile file) throws IOException;

    ImageData getImage(String imageId) throws IOException;

    void deleteImage(String imageId);

    List<ImageData> findSimilarImages(ImageData image) throws IOException;

    List<ImageData> findSimilarImages(MultipartFile file) throws IOException;

    List<ImageData> findSimilarImages(String filename, String contentType, long contentSize, byte[] imageData) throws IOException;
}
