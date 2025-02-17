package ru.nskopt.controllers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.nskopt.App;
import ru.nskopt.models.dtos.ImageDto;
import ru.nskopt.models.entities.Cost;
import ru.nskopt.models.entities.Product;
import ru.nskopt.models.requests.UpdateProductRequest;
import ru.nskopt.repositories.ProductRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class ProductControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ProductRepository productRepository;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
  }

  private Product createProduct(
      String name,
      int availability,
      String description,
      BigDecimal wholesalePrice,
      BigDecimal retailPrice) {

    Product product = new Product();

    product.setName(name);
    product.setAvailability(availability);
    product.setDescription(description);
    product.setCost(new Cost(wholesalePrice, retailPrice));
    product.setImages(new HashSet<>());
    product.setCategories(new HashSet<>());

    return productRepository.save(product);
  }

  private UpdateProductRequest createRequest(
      String name,
      int availability,
      String description,
      BigDecimal wholesalePrice,
      BigDecimal retailPrice,
      Set<ImageDto> images) {

    UpdateProductRequest request = new UpdateProductRequest();

    request.setName(name);
    request.setAvailability(availability);
    request.setDescription(description);
    request.setCost(new Cost(wholesalePrice, retailPrice));
    request.setImages(images);

    return request;
  }

  private void expectCreatedProduct(UpdateProductRequest request, ResultMatcher httpStatus)
      throws Exception {
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(httpStatus)

        // info
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.availability").value(request.getAvailability()))
        .andExpect(jsonPath("$.description").value(request.getDescription()))

        // cost
        .andExpect(jsonPath("$.cost").exists())
        .andExpect(
            jsonPath("$.cost.wholesalePrice")
                .value(request.getCost().getWholesalePrice().doubleValue()))
        .andExpect(
            jsonPath("$.cost.retailPrice").value(request.getCost().getRetailPrice().doubleValue()))

        // images
        .andExpect(jsonPath("$.images").isArray())
        .andExpect(jsonPath("$.images.length()").value(request.getImages().size()))
        .andExpect(
            result -> {
              int i = 0;
              for (ImageDto image : request.getImages()) {
                jsonPath("$.images[" + i + "].link").value(image.getLink()).match(result);
                i++;
              }
            });
  }

  private void expectUpdatedProduct(Long id, UpdateProductRequest request)
      throws Exception {
    mockMvc
        .perform(
            put("/api/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())

        // info
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.availability").value(request.getAvailability()))
        .andExpect(jsonPath("$.description").value(request.getDescription()))

        // cost
        .andExpect(jsonPath("$.cost").exists())
        .andExpect(
            jsonPath("$.cost.wholesalePrice")
                .value(request.getCost().getWholesalePrice().doubleValue()))
        .andExpect(
            jsonPath("$.cost.retailPrice").value(request.getCost().getRetailPrice().doubleValue()))

        // images
        .andExpect(jsonPath("$.images").isArray())
        .andExpect(jsonPath("$.images.length()").value(request.getImages().size()))
        .andExpect(
            result -> {
              int i = 0;
              for (ImageDto image : request.getImages()) {
                jsonPath("$.images[" + i + "].link").value(image.getLink()).match(result);
                i++;
              }
            });
  }



  private void expectCreateBadRequest(UpdateProductRequest request) throws Exception {
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists());
  }

  private void expectUpdateBadRequest(Long id, UpdateProductRequest request) throws Exception {
    expectUpdateBadRequest(id, request, status().isBadRequest());
  }

  private void expectUpdateBadRequest(Long id, UpdateProductRequest request, ResultMatcher httpStatus) throws Exception {
    mockMvc
        .perform(
            put("/api/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(httpStatus)
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  void getAllProducts_empty() throws Exception {
    mockMvc
        .perform(get("/api/products").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
  }

  @Test
  void getAllProducts_exists() throws Exception {
    Product product =
        createProduct(
            "Test Product",
            10,
            "Test Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"));

    mockMvc
        .perform(get("/api/products").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].name").value(product.getName()))
        .andExpect(jsonPath("$[0].cost").exists())
        .andExpect(jsonPath("$[0].cost.wholesalePrice").value(100.0))
        .andExpect(jsonPath("$[0].cost.retailPrice").value(150.0));
  }

  @Test
  void getProductById_exists() throws Exception {
    Product product =
        createProduct(
            "Test Product",
            10,
            "Test Description",
            new BigDecimal("100.00"),
            new BigDecimal("150.00"));

    mockMvc
        .perform(get("/api/products/" + product.getId()).contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(product.getName()));
  }

  @Test
  void getProductById_empty() throws Exception {
    mockMvc
        .perform(get("/api/products/1").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.error").exists());
  }

  @Test
  void createProduct_Successful_1() throws Exception {
    UpdateProductRequest request =
        createRequest(
            "New Product",
            5,
            "New Description",
            new BigDecimal("200.00"),
            new BigDecimal("300.00"),
            Set.of(new ImageDto("https://imgur.com/image")));

    expectCreatedProduct(request, status().isCreated());
  }

  @Test
  void createProduct_Successful_2() throws Exception {
    UpdateProductRequest request =
        createRequest(
            "New Product",
            5,
            "New Description",
            new BigDecimal("0.00"),
            new BigDecimal("777372.00"),
            Set.of(new ImageDto("https://imgur.com/image")));

    expectCreatedProduct(request, status().isCreated());
  }

  @Test
  void createProduct_verification_invalid_cost_1() throws Exception {
    UpdateProductRequest request =
        createRequest(
            "New Product",
            5,
            "New Description",
            new BigDecimal("-200.00"),
            new BigDecimal("137.00"),
            new HashSet<>());

    expectCreateBadRequest(request);
  }

  @Test
  void createProduct_verification_invalid_cost_2() throws Exception {
    UpdateProductRequest request =
        createRequest(
            "New Product",
            5,
            "New Description",
            new BigDecimal("137.00"),
            new BigDecimal("-300.00"),
            new HashSet<>());

    expectCreateBadRequest(request);
  }

  @Test
  void createProduct_verification_zero_wholesale_price() throws Exception {
    UpdateProductRequest request =
        createRequest(
            "New Product",
            5,
            "New Description",
            new BigDecimal("0.00"),
            new BigDecimal("300.00"),
            Set.of(new ImageDto("https://imgur.com/image")));

    expectCreatedProduct(request, status().isCreated());
  }

  @Test
  void createProduct_verification_invalid_image_1() throws Exception {
    UpdateProductRequest request =
        createRequest(
            "New Product",
            5,
            "New Description",
            new BigDecimal("0.00"),
            new BigDecimal("300.00"),
            Set.of(new ImageDto("sftp://imgur.com/image")));

    expectCreateBadRequest(request);
  }

  @Test
  void createProduct_verification_invalid_image_2() throws Exception {
    UpdateProductRequest request =
        createRequest(
            "New Product",
            5,
            "New Description",
            new BigDecimal("0.00"),
            new BigDecimal("300.00"),
            Set.of(new ImageDto("imgur/image")));

    expectCreateBadRequest(request);
  }

  @Test
  void createProduct_verification_invalid_image_3() throws Exception {
    UpdateProductRequest request =
        createRequest(
            "New Product",
            5,
            "New Description",
            new BigDecimal("0.00"),
            new BigDecimal("300.00"),
            Set.of(new ImageDto("http://imgur.com/image")));

    expectCreateBadRequest(request);
  }

  @Test
  void updateProduct_notExists() throws Exception {
    UpdateProductRequest request =
    createRequest(
        "UPDATE PRODUCT",
        5,
        "New Description",
        new BigDecimal("0.00"),
        new BigDecimal("300.00"),
        Set.of(new ImageDto("https://imgur.com/image")));

    expectUpdateBadRequest(1L, request, status().isNotFound());
  }

  @Test
  void updateProduct_successful() throws Exception {
    Product product =
    createProduct(
        "Test Product",
        10,
        "Test Description",
        new BigDecimal("100.00"),
        new BigDecimal("150.00"));

    UpdateProductRequest request =
    createRequest(
        "UPDATE PROUCT",
        5,
        "New Description",
        new BigDecimal("0.00"),
        new BigDecimal("30.00"),
        Set.of(new ImageDto("https://imgur.com/image")));

    expectUpdatedProduct(product.getId(), request);

  }
 
}
