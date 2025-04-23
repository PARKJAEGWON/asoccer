package com.groo.asoccer.domain.member.member.service;

import com.groo.asoccer.domain.member.member.entity.Member;
import com.groo.asoccer.domain.member.member.repository.MemberRepository;
import com.groo.asoccer.global.jwt.JwtProvider;
import com.groo.asoccer.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    //회원가입
    public Member signup(String memberLoginId, String memberPassword, String memberName,String memberPhone){

        if(memberRepository.existsByMemberLoginId(memberLoginId)){
            //Illegal잘못된Argument 매개변수Exception예외처리
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Member member = new Member();

        member.setMemberLoginId(memberLoginId);
        member.setMemberPassword(passwordEncoder.encode(memberPassword));
        member.setMemberName(memberName);
        member.setMemberPhone(memberPhone);
//        member.setMemberStatus(memberStatus);

        return memberRepository.save(member);
    }

    //로그인
    public Member login(String memberLoginId, String memberPassword){

        Optional<Member> optionalMember = this.memberRepository.findByMemberLoginId(memberLoginId);
        //isEmpty와 isPresent는 의미가 반대 isEmpty는 값이 없다면 isPresent는 값이 있다면
        if(optionalMember.isEmpty()){
            throw new RuntimeException("아이디가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        if(!passwordEncoder.matches(memberPassword, member.getMemberPassword())){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        if(member.getMemberStatus() == 9){
            throw new RuntimeException("이용이 정지된 계정입니다.");

        }else if(member.getMemberStatus() == 8){
            throw new RuntimeException("탈퇴된 계정입니다. 탈퇴일로부터 30일이 지난 후 다시 가입하실 수 있습니다.");
        }

        String refreshToken = jwtProvider.generateRefreshToken(member);
        member.setMemberRefreshToken(refreshToken);
        memberRepository.save(member);

        return member;
    }
    //로그아웃
    public void logout(Member member){
        member.setMemberRefreshToken(null);
        memberRepository.save(member);
    }

    //유저 정보
    public Member findByLoginId(String memberLoginId){
        return memberRepository.getMemberLoginId(memberLoginId);
    }

    //토큰 유효성 검증
    public boolean validateToken(String token){
        return jwtProvider.verify(token);
    }

    //토큰 갱신
    public String refreshAccessToken(String memberRefreshToken){

        Optional<Member> optionalMember = memberRepository.findByMemberRefreshToken(memberRefreshToken);
        if(optionalMember.isEmpty()){
            throw  new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
        Member member = optionalMember.get();

        String accessToken = jwtProvider.generateAccessToken(member);

        return accessToken;
    }


    // 토큰으로 securityUser로 가공된 객체 정보 가져오기
    public SecurityUser getUserFromAccessToken(String accessToken) {
        Map<String, Object> payloadBody = jwtProvider.getClaims(accessToken);
        //페이로드에서 맴버기본키 가져오기
        long memberId = (int) payloadBody.get("memberId");
        //페이로드에서 맴버로그인값 가져오기
        String memberLoginId = (String) payloadBody.get("memberLoginId");
        //권한 리스트 재설정
        List<GrantedAuthority> authorities = new ArrayList<>();
        //가공한 SecurityUser에 담아 반환
        return new SecurityUser(memberId, memberLoginId, "", authorities);
    }

}
