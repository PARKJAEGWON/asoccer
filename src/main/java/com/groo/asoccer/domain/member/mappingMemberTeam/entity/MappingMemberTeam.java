package com.groo.asoccer.domain.member.mappingMemberTeam.entity;

import com.groo.asoccer.domain.member.mappingMemberTeam.entity.enums.TeamApplicationStatus;
import com.groo.asoccer.domain.member.mappingMemberTeam.entity.enums.TeamRole;
import com.groo.asoccer.domain.member.member.entity.Member;
import com.groo.asoccer.domain.member.team.entity.Team;
import com.groo.asoccer.global.baseEntity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class MappingMemberTeam extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamRole teamRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamApplicationStatus teamApplicationStatus;
}
