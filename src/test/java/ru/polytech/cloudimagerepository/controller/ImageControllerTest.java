package ru.polytech.cloudimagerepository.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;
import ru.polytech.cloudimagerepository.exception.*;
import ru.polytech.cloudimagerepository.model.ImageData;
import ru.polytech.cloudimagerepository.service.ImageService;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ImageController.class)
class ImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    private ImageData image;
    private MockMultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        image = new ImageData("123456789123456789123456",
                "test.jpg",
                "image/jpeg",
                4L,
                "test".getBytes());
        multipartFile = new MockMultipartFile("file",
                "test.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test".getBytes());
    }

    @Test
    void testUploadValidImage() throws Exception {
        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(image);

        mockMvc.perform(multipart("/images/api").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123456789123456789123456"))
                .andExpect(jsonPath("$.filename").value("test.jpg"))
                .andExpect(jsonPath("$.contentType").value("image/jpeg"));
    }

    @Test
    void testUploadInvalidImageWithEmptyFile() throws Exception {
        MockMultipartFile emptyMultipartFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]);

        when(imageService.uploadImage(any(MultipartFile.class)))
                .thenThrow(new FileIsEmptyException("Input file is empty"));

        mockMvc.perform(multipart("/images/api").file(emptyMultipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadInvalidImageWithEmptyContentType() throws Exception {
        MockMultipartFile emptyMultipartFile = new MockMultipartFile(
                "file",
                "empty.txt",
                MediaType.TEXT_PLAIN_VALUE,
                "test".getBytes());

        when(imageService.uploadImage(any(MultipartFile.class)))
                .thenThrow(new FileIsNotImageException("Input file is not MIME type image/"));

        mockMvc.perform(multipart("/images/api").file(emptyMultipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUploadInvalidImageWithInvalidMimeType() throws Exception {
        MockMultipartFile emptyMultipartFile = new MockMultipartFile(
                "file",
                new byte[0]);

        when(imageService.uploadImage(any(MultipartFile.class)))
                .thenThrow(new FileContentTypeNotFoundException("Input file's content type not found"));

        mockMvc.perform(multipart("/images/api").file(emptyMultipartFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetImage() throws Exception {
        when(imageService.getImage("123456789123456789123456")).thenReturn(image);

        mockMvc.perform(get("/images/api/123456789123456789123456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("123456789123456789123456"))
                .andExpect(jsonPath("$.filename").value("test.jpg"))
                .andExpect(jsonPath("$.contentType").value("image/jpeg"));
    }

    @Test
    void testGetImageWithWrongId() throws Exception {
        when(imageService.getImage("123")).thenThrow(new IncorrectIdFormatException("Incorrect id format"));

        mockMvc.perform(get("/images/api/123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testShowImage() throws Exception {
        when(imageService.getImage("123456789123456789123456")).thenReturn(image);

        mockMvc.perform(get("/images/api/show/123456789123456789123456"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(image.getImageData()));
    }

    @Test
    void testShowImageWithWrongId() throws Exception {
        when(imageService.getImage("123")).thenThrow(new IncorrectIdFormatException("Incorrect id format"));

        mockMvc.perform(get("/images/api/show/123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteImage() throws Exception {
        doNothing().when(imageService).deleteImage(anyString());

        mockMvc.perform(delete("/images/api/1"))
                .andExpect(status().isOk());

        verify(imageService, times(1)).deleteImage("1");
    }

    void testDeleteImageWithWrongId() throws Exception {
        doThrow(new ImageNotFoundException("Image not found")).when(imageService).deleteImage(anyString());

        mockMvc.perform(delete("/images/api/1"))
                .andExpect(status().isBadRequest());

        verify(imageService, times(1)).deleteImage("1");
    }

    @Test
    void testFindSimilarImages() throws Exception {
        List<ImageData> similarImages = Collections.singletonList(image);

        when(imageService.findSimilarImages(any(MultipartFile.class))).thenReturn(similarImages);

        mockMvc.perform(multipart("/images/api/similar").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("123456789123456789123456"))
                .andExpect(jsonPath("$[0].filename").value("test.jpg"))
                .andExpect(jsonPath("$[0].contentType").value("image/jpeg"));
    }
}