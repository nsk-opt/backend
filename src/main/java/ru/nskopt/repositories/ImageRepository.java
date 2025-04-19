package ru.nskopt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nskopt.entities.image.Image;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {}
