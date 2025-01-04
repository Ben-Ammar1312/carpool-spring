package gle.carpoolspring.config;

import gle.carpoolspring.jwt.AuthEntryPointJwt;
import gle.carpoolspring.jwt.AuthTokenFilter;
import gle.carpoolspring.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    /**
     * JWT Token Filter Bean
     */
    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    /**
     * Authentication Manager Bean
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * Password Encoder Bean using BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DAO Authentication Provider Bean
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * API Security Configuration
     * Applies to endpoints matching /api/**
     */
    @Bean
    @Order(1) // Higher precedence
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // Apply to /api/** endpoints
                .csrf(csrf -> csrf.disable()) // Disable CSRF for APIs
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll() // Permit authentication endpoints
                        .anyRequest().authenticated() // Secure other API endpoints
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler) // Handle unauthorized access
                )
                .addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class) // Add JWT filter
                .authenticationProvider(authenticationProvider()); // Register DAO provider for APIs if needed

        return http.build();
    }

    /**
     * Web Security Configuration
     * Applies to all other endpoints
     */
    @Bean
    @Order(2) // Lower precedence
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**") // Apply to all other endpoints
                .csrf(csrf -> csrf.disable()) // Enable CSRF protection for web
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/ws/**", "/register", "/verify-sms", "/verify", "/login", "/css/**", "/js/**", "/images/**").permitAll() // Public endpoints
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin endpoints
                        .anyRequest().authenticated() // Secure other web endpoints
                )
                .formLogin(form -> form
                        .loginPage("/login") // Custom login page
                        .successHandler((request, response, authentication) -> {
                            var authorities = authentication.getAuthorities();
                            if (authorities.stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                                response.sendRedirect("/admin/dashboard"); // Redirect admin users
                            } else {
                                response.sendRedirect("/search"); // Redirect regular users
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout") // Redirect after logout
                        .invalidateHttpSession(true) // Invalidate session
                        .deleteCookies("JSESSIONID") // Delete session cookie
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider()); // Register DAO provider for web

        // **Do NOT add JWT filter to Web Security Filter Chain**
        // Ensure that the following line is **not present**:
        // http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}