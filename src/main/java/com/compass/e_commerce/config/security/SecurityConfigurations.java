package com.compass.e_commerce.config.security;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.core.GrantedAuthorityDefaults;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@SecurityScheme(name = SecurityConfigurations.SECURITY, type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer")
public class SecurityConfigurations {

    public static final String SECURITY = "bearerAuth";
    private final SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/registerUser").permitAll()
                        .requestMatchers(HttpMethod.POST, "/email/sendEmailForgotPassword").permitAll()
                        .requestMatchers(HttpMethod.POST, "resetPassword/request").permitAll()
                        .requestMatchers("v3/api-docs/**", "swagger-ui/**", "swagger/ui.html").permitAll()
                        .requestMatchers(HttpMethod.PUT, "user/isActive/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "game/isActive/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"user/isInactive/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,"game/isInactive/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "stock/reduction/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "stock/replenishment/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "cache/clear/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "auth/registerAdmin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "game/create").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "game/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                        .anyRequest().hasRole("USER")
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Autenticação falhou: credenciais inválidas"))
                )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public GrantedAuthorityDefaults grantedAuthorityDefaults() {
        return new GrantedAuthorityDefaults("");
    }
}
