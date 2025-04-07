package ru.nskopt.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.nskopt.entities.image.Image;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @JoinColumn(name = "cat_images_id")
  private Set<Image> images = new HashSet<>();

  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @ManyToMany(mappedBy = "categories")
  private Set<Product> products = new HashSet<>();
}
