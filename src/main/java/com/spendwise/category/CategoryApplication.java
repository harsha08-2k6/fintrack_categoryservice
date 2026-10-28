package com.spendwise.category;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@SpringBootApplication
@EnableDiscoveryClient
@RestController
public class CategoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CategoryApplication.class, args);
    }

    @GetMapping("/")
    public ResponseEntity<Map<String, String>> home() {
        return ResponseEntity.ok(Map.of(
            "service", "FinTrack Category Service",
            "status", "UP",
            "message", "Category Service is running and connected to Eureka!"
        ));
    }
}
