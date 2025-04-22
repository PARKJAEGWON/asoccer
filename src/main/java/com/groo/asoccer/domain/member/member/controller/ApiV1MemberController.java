package com.groo.asoccer.domain.member.member.controller;

import com.groo.asoccer.domain.member.member.dto.request.MemberLoginRequest;
import com.groo.asoccer.domain.member.member.dto.request.MemberSignupRequest;
import com.groo.asoccer.domain.member.member.entity.Member;
import com.groo.asoccer.domain.member.member.service.MemberService;
import com.groo.asoccer.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiV1MemberController {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    //회원가입
    @PostMapping("/signup")
    public Member signup(@Valid @RequestBody MemberSignupRequest memberSignupRequest){
        Member member = this.memberService.signup(
                memberSignupRequest.getMemberLoginId(),
                memberSignupRequest.getMemberPassword(),
                memberSignupRequest.getMemberName(),
                memberSignupRequest.getMemberPhone()
//                memberSignupRequest.getMemberStatus() 회원가입때는 필드에서 디폴트 값으로 설정
        );
        return member;
    }

    //로그인
    @PostMapping("login")
    public String login(@Valid @RequestBody MemberLoginRequest memberLoginRequest, HttpServletResponse httpServletResponse){
        Member member = memberService.login(memberLoginRequest.getMemberLoginId(), memberLoginRequest.getMemberPassword());

        //입장 토큰 만들기
        String accessToken = jwtProvider.generateAccessToken(member);

//        //httpServletResponse(스프링에서 제공) 응답을 제어하는 메서드 중에 쿠키제어 코드 호출/ 쿠키에 토큰 담기
//        httpServletResponse.addCookie(new Cookie("accessToken", token)); 이코드는 주석위에 설명참고 그리고 밑에 코드가 쿠키에 토큰담는 코드에 보안을 추가함

        //httpOnly Cookie로 보안 추가
        Cookie accessCookie  = new Cookie("accessToken", accessToken);

        //서버가 응답할 때만 하게 만듬 Js로의 접근을 막는다
        accessCookie .setHttpOnly(true);
        //https에서만 전송
        accessCookie .setSecure(true);
        accessCookie .setPath("/");
        accessCookie .setMaxAge(60 * 60);// 유효시간
        httpServletResponse.addCookie(accessCookie);

        //컨트롤러에서는 쿠키랑 호출만 관리하기위해 리프레쉬 토큰db 저장을 서비스로 옮겨서 저장 되어있는 리프레쉬를 호출
        Cookie refreshCookie = new Cookie("refreshToken", member.getMemberRefreshToken());
        refreshCookie .setHttpOnly(true);
        refreshCookie .setSecure(true);
        refreshCookie .setPath("/");
        refreshCookie .setMaxAge(60 * 60 * 24 * 7);
        httpServletResponse.addCookie(refreshCookie);

        return "로그인 성공";
    }
    //내 정보 가져오기
    @GetMapping("/profile")
    public Member profile(HttpServletRequest httpServletRequest){
        //배열로 되어있음 왜 배열로 되어있는 지 의문임
        Cookie[] cookies = httpServletRequest.getCookies();

        if(cookies == null){
            throw new RuntimeException("인증이 필요합니다.");
        }

        String accessToken = "";

        for(Cookie cookie : cookies){
            if(cookie.getName().equals("accessToken")){
                accessToken = cookie.getValue();
            }
        }

        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        String loginId = (String)claims.get("memberLoginId");
        Member member = this.memberService.profile(loginId);
        return member;
    }
}
