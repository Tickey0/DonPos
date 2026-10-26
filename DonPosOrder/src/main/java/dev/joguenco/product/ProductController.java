package dev.joguenco.product;

import dev.joguenco.product.domain.Category;
import dev.joguenco.product.domain.Product;
import dev.joguenco.product.repository.CategoryRepository;
import dev.joguenco.product.repository.ProductRepository;
import io.micronaut.data.model.Sort;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;
import java.util.List;

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/donpos/order/v1/products")
public class ProductController {

  protected final CategoryRepository categoryRepository;
  protected final ProductRepository productRepository;

  public ProductController(
      CategoryRepository categoryRepository, ProductRepository productRepository) {
    this.categoryRepository = categoryRepository;
    this.productRepository = productRepository;
  }

  @Get("/categories")
  public List<Category> listCategory() {
    return categoryRepository.findAll(Sort.of(Sort.Order.asc("catOrder")));
  }

  @Get("/{categoryId}")
  public List<Product> listProduct(String categoryId) {
    return productRepository.findAllByCategory(categoryId);
  }
}
