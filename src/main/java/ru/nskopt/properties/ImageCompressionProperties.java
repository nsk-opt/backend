package ru.nskopt.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.image")
@Data
public class ImageCompressionProperties {
  private double quality;
}
