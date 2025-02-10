package ru.nskopt.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.nskopt.App;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Product;
import ru.nskopt.repositories.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class ProductControllerTest {

  @Autowired private MockMvc mvc;

  @Autowired private ProductRepository repository;

  private ObjectMapper objectMapper = new ObjectMapper();

  private Category existsProduct;

  void refillDb() {
    repository.deleteAll();

    // Cost cost = new Cost();

    Product product = new Product(null, "куртка", null, 0, null, null, null);
  }

  @BeforeEach
  void setup() {
    refillDb();
  }

  // @Test
  // public void getAllProducts() throws Exception {
  // this.mockMvc.perform(get("/api/products")).andExpect(status().isOk())
  // .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
  // .andExpect(jsonPath("$[0].id").value("<value>"))
  // .andExpect(jsonPath("$[0].name").value("<value>"))
  // .andExpect(jsonPath("$[0].cost").value("<value>"))
  // .andExpect(jsonPath("$[0].availability").value("<value>"))
  // .andExpect(jsonPath("$[0].description").value("<value>"))
  // .andExpect(jsonPath("$[0].images").value("<value>"))
  // .andExpect(jsonPath("$[0].categories").value("<value>"));
  // }
}
