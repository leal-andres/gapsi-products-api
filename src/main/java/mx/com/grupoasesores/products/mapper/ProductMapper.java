package mx.com.grupoasesores.products.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import mx.com.grupoasesores.products.dto.ProductResponse;
import mx.com.grupoasesores.products.entity.Product;

@Mapper(componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {
    ProductResponse toResponse(Product product);
}
