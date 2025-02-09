package ru.nskopt.controllers;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nskopt.App;
import ru.nskopt.mappers.CategoryMapper;
import ru.nskopt.models.dtos.ImageDto;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Image;
import ru.nskopt.models.requests.UpdateCategoryRequest;
import ru.nskopt.repositories.CategoryRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class CategoryControllerTest {

  @Autowired private MockMvc mvc;

  @Autowired private CategoryRepository repository;

  private ObjectMapper objectMapper = new ObjectMapper();

  @Autowired CategoryMapper categoryMapper;

  private Category existsCategory;

  void refillDb() {
    repository.deleteAll();

    existsCategory = new Category();
    existsCategory.setName("Pants");
    existsCategory.setImage(new Image(null, "https://imgur.com/testImage"));

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

    JsonNode imageNode = jsonNode.get("image");
    Image image = null;
    if (imageNode != null) {
      Long imageId = imageNode.get("id").asLong();
      String imageLink = imageNode.get("link").asText();
      image = new Image(imageId, imageLink);
    }

    Category category = new Category();
    category.setId(id);
    category.setName(name);
    category.setImage(image);

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
    request.setImage(new ImageDto("https://imgur.com/testImage"));

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
    sendCategory.getImage().setId(receiveCategory.getImage().getId());

    assertTrue(receiveCategory.equals(sendCategory));
  }

  @Test
  void createCategory_success_2() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pan");
    request.setImage(new ImageDto("https://imgur.com/testImage"));
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
    sendCategory.getImage().setId(receiveCategory.getImage().getId());

    assertTrue(receiveCategory.equals(sendCategory));
  }

  @Test
  void createCategory_success_3() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pangkjreodjtjed");
    request.setImage(new ImageDto("https://imgur.com/testImage"));

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
    sendCategory.getImage().setId(receiveCategory.getImage().getId());

    assertTrue(receiveCategory.equals(sendCategory));
  }

  @Test
  void createCategory_nameTooShort() throws Exception {
    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pa");
    request.setImage(new ImageDto("https://imgur.com/testImage"));

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
    request.setImage(new ImageDto("https://imgur.com/testImage"));

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
    request.setImage(new ImageDto("https://imgur.com/testImage"));

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
    sendCategory.getImage().setId(receiveCategory.getImage().getId());

    assertTrue(receiveCategory.equals(sendCategory));

    //
    //
    //

    request.setName("Sweater");
    request.setImage(new ImageDto("https://imgur.com/newImageLol"));

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
    sendCategory.getImage().setId(receiveCategory.getImage().getId());

    assertTrue(receiveCategory.equals(sendCategory));
  }

  @Test
  void updateCategory_notExists() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pants");
    request.setImage(new ImageDto("https://imgur.com/testImage"));

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
}
