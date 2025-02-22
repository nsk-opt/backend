package ru.nskopt.repositories;

import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.nskopt.models.entities.Category;
import ru.nskopt.models.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("SELECT c FROM Category c JOIN c.products p WHERE p.id = :productId")
  Set<Category> findCategoriesByProductId(@Param("productId") Long productId);
}
