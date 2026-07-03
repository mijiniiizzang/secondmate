package com.example.secondmate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers(
                "/product/edit",
                "/product/delete",
                "/wishlist/**",
                "/api/comments",
                "/report/**",
                "/mypage/**",
                "/chat/**",
                "/ws/**"
            ).authenticated()
            .anyRequest().permitAll()
        )
        
        .formLogin(form -> form.loginPage("/auth/login")
                               .loginProcessingUrl("/auth/login")
                               .successHandler((request, response, authentication) -> {
                                    String redirectUrl = request.getParameter("redirectUrl");

                                    // 현재 사이트 내부 주소일 때만 이동
                                    if (redirectUrl != null 
                                        && !redirectUrl.isBlank()
                                        && redirectUrl.startsWith("/")
                                        && !redirectUrl.startsWith("//")) {

                                        response.sendRedirect(redirectUrl);
                                        return;
                                    }
                                    response.sendRedirect("/home");
                               })
                               .failureHandler((request, response, exception) -> {
                                if (exception instanceof DisabledException) {
                                    String[] suspendedInfo = exception.getMessage().split("\\|");

                                    String count = suspendedInfo[0];
                                    String until = suspendedInfo[1];
                                    response.sendRedirect("/auth/login?suspended=true" + "&count=" + count + "&until=" + until);
                                } else {
                                    response.sendRedirect("/auth/login?error=true");
                                }
                               })
                               .permitAll()
        )
        .logout(logout -> logout.logoutUrl("/auth/logout")
                                .logoutSuccessUrl("/home")
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
