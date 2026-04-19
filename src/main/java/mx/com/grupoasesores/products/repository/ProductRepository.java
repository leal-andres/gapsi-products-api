package mx.com.grupoasesores.products.repository;

import mx.com.grupoasesores.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
}
