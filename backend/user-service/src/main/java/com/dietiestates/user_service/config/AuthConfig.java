package com.dietiestates.user_service.config;

import com.dietiestates.user_service.repository.UserRepository;
import com.dietiestates.user_service.service.CustomOAuth2SuccessHandler;
import com.dietiestates.user_service.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class AuthConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public HeaderAuthenticationFilter headerAuthenticationFilter() {
        return new HeaderAuthenticationFilter(userDetailsService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

 @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http,
        HeaderAuthenticationFilter headerAuthenticationFilter,
        CustomOAuth2SuccessHandler successHandler) throws Exception {

    return http
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            
            .requestMatchers("/auth/**", "/oauth2/**", "/login/**").permitAll()
            .anyRequest().authenticated()
        )
        .oauth2Login(oauth -> oauth
            .successHandler(successHandler)
            
            .authorizationEndpoint(auth -> auth.baseUri("/oauth2/authorization"))
            .redirectionEndpoint(red -> red.baseUri("/login/oauth2/code/*"))
        )
        .addFilterBefore(headerAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class)
        .build();
}
}
