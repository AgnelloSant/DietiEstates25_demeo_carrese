// package com.dietiestates.property_service.controller;

// import com.dietiestates.shared.security.JwtProperties;
// import com.dietiestates.shared.security.JwtValidator;
// import com.dietiestates.shared.security.RsaKeyLoader;
// import org.springframework.boot.context.properties.EnableConfigurationProperties;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;

// import java.security.PublicKey;

// @Configuration
// @EnableConfigurationProperties(JwtProperties.class) // Abilita la lettura delle proprietà jwt.*
// public class JwtConfig {

//     private final JwtProperties props;
//     private final RsaKeyLoader keyLoader;

//     public JwtConfig(JwtProperties props, RsaKeyLoader keyLoader) {
//         this.props = props;
//         this.keyLoader = keyLoader;
//     }

//     @Bean
//     public JwtValidator jwtValidator() {
//         // Carica la chiave pubblica usando il loader condiviso
//         // Funziona sia con file locali (Docker) che con env vars (Azure)
//         PublicKey publicKey = keyLoader.loadPublic(props.getPublicPem());

//         // Crea e restituisce il validatore
//         return new JwtValidator(publicKey, props.getIss(), props.getAud());
//     }
// }