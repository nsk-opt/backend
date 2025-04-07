package ru.nskopt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.nskopt.exceptions.UnsupportedImageFormatException;
import ru.nskopt.services.ImageService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/images")
@Tag(name = "Image Controller", description = "Управление изображениями")
public class ImageController {

  private final ImageService imageService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
  @Operation(
      summary = "Загрузить изображение",
      description = "Загружает изображение и возвращает его ID.")
  public Long createImage(
      @Parameter(description = "Файл изображения", required = true, example = "file.png")
          @RequestParam
          MultipartFile file)
      throws IOException {

    String contentType = file.getContentType();

    if (contentType == null
        || (!contentType.equals(MediaType.IMAGE_PNG_VALUE)
            && !contentType.equals(MediaType.IMAGE_JPEG_VALUE)
            && !contentType.equals("image/webp"))) {
      throw new UnsupportedImageFormatException("Unsupported image format: " + contentType);
    }

    return imageService.saveImage(file).getId();
  }

  @GetMapping(
      value = "/{id}",
      produces = {"image/webp"})
  @Operation(
      summary = "Получить данные изображения",
      description = "Возвращает бинарные данные изображения по его ID.")
  public byte[] getImageData(
      @Parameter(description = "ID изображения", example = "1") @PathVariable Long id) {
    return imageService.getImageData(id);
  }
}
