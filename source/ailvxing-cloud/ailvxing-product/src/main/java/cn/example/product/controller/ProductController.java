package cn.example.product.controller;

import cn.example.common.common.Result;
import cn.example.common.entity.Product;
import cn.example.common.entity.ProductTag;
import cn.example.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "旅游产品管理")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "获取产品详情")
    @GetMapping("/{id}")
    public Result<Product> getProduct(@PathVariable Long id) {
        return productService.getProduct(id);
    }

    @Operation(summary = "获取在售产品列表")
    @GetMapping("/list")
    public Result<List<Product>> listOnSale() {
        return productService.listOnSale();
    }

    @Operation(summary = "按目的地搜索")
    @GetMapping("/search/destination")
    public Result<List<Product>> searchByDestination(@RequestParam String destination) {
        return productService.searchByDestination(destination);
    }

    @Operation(summary = "按类型搜索")
    @GetMapping("/search/type")
    public Result<List<Product>> searchByType(@RequestParam String type) {
        return productService.searchByType(type);
    }

    @Operation(summary = "添加产品（旅行社）")
    @PostMapping
    public Result<Void> addProduct(@RequestBody Product product) {
        return productService.addProduct(product);
    }

    @Operation(summary = "更新产品（旅行社）")
    @PutMapping
    public Result<Void> updateProduct(@RequestBody Product product) {
        return productService.updateProduct(product);
    }

    @Operation(summary = "获取产品标签列表")
    @GetMapping("/tags")
    public Result<List<ProductTag>> listTags() {
        return productService.listTags();
    }

    @Operation(summary = "删除产品")
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @Operation(summary = "扣减库存")
    @PostMapping("/{id}/decreaseStock")
    public Result<Void> decreaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.decreaseStock(id, quantity);
        return Result.success();
    }

    @Operation(summary = "恢复库存")
    @PostMapping("/{id}/increaseStock")
    public Result<Void> increaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.increaseStock(id, quantity);
        return Result.success();
    }
}