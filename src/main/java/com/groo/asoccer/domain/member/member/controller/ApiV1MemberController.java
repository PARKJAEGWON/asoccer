package com.groo.asoccer.domain.member.member.controller;

import com.groo.asoccer.domain.member.member.dto.request.MemberLoginRequest;
import com.groo.asoccer.domain.member.member.dto.request.MemberRestoreRequest;
import com.groo.asoccer.domain.member.member.dto.request.MemberSignupRequest;
import com.groo.asoccer.domain.member.member.dto.request.MemberUpdateRequest;
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

    //회원 정보 가져오기
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
        Member member = this.memberService.findByLoginId(loginId);
        return member;
    }

    //회원수정
    @PatchMapping("/profile")
    public Member update(@RequestBody MemberUpdateRequest memberUpdateRequest,HttpServletRequest httpServletRequest){
        Cookie[] cookies = httpServletRequest.getCookies();

        String accessToken = null;

        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("accessToken")){
                    accessToken = cookie.getValue();
                }
            }
        }
        if(accessToken == null) {
            throw  new RuntimeException("로그인이 필요합니다.");
        }

        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        //JWT의 claims 값을 꺼내면 오브젝트 타입으로 반환됨 실제로 Integer로 들어 있음 하지만 db는 Long으로 변환해야해서 longValue()를 사용해서 Integer를 long타입으로 변환
        //오브젝트는 한번에 롱으로 넘어갈 수 없음 같은 객첵 타입인 Integer로 변환해주고 다음 longValue()이용해서 Long으로 넘어가줘야함
        Long loginId = ((Integer)claims.get("memberId")).longValue();

        Member member = memberService.update(loginId,
                memberUpdateRequest.getMemberName(),
                memberUpdateRequest.getMemberPassword(),
                memberUpdateRequest.getMemberPhone());

        return member;
    }

    //회원탈퇴상태 요청
    @DeleteMapping("/withdraw")
    public void withdraw(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

        String accessToken = null;

        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("accessToken".equals(cookie.getName())){
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        if(accessToken == null){
            throw new RuntimeException("인증이 필요합니다.");
        }
        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        Long memberId = ((Integer) claims.get("memberId")).longValue();

        memberService.withdrawMember(memberId);

        Cookie accessCookie  = new Cookie("accessToken", null);
        accessCookie .setPath("/");
        accessCookie .setMaxAge(0);
        httpServletResponse.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie .setPath("/");
        refreshCookie .setMaxAge(0);
        httpServletResponse.addCookie(refreshCookie);
    }

    //회원복구
    @PostMapping("/restore")
    public void restore(@RequestBody MemberRestoreRequest memberRestoreRequest){
            memberService.restore(memberRestoreRequest.getMemberLoginId(), memberRestoreRequest.getMemberPassword());
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

    //로그아웃
    @GetMapping("/logout")
    public void logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse){

        Cookie[] cookies = httpServletRequest.getCookies();

        if(cookies != null){
            // 변수 초기화 찾지 못했을 때 빈문자열로 초기화
            String accessToken = "";
            // 모든 쿠키를 순회하면서 "accessToken"이름을 가진 쿠키 찾기
            for(Cookie cookie : cookies){
                //쿠키.getName 쿠키클래스의 메소드 규약 "accessToken"내가 로그인할 때 이 이름으로 쿠키를 보냈음
                if(cookie.getName().equals("accessToken")){
                    accessToken = cookie.getValue();
                }
            }

            if(!accessToken.isEmpty()){
                    Map<String, Object> claims = jwtProvider.getClaims(accessToken);
                    String loginId = (String) claims.get("memberLoginId");
                    Member member = memberService.findByLoginId(loginId);
                    memberService.logout(member);
            }
        }

        Cookie accessCookie  = new Cookie("accessToken", null);
        accessCookie .setPath("/");
        accessCookie .setMaxAge(0);
        httpServletResponse.addCookie(accessCookie);

        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie .setPath("/");
        refreshCookie .setMaxAge(0);
        httpServletResponse.addCookie(refreshCookie);
    }
}
