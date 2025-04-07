package ru.nskopt.advices;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.nskopt.App;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = App.class)
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

  @Autowired private MockMvc mvc;

  @Test
  void handleNotFoundException() throws Exception {
    mvc.perform(
            post("/api/non-existent-endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void handleNoHandlerFoundException() throws Exception {
    mvc.perform(
            post("/api/another-non-existent-endpoint")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
        .andExpect(status().isNotFound())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void handleValidationExceptions() throws Exception {
    mvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"invalidField\":\"invalidValue\"}"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void handleHttpMessageNotReadableException() throws Exception {
    mvc.perform(
            post("/api/categories").contentType(MediaType.APPLICATION_JSON).content("invalid json"))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.timestamp").exists());
  }

  @Test
  void handleHttpMessageNotReadableException_2() throws Exception {
    mvc.perform(
            post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content("keyss: \"value\""))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.error").exists())
        .andExpect(jsonPath("$.timestamp").exists());
  }
}
