package cn.example.plan.client;

import cn.example.common.common.Result;
import cn.example.common.entity.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * OpenFeign 远程调用产品服务
 */
@FeignClient("ailvxing-product")
public interface ProductClient {

    @GetMapping("/api/product/{id}")
    Result<Product> getById(@PathVariable("id") Long id);

    @GetMapping("/api/product/list")
    Result<List<Product>> listAll();
}
