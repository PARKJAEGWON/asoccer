package com.groo.asoccer.domain.member.team.repository;

import com.groo.asoccer.domain.member.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
    boolean existsByTeamName(String teamName);
}
