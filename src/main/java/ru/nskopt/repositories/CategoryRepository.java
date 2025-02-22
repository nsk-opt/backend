package ru.nskopt.repositories;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Product;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
  @Query("SELECT c.products FROM Category c WHERE c.id = :categoryId")
  Set<Product> findProductsByCategoryId(@Param("categoryId") Long categoryId);
}
