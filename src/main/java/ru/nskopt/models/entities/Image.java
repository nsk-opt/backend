package ru.nskopt.models.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "images")
@Data
@NoArgsConstructor
public class Image {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "image_data_id")
  @EqualsAndHashCode.Exclude
  private ImageData imageData;

  public byte[] getData() {
    if (imageData == null) return new byte[] {};

    return imageData.getData();
  }

  public void setData(byte[] data) {
    if (imageData == null) {
      imageData = new ImageData();
      imageData.setImage(this);
    }

    imageData.setData(data);
  }
}
