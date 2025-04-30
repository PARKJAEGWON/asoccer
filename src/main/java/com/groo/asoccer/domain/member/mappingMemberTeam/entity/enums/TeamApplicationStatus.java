package com.groo.asoccer.domain.member.mappingMemberTeam.entity.enums;

import lombok.Getter;

@Getter
public enum TeamApplicationStatus {
    PENDING,  //대기
    APPROVED, //승인
    REJECTED;
}
