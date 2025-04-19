package ru.nskopt;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.nskopt.properties.ImageCompressionProperties;

@SpringBootApplication
@EnableConfigurationProperties(ImageCompressionProperties.class)
public class App {
  public static void main(String[] args) {
    SpringApplication app = new SpringApplication(App.class);
    app.setBannerMode(Banner.Mode.OFF);
    app.run(args);
  }
}
