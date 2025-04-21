package com.groo.asoccer.domain.member.member.controller;

import com.groo.asoccer.domain.member.member.dto.request.MemberLoginRequest;
import com.groo.asoccer.domain.member.member.dto.request.MemberSignupRequest;
import com.groo.asoccer.domain.member.member.entity.Member;
import com.groo.asoccer.domain.member.member.service.MemberService;
import com.groo.asoccer.global.jwt.JwtProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
                memberSignupRequest.getMemberUserId(),
                memberSignupRequest.getMemberPassword(),
                memberSignupRequest.getMemberName(),
                memberSignupRequest.getMemberPhone()
//                memberSignupRequest.getMemberStatus() 회원가입때는 필드에서 디폴트 값으로 설정
        );
        return member;
    }

    //로그인
    @PostMapping("login")
    public String login(@Valid @RequestBody MemberLoginRequest memberLoginRequest){
        Member member = memberService.login(memberLoginRequest.getMemberUserId(), memberLoginRequest.getMemberPassword());

        String token = jwtProvider.generateAccessToken(member);

        return token;
    }

}
