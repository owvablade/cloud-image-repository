package ru.polytech.cloudimagerepository.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.polytech.cloudimagerepository.model.ImageData;
import ru.polytech.cloudimagerepository.service.ImageService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/images/api")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping
    public ImageData uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return imageService.uploadImage(file);
    }

    @GetMapping("/{imageId}")
    public ImageData getImage(@PathVariable String imageId) throws IOException {
        return imageService.getImage(imageId);
    }

    @GetMapping("/show/{imageId}")
    public ResponseEntity<ByteArrayResource> showImage(@PathVariable String imageId) throws IOException {
        ImageData images = imageService.getImage(imageId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(images.getContentType()))
                .contentLength(images.getContentSize())
                .body(new ByteArrayResource(images.getImageData()));
    }

    @DeleteMapping("/{imageId}")
    public void deleteImage(@PathVariable String imageId) {
        imageService.deleteImage(imageId);
    }

    @GetMapping("/similar")
    public List<ImageData> findSimilarImages(@RequestParam("file") MultipartFile file) throws IOException {
        return imageService.findSimilarImages(file);
    }
}
