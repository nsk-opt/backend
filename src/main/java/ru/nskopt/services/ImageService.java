package ru.nskopt.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import ru.nskopt.exceptions.ResourceNotFoundException;
import ru.nskopt.models.entities.Image;
import ru.nskopt.properties.ImageCompressionProperties;
import ru.nskopt.repositories.ImageRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

  private final ImageCompressionProperties imageCompressionProperties;

  private final ImageRepository imageRepository;

  public Image saveImage(MultipartFile file) throws IOException {
    Image image = new Image();
    image.setData(compressImage(file.getBytes()));
    imageRepository.save(image);

    log.info(
        "Save new image with ID {} size {} kB",
        image.getId(),
        String.format("%.2f", (double) image.getData().length / 1000));

    return image;
  }

  private byte[] compressImage(byte[] data) throws IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(new ByteArrayInputStream(data))
        .size(1000, 1000)
        .keepAspectRatio(false)
        .outputFormat("webp")
        .outputQuality(imageCompressionProperties.getQuality())
        .toOutputStream(outputStream);

    return outputStream.toByteArray();
  }

  public Image getImage(Long id) {
    return imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(id));
  }

  @Transactional
  public byte[] getImageData(Long id) {
    return getImage(id).getData();
  }

  public List<Image> getImagesByIds(List<Long> ids) {
    List<Image> result = imageRepository.findAllById(ids);

    if (result.size() != ids.size()) {
      Set<Long> foundIds = result.stream().map(Image::getId).collect(Collectors.toSet());

      List<Long> notFoundIds = ids.stream().filter(id -> !foundIds.contains(id)).toList();

      throw new ResourceNotFoundException(notFoundIds);
    }

    return result;
  }
}
