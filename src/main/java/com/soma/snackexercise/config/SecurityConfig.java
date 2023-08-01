package com.soma.snackexercise.config;

import com.soma.snackexercise.auth.jwt.filter.JwtAuthenticationProcessingFilter;
import com.soma.snackexercise.auth.jwt.handler.JwtAccessDeniedHandler;
import com.soma.snackexercise.auth.jwt.service.JwtService;
import com.soma.snackexercise.auth.oauth.handler.OAuth2LoginFailureHandler;
import com.soma.snackexercise.auth.oauth.handler.OAuth2LoginSuccessHandler;
import com.soma.snackexercise.auth.oauth.service.CustomOAuth2UserService;
import com.soma.snackexercise.repository.member.MemberRepository;
import com.soma.snackexercise.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final OAuth2LoginFailureHandler oAuth2LoginFailureHandler;
    private final CustomOAuth2UserService customOauth2UserService;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RedisUtil redisUtil;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(formLogin -> formLogin.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(request -> request.requestMatchers(
                        "/error", "/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**", "/css/**", "/images/**", "/js/**", "/favicon.ico", "/h2-console/**",
                        "/sign-up", "/api/auth/**", "/health").permitAll()
                                       .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated())
                .oauth2Login(oauth2Login -> oauth2Login.successHandler(oAuth2LoginSuccessHandler)
                        .failureHandler(oAuth2LoginFailureHandler)
                        .userInfoEndpoint(userInfoEndPoint -> userInfoEndPoint.userService(customOauth2UserService)))
                .addFilterAfter(jwtAuthenticationProcessingFilter(), LogoutFilter.class)
                .exceptionHandling(exception -> exception.accessDeniedHandler(jwtAccessDeniedHandler));

        return http.build();
    }

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter = new JwtAuthenticationProcessingFilter(jwtService, memberRepository, redisUtil);
        return jwtAuthenticationProcessingFilter;
    }
}
