package ru.nskopt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nskopt.App;
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

    mvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value("Pants"));
  }

  @Test
  void createCategory_success_2() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pan");
    request.setImage(new ImageDto("https://imgur.com/testImage"));

    mvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value("Pan"));
  }

  @Test
  void createCategory_success_3() throws Exception {
    repository.deleteAll();

    UpdateCategoryRequest request = new UpdateCategoryRequest();
    request.setName("Pangkjreodjtjed");
    request.setImage(new ImageDto("https://imgur.com/testImage"));

    mvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.name").value(request.getName()));
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
}
