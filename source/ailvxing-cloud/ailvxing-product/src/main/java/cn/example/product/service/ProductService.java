package cn.example.product.service;

import cn.example.common.common.Result;
import cn.example.common.entity.Product;
import cn.example.common.entity.ProductTag;

import java.util.List;

public interface ProductService {

    Result<Product> getProduct(Long id);

    Result<List<Product>> listOnSale();

    Result<List<Product>> searchByDestination(String destination);

    Result<List<Product>> searchByType(String type);

    Result<Void> addProduct(Product product);

    Result<Void> updateProduct(Product product);

    Result<Void> deleteProduct(Long id);

    Result<List<ProductTag>> listTags();

    Result<Void> decreaseStock(Long productId, Integer quantity);

    Result<Void> increaseStock(Long productId, Integer quantity);
}