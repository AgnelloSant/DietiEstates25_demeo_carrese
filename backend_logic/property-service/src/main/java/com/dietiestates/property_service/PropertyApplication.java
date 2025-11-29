package com.dietiestates.property_service;

import com.dietiestates.shared.security.JwtProperties;
import com.dietiestates.shared.security.JwtValidator;
import com.dietiestates.shared.security.RsaKeyLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.security.PublicKey;

@SpringBootApplication
@ComponentScan(basePackages = {"com.dietiestates.property_service", "com.dietiestates.shared"})
@EnableConfigurationProperties(JwtProperties.class)
public class PropertyApplication {
    public static void main(String[] args) {
        SpringApplication.run(PropertyApplication.class, args);
    }

    @Bean
    public JwtValidator jwtValidator(JwtProperties props, RsaKeyLoader keyLoader) {
        System.out.println("🔥 CREAZIONE JWT VALIDATOR IN CORSO..."); // Log di debug
        PublicKey publicKey = keyLoader.loadPublic(props.getPublicPem());
        return new JwtValidator(publicKey, props.getIss(), props.getAud());
    }
}