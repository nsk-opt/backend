package ru.nskopt.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import ru.nskopt.App;
import ru.nskopt.repositories.ImageRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class ImageControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ImageRepository imageRepository;

  @BeforeEach
  void beforeEach() {
    imageRepository.deleteAll();
  }

  @Test
  void createImage_successful_1() throws Exception {
    String filePath = "src/test/resources/images/image.jpg";
    File file = new File(filePath);

    byte[] fileContent = Files.readAllBytes(file.toPath());

    MockMultipartFile multipartFile =
        new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, fileContent);

    String responseContent =
        mockMvc
            .perform(multipart("/api/images").file(multipartFile))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Long imageId = Long.parseLong(responseContent);

    assertTrue(imageRepository.findById(imageId).isPresent());
  }

  @Test
  void createImage_successful_2() throws Exception {
    String filePath = "src/test/resources/images/image.png";
    File file = new File(filePath);

    byte[] fileContent = Files.readAllBytes(file.toPath());

    MockMultipartFile multipartFile =
        new MockMultipartFile("file", file.getName(), MediaType.IMAGE_PNG_VALUE, fileContent);

    String responseContent =
        mockMvc
            .perform(multipart("/api/images").file(multipartFile))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    Long imageId = Long.parseLong(responseContent);

    assertTrue(imageRepository.findById(imageId).isPresent());
  }
}
