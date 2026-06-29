package com.example.secondmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/product/edit",
                "/product/delete",
                "/wishlist/**",
                "/api/comments"
            ).authenticated()
            .anyRequest().permitAll()
        )
        
        .formLogin(form -> form.loginPage("/auth/login")
                               .loginProcessingUrl("/auth/login")
                               .defaultSuccessUrl("/home", true)
                               .failureUrl("/auth/login?error=true")
                               .permitAll()
        )
        .logout(logout -> logout.logoutUrl("/auth/logout")
                                .logoutSuccessUrl("/auth/login?logoutMsg=true")
                                .permitAll()
        );

        return http.build();
    }
    // 비밀번호 암호화 객체 관리
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
