package com.groo.asoccer.domain.member.team.entity;

import com.groo.asoccer.domain.member.team.entity.enums.TeamLevel;
import com.groo.asoccer.global.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
public class Team extends BaseEntity {

    @Column(nullable = false, length = 50, unique = true)
    private String teamName;

    private String teamLogoUrl;

    //    @Convert(converter = TeamLevelConverter.class)  // "R", "E", "D" 등으로 DB 저장 쓰려다 말았음
    //    private String teamLevel;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TeamLevel teamLevel;

    @Column(length = 20)
    private String teamAverageAge;
}
