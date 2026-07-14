package cn.example.product.service.impl;

import cn.example.common.common.Result;
import cn.example.common.entity.Product;
import cn.example.common.entity.ProductTag;
import cn.example.product.mapper.ProductMapper;
import cn.example.product.mapper.ProductTagMapper;
import cn.example.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductTagMapper productTagMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String PRODUCT_LIST_KEY = "product:list";
    private static final String PRODUCT_DETAIL_KEY = "product:detail:";
    private static final String PRODUCT_SEARCH_KEY = "product:search:";
    private static final long CACHE_TTL_HOURS = 2;

    @Override
    @SuppressWarnings("unchecked")
    public Result<Product> getProduct(Long id) {
        String key = PRODUCT_DETAIL_KEY + id;
        Product cached = (Product) redisTemplate.opsForValue().get(key);
        if (cached != null) return Result.success(cached);
        Product product = productMapper.findById(id);
        if (product == null) return Result.error(404, "产品不存在");
        redisTemplate.opsForValue().set(key, product, CACHE_TTL_HOURS, TimeUnit.HOURS);
        return Result.success(product);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Result<List<Product>> listOnSale() {
        List<Product> cached = (List<Product>) redisTemplate.opsForValue().get(PRODUCT_LIST_KEY);
        if (cached != null) return Result.success(cached);
        List<Product> products = productMapper.findAllOnSale();
        redisTemplate.opsForValue().set(PRODUCT_LIST_KEY, products, CACHE_TTL_HOURS, TimeUnit.HOURS);
        return Result.success(products);
    }

    @Override
    public Result<List<Product>> searchByDestination(String destination) {
        return Result.success(productMapper.findByDestination(destination));
    }

    @Override
    public Result<List<Product>> searchByType(String type) {
        return Result.success(productMapper.findByType(type));
    }

    @Override
    public Result<Void> addProduct(Product product) {
        product.setStatus(1);
        product.setSoldCount(0);
        productMapper.insert(product);
        clearProductCache();
        return Result.success();
    }

    @Override
    public Result<Void> updateProduct(Product product) {
        productMapper.update(product);
        redisTemplate.delete(PRODUCT_DETAIL_KEY + product.getId());
        clearProductCache();
        return Result.success();
    }

    @Override
    public Result<List<ProductTag>> listTags() {
        return Result.success(productTagMapper.findAll());
    }

    @Override
    public Result<Void> deleteProduct(Long id) {
        productMapper.deleteById(id);
        redisTemplate.delete(PRODUCT_DETAIL_KEY + id);
        clearProductCache();
        return Result.success();
    }

    @Override
    public Result<Void> decreaseStock(Long productId, Integer quantity) {
        productMapper.decreaseStock(productId, quantity);
        redisTemplate.delete(PRODUCT_DETAIL_KEY + productId);
        clearProductCache();
        return Result.success();
    }

    @Override
    public Result<Void> increaseStock(Long productId, Integer quantity) {
        productMapper.increaseStock(productId, quantity);
        redisTemplate.delete(PRODUCT_DETAIL_KEY + productId);
        clearProductCache();
        return Result.success();
    }

    private void clearProductCache() {
        var keys = redisTemplate.keys(PRODUCT_LIST_KEY + "*");
        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
        keys = redisTemplate.keys(PRODUCT_SEARCH_KEY + "*");
        if (keys != null && !keys.isEmpty()) redisTemplate.delete(keys);
    }
}
