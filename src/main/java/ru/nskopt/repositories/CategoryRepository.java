package ru.nskopt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nskopt.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {}
