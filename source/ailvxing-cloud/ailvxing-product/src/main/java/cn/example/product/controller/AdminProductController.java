package cn.example.product.controller;

import cn.example.common.common.Result;
import cn.example.common.entity.Product;
import cn.example.product.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/product")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductMapper productMapper;

    @PutMapping("/{id}/offline")
    public Result<Void> offlineProduct(@PathVariable Long id) {
        Product p = productMapper.findById(id);
        if (p != null) { p.setStatus(0); productMapper.update(p); }
        return Result.success();
    }

    @PutMapping("/{id}/online")
    public Result<Void> onlineProduct(@PathVariable Long id) {
        Product p = productMapper.findById(id);
        if (p != null) { p.setStatus(1); productMapper.update(p); }
        return Result.success();
    }

    @PutMapping
    public Result<Void> updateProduct(@RequestBody Product product) {
        productMapper.update(product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        productMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping
    public Result<List<Product>> listAll() {
        return Result.success(productMapper.findAll());
    }
}
