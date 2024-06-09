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
        image = new ImageData("1",
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
    void testUploadImage() throws Exception {
        when(imageService.uploadImage(any(MultipartFile.class))).thenReturn(image);

        mockMvc.perform(multipart("/images/api").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.filename").value("test.jpg"))
                .andExpect(jsonPath("$.contentType").value("image/jpeg"));
    }

    @Test
    void testGetImage() throws Exception {
        when(imageService.getImage("1")).thenReturn(image);

        mockMvc.perform(get("/images/api/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.filename").value("test.jpg"))
                .andExpect(jsonPath("$.contentType").value("image/jpeg"));
    }

    @Test
    void testShowImage() throws Exception {
        when(imageService.getImage("1")).thenReturn(image);

        mockMvc.perform(get("/images/api/show/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG_VALUE))
                .andExpect(content().bytes(image.getImageData()));
    }

    @Test
    void testDeleteImage() throws Exception {
        doNothing().when(imageService).deleteImage(anyString());

        mockMvc.perform(delete("/images/api/1"))
                .andExpect(status().isOk());

        verify(imageService, times(1)).deleteImage("1");
    }

    @Test
    void testFindSimilarImages() throws Exception {
        List<ImageData> similarImages = Collections.singletonList(image);

        when(imageService.findSimilarImages(any(MultipartFile.class))).thenReturn(similarImages);

        mockMvc.perform(multipart("/images/api/similar").file(multipartFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].filename").value("test.jpg"))
                .andExpect(jsonPath("$[0].contentType").value("image/jpeg"));
    }
}