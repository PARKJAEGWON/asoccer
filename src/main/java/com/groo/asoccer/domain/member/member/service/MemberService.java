package com.groo.asoccer.domain.member.member.service;

import com.groo.asoccer.domain.member.member.entity.Member;
import com.groo.asoccer.domain.member.member.repository.MemberRepository;
import com.groo.asoccer.global.jwt.JwtProvider;
import com.groo.asoccer.global.security.SecurityUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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

    //회원수정
    public Member update(Long id, String memberName, String memberPassword, String memberPhone){
        Optional<Member> optionalMember = memberRepository.findById(id);
            if(optionalMember.isEmpty()){
                throw new RuntimeException("해당 ID의 회원을 찾을 수 없습니다.");  
            }
            Member member = optionalMember.get();
            
            if(memberName != null && !memberName.isBlank()){
                member.setMemberName(memberName);
            }
            if(memberPassword != null && !memberPassword.isBlank()){
                member.setMemberPassword(passwordEncoder.encode(memberPassword));
            }
            if(memberPhone != null && !memberPhone.isBlank()){
                member.setMemberPhone(memberPhone);
            }
            return memberRepository.save(member);
    }
    //회원탈퇴상태 요청
    public void withdrawMember(Long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isEmpty()){
            throw new RuntimeException("아이디가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        member.setMemberStatus(8);
        member.setWithdrawDateTime(LocalDateTime.now());
        memberRepository.save(member);

        logout(member);
    }
    //회원 탈퇴
    public void deleteWithdrawMembers(){
        LocalDateTime fourteenDays = LocalDateTime.now().minusDays(14);
        List<Member> withdrawMembers = memberRepository.findByMemberStatusAndWithdrawDateTimeBefore(8, fourteenDays);
        for(Member member: withdrawMembers) {
            memberRepository.delete(member);
        }
    }

    //회원 복구
    public void restore(String memberLoginId, String rawPassword){
        Optional<Member> optionalMember = memberRepository.findByMemberLoginId(memberLoginId);
        if(optionalMember.isEmpty()){
            throw new RuntimeException("존재하지 않는 아이디입니다.");
        }
        Member member = optionalMember.get();

        if(!passwordEncoder.matches(rawPassword, member.getMemberPassword())){
            throw  new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        if(member.getMemberStatus() != 8){
            throw new RuntimeException("탈퇴 상태가 아닙니다.");
        }
        //실제로는 14일 이후 데이터 완전 소멸이라 작동하지는 않지만 혹시라도 스케줄러가 정상 작동하지않거나 데이터가 꼬일 경우를 대비해 방어적으로 남겨두는게 좋다해서 추가
        if(member.getWithdrawDateTime() == null || member.getWithdrawDateTime().plusDays(14).isBefore(LocalDateTime.now())){
            throw new RuntimeException("복구 가능 기간(14일)이 지났습니다. 고객센터에 문의 바랍니다.");
        }

        member.setMemberStatus(0);
        member.setWithdrawDateTime(null);
        memberRepository.save(member);
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
            throw new RuntimeException("탈퇴된 계정입니다. 탈퇴일로부터 14일이 지난 후 다시 가입하실 수 있습니다.");
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
        //orElseThrow를 사용안하고싶어서 타입을 옵셔널로했더니 컨트롤러도 옵셔널로 맞춰줘야함 orElseThrow로 하면 맴버로 반환됨 레파지토리는 옵셔널 인데 이걸 사용하면 맴버로
        //뿌려지는 이유를 찾아봐야겠음
        return memberRepository.findByMemberLoginId(memberLoginId).orElseThrow(() -> new RuntimeException("해당 ID의 회원을 찾을 수 없습니다."));
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
