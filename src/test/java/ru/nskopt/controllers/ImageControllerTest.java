package ru.nskopt.controllers;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.nio.file.Files;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.nskopt.App;
import ru.nskopt.entities.image.Image;
import ru.nskopt.entities.user.Role;
import ru.nskopt.entities.user.User;
import ru.nskopt.repositories.ImageRepository;
import ru.nskopt.repositories.UserRepository;
import ru.nskopt.utils.JwtUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class ImageControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired PasswordEncoder passwordEncoder;
  @Autowired JwtUtils jwtUtils;

  @Autowired ImageRepository imageRepository;
  @Autowired UserRepository userRepository;

  User admin;
  String adminToken;

  User manager;
  String managerToken;

  User user;
  String userToken;

  void createAdmin() {
    User user = new User();
    user.setUsername("username123213");
    user.setPassword(passwordEncoder.encode("footerNeck8273te"));
    user.setRole(Role.ROLE_ADMIN);

    admin = userRepository.save(user);
    adminToken = jwtUtils.generateToken(user);
  }

  void createManager() {
    User user = new User();
    user.setUsername("manager123896192836");
    user.setPassword(passwordEncoder.encode("footer322Neck8273te"));
    user.setRole(Role.ROLE_ADMIN);

    manager = userRepository.save(user);
    managerToken = jwtUtils.generateToken(user);
  }

  void createUser() {
    User user = new User();
    user.setUsername("user123896192836");
    user.setPassword(passwordEncoder.encode("footer322Neck8273te"));
    user.setRole(Role.ROLE_USER);

    this.user = userRepository.save(user);
    this.userToken = jwtUtils.generateToken(user);
  }

  @BeforeEach
  void beforeEach() {
    imageRepository.deleteAll();
    userRepository.deleteAll();
    createAdmin();
    createManager();
    createUser();
  }

  @Nested
  @DisplayName("/api/images -- Admin access test")
  class AdminAccessTest {
    @Test
    void createImage_successful_jpg() throws Exception {
      String filePath = "src/test/resources/images/image.jpg";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, fileContent);

      String responseContent =
          mockMvc
              .perform(
                  multipart("/api/images")
                      .file(multipartFile)
                      .header("Authorization", "Bearer " + adminToken))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      Long imageId = Long.parseLong(responseContent);

      assertTrue(imageRepository.findById(imageId).isPresent());
    }

    @Test
    void createImage_successful_png() throws Exception {
      String filePath = "src/test/resources/images/image.png";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), MediaType.IMAGE_PNG_VALUE, fileContent);

      String responseContent =
          mockMvc
              .perform(
                  multipart("/api/images")
                      .file(multipartFile)
                      .header("Authorization", "Bearer " + adminToken))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      Long imageId = Long.parseLong(responseContent);

      assertTrue(imageRepository.findById(imageId).isPresent());
    }

    @Test
    void createImage_successful_webp() throws Exception {
      String filePath = "src/test/resources/images/image.webp";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), "image/webp", fileContent);

      String responseContent =
          mockMvc
              .perform(
                  multipart("/api/images")
                      .file(multipartFile)
                      .header("Authorization", "Bearer " + adminToken))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      Long imageId = Long.parseLong(responseContent);

      assertTrue(imageRepository.findById(imageId).isPresent());
    }

    @Test
    void createImage_unsupported_format() throws Exception {
      String filePath = "src/test/resources/images/image.gif";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), "image/gif", fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + adminToken))
          .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createImage_unsupported_format_null_content_type() throws Exception {
      String filePath = "src/test/resources/images/image.gif";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), null, fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + adminToken))
          .andExpect(status().isUnsupportedMediaType());
    }
  }

  @Nested
  @DisplayName("/api/images -- Manager access test")
  class ManagerAccessTest {
    @Test
    void createImage_successful_jpg() throws Exception {
      String filePath = "src/test/resources/images/image.jpg";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, fileContent);

      String responseContent =
          mockMvc
              .perform(
                  multipart("/api/images")
                      .file(multipartFile)
                      .header("Authorization", "Bearer " + managerToken))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      Long imageId = Long.parseLong(responseContent);

      assertTrue(imageRepository.findById(imageId).isPresent());
    }

    @Test
    void createImage_successful_png() throws Exception {
      String filePath = "src/test/resources/images/image.png";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), MediaType.IMAGE_PNG_VALUE, fileContent);

      String responseContent =
          mockMvc
              .perform(
                  multipart("/api/images")
                      .file(multipartFile)
                      .header("Authorization", "Bearer " + managerToken))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      Long imageId = Long.parseLong(responseContent);

      assertTrue(imageRepository.findById(imageId).isPresent());
    }

    @Test
    void createImage_successful_webp() throws Exception {
      String filePath = "src/test/resources/images/image.webp";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), "image/webp", fileContent);

      String responseContent =
          mockMvc
              .perform(
                  multipart("/api/images")
                      .file(multipartFile)
                      .header("Authorization", "Bearer " + managerToken))
              .andExpect(status().isOk())
              .andReturn()
              .getResponse()
              .getContentAsString();

      Long imageId = Long.parseLong(responseContent);

      assertTrue(imageRepository.findById(imageId).isPresent());
    }

    @Test
    void createImage_unsupported_format() throws Exception {
      String filePath = "src/test/resources/images/image.gif";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), "image/gif", fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + managerToken))
          .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void createImage_unsupported_format_null_content_type() throws Exception {
      String filePath = "src/test/resources/images/image.gif";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), null, fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + managerToken))
          .andExpect(status().isUnsupportedMediaType());
    }
  }

  @Nested
  @DisplayName("/api/images -- User access test")
  class UserAccessTest {
    @Test
    void createImage_successful_jpg() throws Exception {
      String filePath = "src/test/resources/images/image.jpg";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), MediaType.IMAGE_JPEG_VALUE, fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + userToken))
          .andExpect(status().isForbidden());
    }

    @Test
    void createImage_successful_png() throws Exception {
      String filePath = "src/test/resources/images/image.png";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), MediaType.IMAGE_PNG_VALUE, fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + userToken))
          .andExpect(status().isForbidden());
    }

    @Test
    void createImage_successful_webp() throws Exception {
      String filePath = "src/test/resources/images/image.webp";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), "image/webp", fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + userToken))
          .andExpect(status().isForbidden());
    }

    @Test
    void createImage_unsupported_format() throws Exception {
      String filePath = "src/test/resources/images/image.gif";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), "image/gif", fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + userToken))
          .andExpect(status().isForbidden());
    }

    @Test
    void createImage_unsupported_format_null_content_type() throws Exception {
      String filePath = "src/test/resources/images/image.gif";
      File file = new File(filePath);

      byte[] fileContent = Files.readAllBytes(file.toPath());

      MockMultipartFile multipartFile =
          new MockMultipartFile("file", file.getName(), null, fileContent);

      mockMvc
          .perform(
              multipart("/api/images")
                  .file(multipartFile)
                  .header("Authorization", "Bearer " + userToken))
          .andExpect(status().isForbidden());
    }
  }

  @Test
  void getImage_successful() throws Exception {
    Image image = new Image();
    image.setData("data".getBytes());

    imageRepository.save(image);

    byte[] result =
        mockMvc
            .perform(get("/api/images/" + image.getId()).contentType("image/webp"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsByteArray();

    assertArrayEquals(result, image.getData());
  }

  @Test
  void getImage_not_found() throws Exception {
    mockMvc
        .perform(get("/api/images/" + 1).contentType("image/webp"))
        .andExpect(status().isNotFound());
  }
}
