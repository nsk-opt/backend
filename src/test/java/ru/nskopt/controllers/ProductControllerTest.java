package ru.nskopt.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
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
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.transaction.annotation.Transactional;
import ru.nskopt.App;
import ru.nskopt.dto.product.ProductUpdateRequest;
import ru.nskopt.entities.Category;
import ru.nskopt.entities.Cost;
import ru.nskopt.entities.Product;
import ru.nskopt.entities.image.Image;
import ru.nskopt.entities.user.Role;
import ru.nskopt.entities.user.User;
import ru.nskopt.repositories.CategoryRepository;
import ru.nskopt.repositories.ImageRepository;
import ru.nskopt.repositories.ProductRepository;
import ru.nskopt.repositories.UserRepository;
import ru.nskopt.utils.JwtUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class ProductControllerTest {

  @Autowired MockMvc mockMvc;

  @Autowired ProductRepository productRepository;
  @Autowired CategoryRepository categoryRepository;
  @Autowired ImageRepository imageRepository;
  @Autowired UserRepository userRepository;
  @Autowired JwtUtils jwtUtils;
  @Autowired PasswordEncoder passwordEncoder;

  @Autowired ObjectMapper objectMapper;

  User admin;
  String adminToken;

  void createAdmin() {
    User user = new User();
    user.setUsername("username123213");
    user.setPassword(passwordEncoder.encode("footerNeck8273te"));
    user.setRole(Role.ROLE_ADMIN);

    admin = userRepository.save(user);
    adminToken = jwtUtils.generateToken(user);
  }

  @BeforeEach
  void setUp() {
    productRepository.deleteAll();
    userRepository.deleteAll();
    createAdmin();
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

    return productRepository.save(product);
  }

  private ProductUpdateRequest createRequest(
      String name, int availability, BigDecimal wholesalePrice, BigDecimal retailPrice) {

    ProductUpdateRequest request = new ProductUpdateRequest();

    request.setName(name);
    request.setAvailability(availability);
    request.setDescription("New Description");
    request.setCost(new Cost(wholesalePrice, retailPrice));

    return request;
  }

  private void expectCreatedProduct(ProductUpdateRequest request, ResultMatcher httpStatus)
      throws Exception {
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(httpStatus)
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.availability").value(request.getAvailability()))
        .andExpect(jsonPath("$.description").value(request.getDescription()))
        .andExpect(jsonPath("$.price").value(request.getCost().getRetailPrice().doubleValue()));
  }

  private void expectUpdatedProduct(Long id, ProductUpdateRequest request) throws Exception {
    mockMvc
        .perform(
            put("/api/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())

        // info
        .andExpect(jsonPath("$.name").value(request.getName()))
        .andExpect(jsonPath("$.availability").value(request.getAvailability()))
        .andExpect(jsonPath("$.description").value(request.getDescription()))
        .andExpect(jsonPath("$.price").value(request.getCost().getRetailPrice().doubleValue()));
  }

  private void expectSuccessfulProductDeletion(Long id) throws Exception {
    mockMvc
        .perform(delete("/api/products/" + id).header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isNoContent());

    mockMvc
        .perform(get("/api/products/" + id).header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isNotFound());
  }

  private void expectNotFoundProductDeletion(Long id) throws Exception {
    mockMvc
        .perform(delete("/api/products/" + id).header("Authorization", "Bearer " + adminToken))
        .andExpect(status().isNotFound());
  }

  private void expectCreateBadRequest(ProductUpdateRequest request) throws Exception {
    mockMvc
        .perform(
            post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error").exists());
  }

  private void expectUpdateBadRequest(Long id, ProductUpdateRequest request) throws Exception {
    expectUpdateBadRequest(id, request, status().isBadRequest());
  }

  private void expectUpdateBadRequest(
      Long id, ProductUpdateRequest request, ResultMatcher httpStatus) throws Exception {
    mockMvc
        .perform(
            put("/api/products/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + adminToken)
                .content(objectMapper.writeValueAsString(request)))
        .andExpect(httpStatus)
        .andExpect(jsonPath("$.error").exists());
  }

  @Nested
  @DisplayName("/api/products -- Admin access test")
  class AdminAccessTest {
    @Test
    void createProduct_Successful_1() throws Exception {
      ProductUpdateRequest request =
          createRequest("New Product", 5, new BigDecimal("200.00"), new BigDecimal("300.00"));

      expectCreatedProduct(request, status().isCreated());
    }

    @Test
    void createProduct_Successful_2() throws Exception {
      ProductUpdateRequest request =
          createRequest("New Product", 5, new BigDecimal("0.00"), new BigDecimal("777372.00"));

      expectCreatedProduct(request, status().isCreated());
    }

    @Test
    void createProduct_verification_invalid_cost_1() throws Exception {
      ProductUpdateRequest request =
          createRequest("New Product", 5, new BigDecimal("-200.00"), new BigDecimal("137.00"));

      expectCreateBadRequest(request);
    }

    @Test
    void createProduct_verification_invalid_cost_2() throws Exception {
      ProductUpdateRequest request =
          createRequest("New Product", 5, new BigDecimal("137.00"), new BigDecimal("-300.00"));

      expectCreateBadRequest(request);
    }

    @Test
    void createProduct_verification_zero_wholesale_price() throws Exception {
      ProductUpdateRequest request =
          createRequest("New Product", 5, new BigDecimal("0.00"), new BigDecimal("300.00"));

      expectCreatedProduct(request, status().isCreated());
    }

    @Test
    void updateProduct_notExists() throws Exception {
      ProductUpdateRequest request =
          createRequest("UPDATE PRODUCT", 5, new BigDecimal("0.00"), new BigDecimal("300.00"));

      expectUpdateBadRequest(1L, request, status().isNotFound());
    }

    @Test
    void updateProduct_successful_1() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      ProductUpdateRequest request =
          createRequest("UPDATE PRODUCT", 5, new BigDecimal("0.00"), new BigDecimal("30.00"));

      expectUpdatedProduct(product.getId(), request);
    }

    @Test
    void updateProduct_successful_2() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      ProductUpdateRequest request =
          createRequest("UPDATE PRODUCT", 4, new BigDecimal("154.00"), new BigDecimal("30.00"));

      expectUpdatedProduct(product.getId(), request);
    }

    @Test
    void updateProduct_successful_3() throws Exception {
      Product product =
          createProduct(
              "pants gucci",
              1,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      ProductUpdateRequest request =
          createRequest("pants versace", 4, new BigDecimal("154.00"), new BigDecimal("30.00"));

      expectUpdatedProduct(product.getId(), request);
    }

    @Test
    void updateProduct_verification_invalid_name() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      ProductUpdateRequest request =
          createRequest(
              "UPDATE PRODUCT TOO LONG NAME..", 5, new BigDecimal("0.00"), new BigDecimal("30.00"));

      expectUpdateBadRequest(product.getId(), request);
    }

    @Test
    void updateProduct_verification_invalid_cost_1() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      ProductUpdateRequest request =
          createRequest(
              "UPDATE PRODUCT TOO LONG NAME..",
              5,
              new BigDecimal("-30.00"),
              new BigDecimal("30.00"));

      expectUpdateBadRequest(product.getId(), request);
    }

    @Test
    void updateProduct_verification_invalid_cost_2() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      ProductUpdateRequest request =
          createRequest(
              "UPDATE PRODUCT TOO LONG NAME..",
              5,
              new BigDecimal("-30.00"),
              new BigDecimal("-30.00"));

      expectUpdateBadRequest(product.getId(), request);
    }

    @Test
    void updateProduct_verification_invalid_cost_3() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      ProductUpdateRequest request =
          createRequest(
              "UPDATE PRODUCT TOO LONG NAME..",
              5,
              new BigDecimal("30.00"),
              new BigDecimal("-30.00"));

      expectUpdateBadRequest(product.getId(), request);
    }

    @Test
    void deleteProduct_successful() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      expectSuccessfulProductDeletion(product.getId());
    }

    @Test
    void deleteProduct_notFound() throws Exception {
      expectNotFoundProductDeletion(444L);
    }

    @Test
    void deleteProduct_notFound_2() throws Exception {
      expectNotFoundProductDeletion(447L);
    }

    @Test
    @Transactional
    void updateCategories_successful() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      Category category1 = new Category();
      category1.setName("Category 1");
      categoryRepository.save(category1);

      Category category2 = new Category();
      category2.setName("Category 2");
      categoryRepository.save(category2);

      Category category3 = new Category();
      category3.setName("Category 3");
      categoryRepository.save(category3);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(
                      objectMapper.writeValueAsString(
                          List.of(category1.getId(), category2.getId(), category3.getId()))))
          .andExpect(status().isOk());

      Product updatedProduct =
          productRepository.findById(product.getId()).orElseThrow(Exception::new);
      Set<Category> categories = updatedProduct.getCategories();

      assertTrue(categories.contains(category1));
      assertTrue(categories.contains(category2));
      assertTrue(categories.contains(category3));
      assertEquals(3, categories.size());
    }

    @Test
    @Transactional
    void updateCategories_successful_1() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      Category category1 = new Category();
      category1.setName("Category 1");
      categoryRepository.save(category1);

      Category category2 = new Category();
      category2.setName("Category 2");
      categoryRepository.save(category2);

      Category category3 = new Category();
      category3.setName("Category 3");
      categoryRepository.save(category3);

      product.getCategories().addAll(List.of(category1, category2, category3));
      productRepository.save(product);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(
                      objectMapper.writeValueAsString(
                          List.of(category1.getId(), category3.getId()))))
          .andExpect(status().isOk());

      Product updatedProduct =
          productRepository.findById(product.getId()).orElseThrow(Exception::new);
      Set<Category> categories = updatedProduct.getCategories();

      assertTrue(categories.contains(category1));
      assertTrue(categories.contains(category3));
      assertEquals(2, categories.size());
    }

    @Test
    @Transactional
    void updateCategories_delete_all() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      Category category = new Category();
      category.setName("Category 1");
      categoryRepository.save(category);

      product.getCategories().add(category);
      productRepository.save(product);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(List.of())))
          .andExpect(status().isOk());

      Product updatedProduct =
          productRepository.findById(product.getId()).orElseThrow(Exception::new);
      Set<Category> categories = updatedProduct.getCategories();

      assertTrue(categories.isEmpty());
    }

    @Test
    @Transactional
    void updateCategories_emptyCategories() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(List.of())))
          .andExpect(status().isOk());
    }

    @Test
    void updateCategories_productNotFound() throws Exception {
      mockMvc
          .perform(
              put("/api/products/999/categories")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(List.of(1L, 2L, 3L))))
          .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_verification_emptyName() throws Exception {
      ProductUpdateRequest request =
          createRequest("", 5, new BigDecimal("200.00"), new BigDecimal("300.00"));

      expectCreateBadRequest(request);
    }

    @Test
    void createProduct_verification_negativeAvailability() throws Exception {
      ProductUpdateRequest request =
          createRequest("New Product", -5, new BigDecimal("200.00"), new BigDecimal("300.00"));

      expectCreateBadRequest(request);
    }

    @Test
    void deleteProduct_checkDatabase() throws Exception {
      Product product =
          createProduct(
              "Test Product",
              10,
              "Test Description",
              new BigDecimal("100.00"),
              new BigDecimal("150.00"));

      mockMvc
          .perform(
              delete("/api/products/" + product.getId())
                  .header("Authorization", "Bearer " + adminToken))
          .andExpect(status().isNoContent());

      assertFalse(productRepository.existsById(product.getId()));
    }

    @Test
    @Transactional
    void addImage_successful_one() throws Exception {
      Image image = new Image();
      image.setData("sample data".getBytes());
      imageRepository.save(image);

      Product product = new Product();
      product.setName("test category");
      product.setAvailability(30);
      product.setDescription("sample description");
      product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
      productRepository.save(product);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(List.of(image.getId()))))
          .andExpect(status().isOk());

      assertEquals(1, product.getImages().size());
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

      Product product = new Product();
      product.setName("test category");
      product.setAvailability(30);
      product.setDescription("sample description");
      product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
      productRepository.save(product);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(
                      objectMapper.writeValueAsString(List.of(image1.getId(), image2.getId()))))
          .andExpect(status().isOk());

      assertEquals(2, product.getImages().size());
    }

    @Test
    @Transactional
    void addImage_not_exists_one() throws Exception {

      Product product = new Product();
      product.setName("test category");
      product.setAvailability(30);
      product.setDescription("sample description");
      product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
      productRepository.save(product);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(List.of(1))))
          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void addImage_not_exists_one_and_one_exists() throws Exception {
      Image image = new Image();
      image.setData("sample image data".getBytes());

      Product product = new Product();
      product.setName("test category");
      product.setAvailability(30);
      product.setDescription("sample description");
      product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
      product.getImages().add(image);
      productRepository.save(product);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(List.of(1, image.getId()))))
          .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void addImage_not_exists_multiple() throws Exception {

      Product product = new Product();
      product.setName("test category");
      product.setAvailability(30);
      product.setDescription("sample description");
      product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
      productRepository.save(product);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
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

      Product product = new Product();
      product.setName("test category");
      product.setAvailability(30);
      product.setDescription("sample description");
      product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
      product.getImages().add(image);
      product.getImages().add(orphanImage);
      productRepository.save(product);

      mockMvc
          .perform(
              put("/api/products/" + product.getId() + "/images")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + adminToken)
                  .content(objectMapper.writeValueAsString(List.of(image.getId()))))
          .andExpect(status().isOk());

      assertFalse(imageRepository.existsById(orphanImage.getId()));
    }
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
        .andExpect(jsonPath("$[0].price").value(150.0));
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
  void getProductById_checkAllFields() throws Exception {
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
        .andExpect(jsonPath("$.id").value(product.getId()))
        .andExpect(jsonPath("$.name").value(product.getName()))
        .andExpect(jsonPath("$.availability").value(product.getAvailability()))
        .andExpect(jsonPath("$.description").value(product.getDescription()))
        .andExpect(jsonPath("$.price").value(150.0));
  }

  @Test
  void getAllProducts_checkAllFields() throws Exception {
    Product product1 =
        createProduct(
            "Product 1", 10, "Description 1", new BigDecimal("100.00"), new BigDecimal("150.00"));

    Product product2 =
        createProduct(
            "Product 2", 20, "Description 2", new BigDecimal("200.00"), new BigDecimal("250.00"));

    mockMvc
        .perform(get("/api/products").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(product1.getId()))
        .andExpect(jsonPath("$[0].name").value(product1.getName()))
        .andExpect(jsonPath("$[0].availability").value(product1.getAvailability()))
        .andExpect(jsonPath("$[0].description").value(product1.getDescription()))
        .andExpect(jsonPath("$[0].price").value(150.0))
        .andExpect(jsonPath("$[1].id").value(product2.getId()))
        .andExpect(jsonPath("$[1].name").value(product2.getName()))
        .andExpect(jsonPath("$[1].availability").value(product2.getAvailability()))
        .andExpect(jsonPath("$[1].description").value(product2.getDescription()))
        .andExpect(jsonPath("$[1].price").value(250.0));
  }

  @Test
  @Transactional
  void getImages_successful_one() throws Exception {
    Image image = new Image();
    image.setData("sample data first".getBytes());
    imageRepository.save(image);

    Product product = new Product();
    product.setName("test category");
    product.setAvailability(30);
    product.setDescription("sample description");
    product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
    product.getImages().add(image);
    productRepository.save(product);

    String response =
        mockMvc
            .perform(get("/api/products/" + product.getId() + "/images"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertTrue(response.contains(String.valueOf(image.getId())));
  }

  @Test
  @Transactional
  void getImages_empty() throws Exception {
    Product product = new Product();
    product.setName("test category");
    product.setAvailability(30);
    product.setDescription("sample description");
    product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
    productRepository.save(product);

    mockMvc
        .perform(get("/api/products/" + product.getId() + "/images"))
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

    Product product = new Product();
    product.setName("test category");
    product.setAvailability(30);
    product.setDescription("sample description");
    product.setCost(new Cost(BigDecimal.valueOf(130), BigDecimal.valueOf(2500)));
    product.getImages().add(image1);
    product.getImages().add(image2);
    productRepository.save(product);

    String response =
        mockMvc
            .perform(get("/api/products/" + product.getId() + "/images"))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    assertTrue(response.contains(String.valueOf(image1.getId())));
    assertTrue(response.contains(String.valueOf(image2.getId())));
  }
}
