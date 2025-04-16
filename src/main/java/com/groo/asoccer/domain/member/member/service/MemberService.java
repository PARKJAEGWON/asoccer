package com.groo.asoccer.domain.member.member.service;

import com.groo.asoccer.domain.member.member.entity.Member;
import com.groo.asoccer.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    //회원가입
    public Member signup(String memberUserId, String memberPassword, String memberName,String memberPhone){

        if(memberRepository.existsByMemberUserId(memberUserId)){
            //Illegal잘못된Argument 매개변수Exception예외처리
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        Member member = new Member();

        member.setMemberUserId(memberUserId);
        member.setMemberPassword(memberPassword);
        member.setMemberName(memberName);
        member.setMemberPhone(memberPhone);
//        member.setMemberStatus(memberStatus);

        return memberRepository.save(member);
    }

    //로그인
    public Member login(String memberUserId, String memberPassword){

        Optional<Member> optionalMember = this.memberRepository.findByMemberUserId(memberUserId);
        //isEmpty와 isPresent는 의미가 반대 isEmpty는 값이 없다면 isPresent는 값이 있다면
        if(optionalMember.isEmpty()){
            throw new RuntimeException("아이디가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        if(!member.getMemberPassword().equals(memberPassword)){
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        if(member.getMemberStatus() == 9){
            throw new RuntimeException("이용이 정지된 계정입니다.");

        }else if(member.getMemberStatus() == 8){
            throw new RuntimeException("탈퇴된 계정입니다. 탈퇴일로부터 30일이 지난 후 다시 가입하실 수 있습니다.");
        }
        return member;
    }
}
