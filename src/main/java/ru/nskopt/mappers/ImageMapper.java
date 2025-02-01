package ru.nskopt.mappers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.nskopt.models.dtos.ImageDto;
import ru.nskopt.models.entities.Image;

@Component
@Slf4j
public class ImageMapper implements Mapper<Image, ImageDto> {
  @Override
  public Image map(ImageDto value) {
    Image image = new Image();
    updateImageFields(image, value);
    return image;
  }

  @Override
  public void update(Image dest, ImageDto src) {
    updateImageFields(dest, src);
  }

  private void updateImageFields(Image image, ImageDto imageDto) {
    image.setLink(imageDto.getLink());
  }
}
