package com.groo.asoccer.domain.member.member.repository;

import com.groo.asoccer.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    //맴버 id를 비교할 때 사용해야함 헷갈려하는 부분인 equals나 optinal은 null값을 비교할 때 씀
    boolean existsByMemberLoginId(String memberLoginId);
    Optional<Member> findByMemberLoginId(String memberLoginId);
    //findByMemberLoginId 옵셔널로 만들어서 프로필 메소드사용 불가 리펙토링때 공부할 요소
    //JPA에서는 by뒤에는 무조건 엔티티의 필드명과 일치해야함
    Member findProfileByMemberLoginId(String memberLoginId);
    Optional<Member> findByMemberRefreshToken(String memberRefreshToken);
}
