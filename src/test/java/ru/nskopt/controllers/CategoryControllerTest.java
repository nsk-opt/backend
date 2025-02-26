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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nskopt.App;
import ru.nskopt.mappers.CategoryMapper;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Image;
import ru.nskopt.models.requests.UpdateCategoryRequest;
import ru.nskopt.repositories.CategoryRepository;
import ru.nskopt.repositories.ImageRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class CategoryControllerTest {

  @Autowired private MockMvc mvc;

  @Autowired private CategoryRepository repository;

  @Autowired private ImageRepository imageRepository;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired CategoryMapper categoryMapper;

  private Category existsCategory;

  void refillDb() {
    repository.deleteAll();

    existsCategory = new Category();
    existsCategory.setName("Pants");

    repository.save(existsCategory);
  }

  @BeforeEach
  void setup() {
    refillDb();
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

  boolean isCategoriesEquals(String firstResponse, String secondResponse)
      throws JsonProcessingException {
    Category firstCategory = getCategoryFromResponse(firstResponse);
    Category secondCategory = getCategoryFromResponse(secondResponse);

    return firstCategory.equals(secondCategory);
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
    repository.deleteAll();

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
    repository.deleteAll();

    mvc.perform(get("/api/categories/999").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void createCategory_success() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pants");

    Category sendCategory = categoryMapper.map(request);

    String responseString =
        mvc.perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Category receiveCategory = getCategoryFromResponse(responseString);
    sendCategory.setId(receiveCategory.getId());

    assertTrue(receiveCategory.equals(sendCategory));
  }

  @Test
  void createCategory_success_2() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pan");

    Category sendCategory = categoryMapper.map(request);

    String responseString =
        mvc.perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Category receiveCategory = getCategoryFromResponse(responseString);
    sendCategory.setId(receiveCategory.getId());

    assertTrue(receiveCategory.equals(sendCategory));
  }

  @Test
  void createCategory_success_3() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pangkjreodjtjed");

    Category sendCategory = categoryMapper.map(request);

    String responseString =
        mvc.perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Category receiveCategory = getCategoryFromResponse(responseString);
    sendCategory.setId(receiveCategory.getId());

    assertTrue(receiveCategory.equals(sendCategory));
  }

  @Test
  void createCategory_nameTooShort() throws Exception {
    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pa");

    mvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
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
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  void updateCategory_success() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pants");

    Category sendCategory = categoryMapper.map(request);

    String responseString =
        mvc.perform(
                post("/api/categories")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    Category receiveCategory = getCategoryFromResponse(responseString);
    sendCategory.setId(receiveCategory.getId());

    assertTrue(receiveCategory.equals(sendCategory));

    //
    //
    //

    request.setName("Sweater");

    sendCategory = categoryMapper.map(request);

    responseString =
        mvc.perform(
                put("/api/categories/" + receiveCategory.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

    receiveCategory = getCategoryFromResponse(responseString);
    sendCategory.setId(receiveCategory.getId());

    assertTrue(receiveCategory.equals(sendCategory));
  }

  @Test
  void updateCategory_notExists() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pants");

    mvc.perform(
            put("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteCategory_success() throws Exception {
    mvc.perform(delete("/api/categories/" + existsCategory.getId()))
        .andExpect(status().isNoContent());
  }

  @Test
  void deleteCategory_notExists() throws Exception {
    repository.deleteAll();

    mvc.perform(
            delete("/api/categories/" + existsCategory.getId())
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
    repository.save(category);

    mvc.perform(
            put("/api/categories/" + category.getId() + "/images")
                .contentType(MediaType.APPLICATION_JSON)
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
    repository.save(category);

    mvc.perform(
            put("/api/categories/" + category.getId() + "/images")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(image1.getId(), image2.getId()))))
        .andExpect(status().isOk());

    assertEquals(2, category.getImages().size());
  }

  @Test
  @Transactional
  void addImage_not_exists_one() throws Exception {

    Category category = new Category();
    category.setName("test category");
    repository.save(category);

    mvc.perform(
            put("/api/categories/" + category.getId() + "/images")
                .contentType(MediaType.APPLICATION_JSON)
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
    repository.save(category);

    mvc.perform(
            put("/api/categories/" + category.getId() + "/images")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(1, image.getId()))))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  void addImage_not_exists_multiple() throws Exception {

    Category category = new Category();
    category.setName("test category");
    repository.save(category);

    mvc.perform(
            put("/api/categories/" + category.getId() + "/images")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(1, 2, 3))))
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
    repository.save(category);

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
    repository.save(category);

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
    repository.save(category);

    String response =
        mvc.perform(get("/api/categories/" + category.getId() + "/images"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertTrue(response.contains(String.valueOf(image1.getId())));
    assertTrue(response.contains(String.valueOf(image2.getId())));
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
    repository.save(category);

    mvc.perform(
            put("/api/categories/" + category.getId() + "/images")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(image.getId()))))
        .andExpect(status().isOk());

    assertFalse(imageRepository.existsById(orphanImage.getId()));
  }
}
