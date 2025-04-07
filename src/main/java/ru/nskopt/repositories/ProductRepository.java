package ru.nskopt.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.nskopt.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @Query("SELECT p.id FROM Product p " + "JOIN p.categories c " + "WHERE c.id = :categoryId")
  List<Long> findAllProductsIdByCategoryId(Long categoryId);
}
