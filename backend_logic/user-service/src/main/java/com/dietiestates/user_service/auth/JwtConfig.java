package com.dietiestates.user_service.auth;

import com.dietiestates.shared.security.JwtProperties;
import com.dietiestates.shared.security.JwtValidator;
import com.dietiestates.shared.security.RsaKeyLoader;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.PublicKey;

@Configuration
@EnableConfigurationProperties(JwtProperties.class) // Abilita la lettura delle properties qui
public class JwtConfig {

    private final JwtProperties props;
    private final RsaKeyLoader keyLoader;

    public JwtConfig(JwtProperties props, RsaKeyLoader keyLoader) {
        this.props = props;
        this.keyLoader = keyLoader;
    }

    @Bean
    public JwtValidator jwtValidator() {
        // 1. Qui siamo nel servizio User, quindi il file PEM esiste e possiamo caricarlo!
        PublicKey publicKey = keyLoader.loadPublic(props.getPublicPem());
        
        // 2. Creiamo il validatore iniettando la chiave e le stringhe
        return new JwtValidator(publicKey, props.getIss(), props.getAud());
    }
}