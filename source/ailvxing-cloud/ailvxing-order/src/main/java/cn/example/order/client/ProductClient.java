package cn.example.order.client;

import cn.example.common.common.Result;
import cn.example.common.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient("ailvxing-product")
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    Result<Product> getById(@PathVariable("id") Long id);

    @PostMapping("/api/product/{id}/decreaseStock")
    Result<Void> decreaseStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);

    @PostMapping("/api/product/{id}/increaseStock")
    Result<Void> increaseStock(@PathVariable("id") Long id, @RequestParam("quantity") Integer quantity);
}
