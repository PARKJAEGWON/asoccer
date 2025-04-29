package com.groo.asoccer.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {

    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                //다시 api경로들을 잠금
                .securityMatcher("/api/**")
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
//                        .requestMatchers(HttpMethod.GET, "/api/*/articles").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/api/*/articles/*").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/*/members/signup").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/*/members/login").permitAll()
                                .requestMatchers(HttpMethod.GET, "/api/*/members/logout").permitAll()
                                .requestMatchers(HttpMethod.POST, "/api/*/members/restore").permitAll()
//                                .requestMatchers(HttpMethod.GET, "/api/*/members/*").permitAll()//테스트용 임시
                        .anyRequest().authenticated()
                )
                //csrf중복으로 끄는지 궁금했는데 이전 설정과 별개의 설정체인을 만드는것이기 때문에 다시 명시해줘야함
                .csrf(csrf -> csrf.disable())
                //jwt를 사용하기 때문에 시큐리티의 기본 인증 방식인 httpBasic이 불 필요한 인증방식이 된다
                .httpBasic(httpBasic -> httpBasic.disable())
                //위와 같이 시큐리티의 기본 폼 방식인데 rest api 서버에서는 로그인 폼이 필요가 없어서 꺼줘야 좋음
                .formLogin(formLogin -> formLogin.disable())
                //세션을 만들지않음 jwt쿠키를 사용하기 때문에 세션을 끔 이제 클라이언트에서만 관리된다
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                //UsernamePasswordAuthenticationFilter실행전 jwtAuthorizationFilter얘를 먼저 실행해라
                .addFilterBefore(
                        jwtAuthorizationFilter,//jwt가 있으면 인증자로 작동
                        UsernamePasswordAuthenticationFilter.class// 로그인해서 인증해라 전에 jwtAuthorizationFilter 있으면 안함
                );
        return http.build();
    }
}

