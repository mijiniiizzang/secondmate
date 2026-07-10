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
                                "/review/**",
                                "/ws/**")
                        .authenticated()
                        .anyRequest().permitAll())

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendRedirect("/home");
                        }))

                .formLogin(form -> form.loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .successHandler((request, response, authentication) -> {
                            String redirectUrl = request.getParameter("redirectUrl");

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
                            String redirectUrl = request.getParameter("redirectUrl");

                            if (exception instanceof DisabledException) {
                                String count = request.getParameter("count");
                                String until = request.getParameter("until");

                                response.sendRedirect("/auth/login?suspended=true&count=" + count + "&until=" + until);
                                return;
                            }

                            if (redirectUrl != null
                                    && !redirectUrl.isBlank()
                                    && redirectUrl.startsWith("/")
                                    && !redirectUrl.startsWith("//")) {

                                if (redirectUrl.contains("?")) {
                                    response.sendRedirect(redirectUrl + "&loginError=true");
                                } else {
                                    response.sendRedirect(redirectUrl + "?loginError=true");
                                }

                                return;
                            }

                            response.sendRedirect("/home?loginError=true");
                        })
                        .permitAll())

                .logout(logout -> logout.logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/home")
                        .permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}