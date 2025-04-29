package com.groo.asoccer.domain.member.member.scheduler;

import com.groo.asoccer.domain.member.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberDeletionScheduler {
    private final MemberService memberService;

    //회원탈퇴
    //cron 주기적으로 작업을 자동 실행해줌
    // 0   0   0   *   *   ?
    //초   분  시  일  월  요일
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteWithdrawMembers(){
        memberService.deleteWithdrawMembers();
    }
}
