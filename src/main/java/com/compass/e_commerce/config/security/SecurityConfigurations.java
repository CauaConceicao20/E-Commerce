package com.compass.e_commerce.config.security;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
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

import static org.springframework.security.config.Customizer.withDefaults;

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
                        .requestMatchers(HttpMethod.POST, "/api/auth/v1/username").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/v1/registerUser").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/email/v1/sendEmailForgotPassword").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/v1/requestResetPassword").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger/ui.html").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/user/v1/userDetails").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/api/user/v1/update/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/user/v1/isActive/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/game/v1/isActive/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/user/v1/isInactive/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/game/v1/isInactive/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/stock/v1/reduction/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/stock/v1/replenishment/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cache/v1/clear/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/auth/v1/registerAdmin").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/role/v1/createRole").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/role/v1/getAllRoles").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/game/v1/update").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/cart/v1/removeGameFromCart/*").hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/purchasing/v1/generatePayment").hasRole("USER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> {
                    form.loginPage("/username").defaultSuccessUrl("/");
                })
                .logout(logout -> {
                    logout.logoutUrl("/logout");
                })
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

