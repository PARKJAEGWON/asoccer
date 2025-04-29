package com.groo.asoccer.global.security;

import com.groo.asoccer.domain.member.member.service.MemberService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;


//ApiSecurityConfig에서 허용하지 않은 api주소를 검증해서 인가처리해주기 위한 파일
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final HttpServletRequest req;
    private final HttpServletResponse resp;
    private final MemberService memberService;
    ////////////////////////////////////////////////////////////////////////////////////////이부분 다시 집중해서 이해해야함
@Override
@SneakyThrows
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
    //로그인 주소는 열어주기
    if (            request.getRequestURI().equals("/api/v1/members/login")     ||
                    request.getRequestURI().equals("/api/v1/members/logout")    ||
                    request.getRequestURI().equals("/api/v1/members/restore")
                    ) {
        filterChain.doFilter(request, response);
        return;
    }
    String accessToken = _getCookie("accessToken");

    // accessToken이 있고 비어있지 않을 때만 검증 진행
    if (accessToken != null && !accessToken.isBlank()) {
        // 토큰 유효기간 검증
        if (!memberService.validateToken(accessToken)) {
            String refreshToken = _getCookie("refreshToken");

            // refreshToken이 있을 때만 새 토큰 발급
            if (refreshToken != null && !refreshToken.isBlank()) {
                String newAccessToken = memberService.refreshAccessToken(refreshToken);
                _addHeaderCookie("accessToken", newAccessToken);
                accessToken = newAccessToken;
            }
        }
        // securityUser 가져오기
        SecurityUser securityUser = memberService.getUserFromAccessToken(accessToken);
        // 인가 처리
        SecurityContextHolder.getContext().setAuthentication(securityUser.genAuthentication());
    }
    filterChain.doFilter(request, response);
}
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private String _getCookie(String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;  // 쿠키가 없는 경우 null 반환

        return Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(name))
                .findFirst()
                .map(Cookie::getValue)
                .orElse("");
    }
    private void _addHeaderCookie(String tokenName, String token) {

        int maxAge = tokenName.equals("accessToken") ? 60 * 60 : 60 * 60 *24 *7; //삼항 연산자 사용해서 입장토큰이름이면 1시간 아니면 7일

        ResponseCookie cookie = ResponseCookie.from(tokenName, token)
                .path("/")
                .sameSite("None")
                .secure(true)
                .httpOnly(true)
                .maxAge(maxAge)
                .build();
        resp.addHeader("Set-Cookie", cookie.toString());
    }
}