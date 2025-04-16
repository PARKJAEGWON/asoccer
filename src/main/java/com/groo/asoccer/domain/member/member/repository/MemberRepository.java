package com.groo.asoccer.domain.member.member.repository;

import com.groo.asoccer.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //맴버 id를 비교할 때 사용해야함 헷갈려하는 부분인 equals나 optinal은 null값을 비교할 때 씀
    boolean existsByMemberUserId(String memberUserId);
    Optional<Member> findByMemberUserId(String memberUserId);
}
