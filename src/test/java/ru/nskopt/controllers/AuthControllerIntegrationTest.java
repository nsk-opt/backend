package ru.nskopt.controllers;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;
import ru.nskopt.App;
import ru.nskopt.dto.user.security.UserAuthRequest;
import ru.nskopt.dto.user.security.UserRegistrationRequest;
import ru.nskopt.entities.user.Role;
import ru.nskopt.entities.user.User;
import ru.nskopt.repositories.UserRepository;
import ru.nskopt.utils.JwtUtils;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
@DisplayName("/api/auth -- AuthController Integration Tests")
class AuthControllerIntegrationTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private PasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtUtils jwtUtils;

  private final String validUsername = "semenPavlov1234";
  private final String validPassword = "superPaSsWoRd1337";

  @BeforeEach
  void setup() {
    userRepository.deleteAll();
  }

  User createUserWithRole(Role role) {
    User user = new User();
    user.setUsername("username");
    user.setPassword("password");
    user.setRole(role);

    return userRepository.save(user);
  }

  @Nested
  @DisplayName("/api/auth/check-{admin, manager}")
  class CheckAuthoritiesTests {
    @Test
    @DisplayName("200: /api/auth/check-admin -- correct admin jwt")
    void checkValidAdminJwt() throws Exception {
      User admin = createUserWithRole(Role.ROLE_ADMIN);

      mockMvc
          .perform(
              post("/api/auth/check-admin")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + jwtUtils.generateToken(admin)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("403: /api/auth/check-admin -- incorrect manager jwt")
    void checkInvalidManagerJwt() throws Exception {
      User admin = createUserWithRole(Role.ROLE_MANAGER);

      mockMvc
          .perform(
              post("/api/auth/check-admin")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + jwtUtils.generateToken(admin)))
          .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("200: /api/auth/check-manager -- correct admin jwt")
    void checkValidAdminJwtManager() throws Exception {
      User admin = createUserWithRole(Role.ROLE_ADMIN);

      mockMvc
          .perform(
              post("/api/auth/check-manager")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + jwtUtils.generateToken(admin)))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("200: /api/auth/check-manager -- incorrect manager jwt")
    void checkValidManagerJwtManager() throws Exception {
      User admin = createUserWithRole(Role.ROLE_MANAGER);

      mockMvc
          .perform(
              post("/api/auth/check-manager")
                  .contentType(MediaType.APPLICATION_JSON)
                  .header("Authorization", "Bearer " + jwtUtils.generateToken(admin)))
          .andExpect(status().isOk());
    }
  }

  @Nested
  @DisplayName("/api/auth/register -- Registration Tests")
  class RegistrationTests {

    @Test
    @DisplayName("201: Should successfully register new user")
    void registration_valid() throws Exception {
      UserRegistrationRequest request = new UserRegistrationRequest(validUsername, validPassword);

      assertTrue(userRepository.findAll().isEmpty(), "Database should be empty before test");

      mockMvc
          .perform(
              post("/api/auth/register")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isCreated());

      assertTrue(
          userRepository.existsByUsername(validUsername), "User should be created in database");
    }

    @Nested
    @DisplayName("User already exists")
    class ExistingUserTests {
      @BeforeEach
      void createExistingUser() {
        User user = new User();
        user.setUsername(validUsername);
        user.setPassword(passwordEncoder.encode(validPassword));
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
      }

      @Test
      @DisplayName("409: Same credentials")
      void registration_userExists_sameCredentials() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest(validUsername, validPassword);

        mockMvc
            .perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
      }

      @Test
      @DisplayName("409: Different password")
      void registration_userExists_differentPassword() throws Exception {
        UserRegistrationRequest request =
            new UserRegistrationRequest(validUsername, "differentPassword");

        mockMvc
            .perform(
                post("/api/auth/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict());
      }
    }
  }

  @Nested
  @DisplayName("Login Tests")
  class LoginTests {

    @BeforeEach
    void createUser() {
      User user = new User();
      user.setUsername(validUsername);
      user.setPassword(passwordEncoder.encode(validPassword));
      user.setRole(Role.ROLE_USER);
      userRepository.save(user);
    }

    @Test
    @DisplayName("200: Should return valid access token")
    void login_validCredentials() throws Exception {
      UserAuthRequest request = new UserAuthRequest(validUsername, validPassword);

      MvcResult result =
          mockMvc
              .perform(
                  post("/api/auth/login")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(request)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.accessToken").exists())
              .andReturn();

      String jwtToken =
          objectMapper
              .readTree(result.getResponse().getContentAsString())
              .path("accessToken")
              .asText();

      //noinspection OptionalGetWithoutIsPresent
      assertTrue(
          jwtUtils.isTokenValid(jwtToken, userRepository.findByUsername(validUsername).get()),
          "JWT is invalid");
    }

    @Test
    @DisplayName("401: Non existing username")
    void login_username_notExists() throws Exception {
      @SuppressWarnings("SpellCheckingInspection")
      UserAuthRequest request = new UserAuthRequest("nonexisting", validPassword);

      mockMvc
          .perform(
              post("/api/auth/login")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isUnauthorized())
          .andExpect(jsonPath("$.timestamp").exists())
          .andExpect(jsonPath("$.error").exists());
    }

    @Nested
    @DisplayName("Invalid password")
    class InvalidPasswordTests {
      @Test
      @DisplayName("401: wrong password (same length)")
      void login_invalid_password_sameLength() throws Exception {
        UserAuthRequest request = new UserAuthRequest(validUsername, "123457");

        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.error").exists());
      }

      @Test
      @DisplayName("401: wrong password (shorter)")
      void login_invalid_password_shorter() throws Exception {
        UserAuthRequest request = new UserAuthRequest(validUsername, "12345");

        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.error").exists());
      }

      @Test
      @DisplayName("401: wrong password (longer)")
      void login_invalid_password_longer() throws Exception {
        UserAuthRequest request = new UserAuthRequest(validUsername, "123554567");

        mockMvc
            .perform(
                post("/api/auth/login")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.timestamp").exists())
            .andExpect(jsonPath("$.error").exists());
      }
    }
  }
}
