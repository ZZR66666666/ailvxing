package cn.example.plan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.example.plan", "cn.example.common"})
@MapperScan("cn.example.plan.mapper")
@EnableDiscoveryClient
@EnableFeignClients
public class PlanApplication {
    public static void main(String[] args) {
        SpringApplication.run(PlanApplication.class, args);
    }
}
