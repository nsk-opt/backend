package ru.nskopt.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.nskopt.App;
import ru.nskopt.entities.Category;
import ru.nskopt.entities.Cost;
import ru.nskopt.entities.Product;
import ru.nskopt.entities.image.Image;
import ru.nskopt.entities.requests.UpdateCategoryRequest;
import ru.nskopt.entities.user.Role;
import ru.nskopt.entities.user.User;
import ru.nskopt.mappers.CategoryMapper;
import ru.nskopt.repositories.CategoryRepository;
import ru.nskopt.repositories.ImageRepository;
import ru.nskopt.repositories.ProductRepository;
import ru.nskopt.repositories.UserRepository;
import ru.nskopt.utils.JwtUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class CategoryControllerTest {

  @Autowired MockMvc mvc;

  @Autowired CategoryRepository categoryRepository;
  @Autowired ImageRepository imageRepository;
  @Autowired UserRepository userRepository;

  final ObjectMapper objectMapper = new ObjectMapper();
  @Autowired PasswordEncoder passwordEncoder;
  @Autowired CategoryMapper categoryMapper;
  @Autowired JwtUtils jwtUtils;

  Category existsCategory;
  User admin;
  String adminToken;

  User manager;
  String managerToken;

  User user;
  String userToken;

  @Autowired private ProductRepository productRepository;

  void refillDb() {
    categoryRepository.deleteAll();
    //    productRepository
    userRepository.deleteAll();

    existsCategory = new Category();
    existsCategory.setName("Pants");

    categoryRepository.save(existsCategory);
  }

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
  void setup() {
    refillDb();
    createAdmin();
    createManager();
    createUser();
  }

  public Category getCategoryFromResponse(String response) throws JsonProcessingException {
    JsonNode jsonNode = objectMapper.readTree(response);

    Long id = jsonNode.get("id").asLong();
    String name = jsonNode.get("name").asText();

    Category category = new Category();
    category.setId(id);
    category.setName(name);

    return category;
  }

  @Nested
  @DisplayName("/api/categories -- Manager access test")
  class ManagerAccessTest {
    @Test
    void createCategory_success() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      Category sendCategory = categoryMapper.map(request);

      String responseString =
          mvc.perform(
                  post("/api/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + managerToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Category receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);
    }

    @Test
    void createCategory_success_2() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pan");

      Category sendCategory = categoryMapper.map(request);

      String responseString =
          mvc.perform(
                  post("/api/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + managerToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Category receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);
    }

    @Test
    void createCategory_success_3() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pangkjreodjtjed");

      Category sendCategory = categoryMapper.map(request);

      String responseString =
          mvc.perform(
                  post("/api/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + managerToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Category receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);
    }

    @Test
    void createCategory_nameTooShort() throws Exception {
      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pa");

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createCategory_nameTooLong() throws Exception {
      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pantsjh uhrwkdjre");

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateCategory_success() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      Category sendCategory = categoryMapper.map(request);

      String responseString =
          mvc.perform(
                  post("/api/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + managerToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Category receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);

      //
      //
      //

      request.setName("Sweater");

      sendCategory = categoryMapper.map(request);

      responseString =
          mvc.perform(
                  put("/api/categories/" + receiveCategory.getId())
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + managerToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);
    }

    @Test
    void updateCategory_notExists() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      mvc.perform(
              put("/api/categories/999")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCategory_success() throws Exception {
      mvc.perform(
              delete("/api/categories/" + existsCategory.getId())
                  .header("Authorization", "Bearer " + managerToken))
          .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_notExists() throws Exception {
      categoryRepository.deleteAll();

      mvc.perform(
              delete("/api/categories/" + existsCategory.getId())
                  .header("Authorization", "Bearer " + managerToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional
    void addImage_successful_one() throws Exception {
      Image image = new Image();
      image.setData("sample data".getBytes());
      imageRepository.save(image);

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(image.getId()))))
          .andExpect(status().isOk());

      assertEquals(1, category.getImages().size());
    }

    @Test
    @Transactional
    void addImage_successful_multiple() throws Exception {
      Image image1 = new Image();
      image1.setData("sample data first".getBytes());
      imageRepository.save(image1);

      Image image2 = new Image();
      image2.setData("sample data second".getBytes());
      imageRepository.save(image2);

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(
                      objectMapper.writeValueAsString(List.of(image1.getId(), image2.getId()))))
          .andExpect(status().isOk());

      assertEquals(2, category.getImages().size());
    }

    @Test
    @Transactional
    void addImage_not_exists_one() throws Exception {

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(1))))
          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void addImage_not_exists_one_and_one_exists() throws Exception {
      Image image = new Image();
      image.setData("sample image data".getBytes());

      Category category = new Category();
      category.setName("test category");
      category.getImages().add(image);
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(1, image.getId()))))
          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void addImage_not_exists_multiple() throws Exception {

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(1, 2, 3))))
          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteOrphanImage_successful() throws Exception {
      Image image = new Image();
      image.setData("sample data first".getBytes());
      imageRepository.save(image);

      Image orphanImage = new Image();
      orphanImage.setData("sample data second".getBytes());
      imageRepository.save(orphanImage);

      Category category = new Category();
      category.setName("test category");
      category.getImages().add(image);
      category.getImages().add(orphanImage);
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(image.getId()))))
          .andExpect(status().isOk());

      assertFalse(imageRepository.existsById(orphanImage.getId()));
    }
  }

  @Nested
  @DisplayName("/api/categories -- Admin access test")
  class AdminAccessTest {
    @Test
    void createCategory_success() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      Category sendCategory = categoryMapper.map(request);

      String responseString =
          mvc.perform(
                  post("/api/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + adminToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Category receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);
    }

    @Test
    void createCategory_success_2() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pan");

      Category sendCategory = categoryMapper.map(request);

      String responseString =
          mvc.perform(
                  post("/api/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + adminToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Category receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);
    }

    @Test
    void createCategory_success_3() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pangkjreodjtjed");

      Category sendCategory = categoryMapper.map(request);

      String responseString =
          mvc.perform(
                  post("/api/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + adminToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Category receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);
    }

    @Test
    void createCategory_nameTooShort() throws Exception {
      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pa");

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createCategory_nameTooLong() throws Exception {
      UpdateCategoryRequest request = new UpdateCategoryRequest();
      //noinspection SpellCheckingInspection
      request.setName("Pantsjh uhrwkdjre");

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void updateCategory_success() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      Category sendCategory = categoryMapper.map(request);

      String responseString =
          mvc.perform(
                  post("/api/categories")
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + adminToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isCreated())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      Category receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);

      //
      //
      //

      request.setName("Sweater");

      sendCategory = categoryMapper.map(request);

      responseString =
          mvc.perform(
                  put("/api/categories/" + receiveCategory.getId())
                      .contentType(MediaType.APPLICATION_JSON)
                      .header("Authorization", "Bearer " + adminToken)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(content().contentType(MediaType.APPLICATION_JSON))
              .andReturn()
              .getResponse()
              .getContentAsString();

      receiveCategory = getCategoryFromResponse(responseString);
      sendCategory.setId(receiveCategory.getId());

      assertEquals(receiveCategory, sendCategory);
    }

    @Test
    void updateCategory_notExists() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      mvc.perform(
              put("/api/categories/999")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void deleteCategory_success() throws Exception {
      mvc.perform(
              delete("/api/categories/" + existsCategory.getId())
                  .header("Authorization", "Bearer " + adminToken))
          .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_notExists() throws Exception {
      categoryRepository.deleteAll();

      mvc.perform(
              delete("/api/categories/" + existsCategory.getId())
                  .header("Authorization", "Bearer " + adminToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Transactional
    void addImage_successful_one() throws Exception {
      Image image = new Image();
      image.setData("sample data".getBytes());
      imageRepository.save(image);

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(image.getId()))))
          .andExpect(status().isOk());

      assertEquals(1, category.getImages().size());
    }

    @Test
    @Transactional
    void addImage_successful_multiple() throws Exception {
      Image image1 = new Image();
      image1.setData("sample data first".getBytes());
      imageRepository.save(image1);

      Image image2 = new Image();
      image2.setData("sample data second".getBytes());
      imageRepository.save(image2);

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(
                      objectMapper.writeValueAsString(List.of(image1.getId(), image2.getId()))))
          .andExpect(status().isOk());

      assertEquals(2, category.getImages().size());
    }

    @Test
    @Transactional
    void addImage_not_exists_one() throws Exception {

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(1))))
          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void addImage_not_exists_one_and_one_exists() throws Exception {
      Image image = new Image();
      image.setData("sample image data".getBytes());

      Category category = new Category();
      category.setName("test category");
      category.getImages().add(image);
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(1, image.getId()))))
          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void addImage_not_exists_multiple() throws Exception {

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(1, 2, 3))))
          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void deleteOrphanImage_successful() throws Exception {
      Image image = new Image();
      image.setData("sample data first".getBytes());
      imageRepository.save(image);

      Image orphanImage = new Image();
      orphanImage.setData("sample data second".getBytes());
      imageRepository.save(orphanImage);

      Category category = new Category();
      category.setName("test category");
      category.getImages().add(image);
      category.getImages().add(orphanImage);
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + managerToken)
                  .content(objectMapper.writeValueAsString(List.of(image.getId()))))
          .andExpect(status().isOk());

      assertFalse(imageRepository.existsById(orphanImage.getId()));
    }
  }

  @Nested
  @DisplayName("/api/categories -- User access test")
  class UserAccessTest {
    @Test
    void createCategory_success() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }

    @Test
    void createCategory_success_2() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pan");

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }

    @Test
    void createCategory_success_3() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pangkjreodjtjed");

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }

    @Test
    void createCategory_nameTooShort() throws Exception {
      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pa");

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    void createCategory_nameTooLong() throws Exception {
      UpdateCategoryRequest request = new UpdateCategoryRequest();

      mvc.perform(
              post("/api/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    void updateCategory_success() throws Exception {
      categoryRepository.deleteAll();

      Category category = new Category();
      category.setName("sample name");

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      category = categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }

    @Test
    void updateCategory_notExists() throws Exception {
      categoryRepository.deleteAll();

      UpdateCategoryRequest request = new UpdateCategoryRequest();
      request.setName("Pants");

      mvc.perform(
              put("/api/categories/999")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isForbidden());
    }

    @Test
    void deleteCategory_success() throws Exception {
      mvc.perform(
              delete("/api/categories/" + existsCategory.getId())
                  .header("Authorization", "Bearer " + userToken))
          .andExpect(status().isForbidden());
    }

    @Test
    void deleteCategory_notExists() throws Exception {
      categoryRepository.deleteAll();

      mvc.perform(
              delete("/api/categories/" + existsCategory.getId())
                  .header("Authorization", "Bearer " + userToken)
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void addImage_successful_one() throws Exception {
      Image image = new Image();
      image.setData("sample data".getBytes());
      imageRepository.save(image);

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(List.of(image.getId()))))
          .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void addImage_successful_multiple() throws Exception {
      Image image1 = new Image();
      image1.setData("sample data first".getBytes());
      imageRepository.save(image1);

      Image image2 = new Image();
      image2.setData("sample data second".getBytes());
      imageRepository.save(image2);

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(
                      objectMapper.writeValueAsString(List.of(image1.getId(), image2.getId()))))
          .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void addImage_not_exists_one() throws Exception {

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(List.of(1))))
          .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void addImage_not_exists_one_and_one_exists() throws Exception {
      Image image = new Image();
      image.setData("sample image data".getBytes());

      Category category = new Category();
      category.setName("test category");
      category.getImages().add(image);
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(List.of(1, image.getId()))))
          .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void addImage_not_exists_multiple() throws Exception {

      Category category = new Category();
      category.setName("test category");
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(List.of(1, 2, 3))))
          .andExpect(status().isForbidden());
    }

    @Test
    @Transactional
    void deleteOrphanImage_successful() throws Exception {
      Image image = new Image();
      image.setData("sample data first".getBytes());
      imageRepository.save(image);

      Image orphanImage = new Image();
      orphanImage.setData("sample data second".getBytes());
      imageRepository.save(orphanImage);

      Category category = new Category();
      category.setName("test category");
      category.getImages().add(image);
      category.getImages().add(orphanImage);
      categoryRepository.save(category);

      mvc.perform(
              put("/api/categories/" + category.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + userToken)
                  .content(objectMapper.writeValueAsString(List.of(image.getId()))))
          .andExpect(status().isForbidden());

      assertTrue(imageRepository.existsById(orphanImage.getId()));
    }
  }

  @Test
  void getProductsIdsByCategoryIdTest() throws Exception {
    Category category = new Category();
    category.setName("Свитшоты");
    category = categoryRepository.save(category);

    Product product = new Product();
    product.setName("Свиншот n1");
    product.setCategories(Set.of(category));
    product.setAvailability(30);
    product.setDescription("Описание свиншота n1");
    product.setCost(new Cost(BigDecimal.valueOf(300), BigDecimal.valueOf(900)));

    Product product2 = new Product();
    product2.setName("Свиншот n2");
    product2.setCategories(Set.of(category));
    product2.setAvailability(30);
    product2.setDescription("Описание свиншота n2");
    product2.setCost(new Cost(BigDecimal.valueOf(300), BigDecimal.valueOf(900)));

    product = productRepository.save(product);
    product2 = productRepository.save(product2);

    category.setProducts(Set.of(product, product2));
    categoryRepository.save(category);

    mvc.perform(get("/api/categories/" + category.getId() + "/products"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$.length()").value(2))
        .andExpect(
            jsonPath(
                "$",
                Matchers.containsInAnyOrder(
                    product.getId().intValue(), product2.getId().intValue())));
  }

  @Test
  void getAllCategories_notEmpty() throws Exception {
    mvc.perform(get("/api/categories").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$[0].id").value(existsCategory.getId()))
        .andExpect(jsonPath("$[0].name").value(existsCategory.getName()));
  }

  @Test
  void getAllCategories_empty() throws Exception {
    categoryRepository.deleteAll();

    mvc.perform(get("/api/categories").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isEmpty());
  }

  @Test
  void testGetCategoryById_exists() throws Exception {
    mvc.perform(
            get("/api/categories/" + existsCategory.getId())
                .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(existsCategory.getId()))
        .andExpect(jsonPath("$.name").value(existsCategory.getName()));
  }

  @Test
  void testGetCategoryById_notFound() throws Exception {
    categoryRepository.deleteAll();

    mvc.perform(get("/api/categories/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void getImages_successful_one() throws Exception {
    Image image = new Image();
    image.setData("sample data first".getBytes());
    imageRepository.save(image);

    Category category = new Category();
    category.setName("test category");
    category.getImages().add(image);
    categoryRepository.save(category);

    String response =
        mvc.perform(get("/api/categories/" + category.getId() + "/images"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertTrue(response.contains(String.valueOf(image.getId())));
  }

  @Test
  @Transactional
  void getImages_empty() throws Exception {
    Category category = new Category();
    category.setName("test category");
    categoryRepository.save(category);

    mvc.perform(get("/api/categories/" + category.getId() + "/images"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(0));
  }

  @Test
  @Transactional
  void getImages_successful_multiple() throws Exception {
    Image image1 = new Image();
    image1.setData("sample data first".getBytes());
    imageRepository.save(image1);

    Image image2 = new Image();
    image2.setData("sample data second".getBytes());
    imageRepository.save(image2);

    Category category = new Category();
    category.setName("test category");
    category.getImages().add(image1);
    category.getImages().add(image2);
    categoryRepository.save(category);

    String response =
        mvc.perform(get("/api/categories/" + category.getId() + "/images"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertTrue(response.contains(String.valueOf(image1.getId())));
    assertTrue(response.contains(String.valueOf(image2.getId())));
  }
}
