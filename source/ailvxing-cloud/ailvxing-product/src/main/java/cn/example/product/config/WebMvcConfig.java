package cn.example.product.config;

import cn.example.product.interceptor.JwtInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/user/register",
                        "/api/user/login",
                        "/api/product/list",
                        "/api/product/**",       // Feign 内部调用（单品、扣库存等）
                        "/api/product/search/**",
                        "/api/product/tags",
                        "/api/plan/shared/**",
                        "/api/review/product/**",
                        "/api/admin/product/**",  // 管理员操作
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                );
    }
}