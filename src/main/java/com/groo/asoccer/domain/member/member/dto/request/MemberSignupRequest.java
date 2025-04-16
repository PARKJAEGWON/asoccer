package com.groo.asoccer.domain.member.member.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequest {

    private String memberUserId;

    private String memberPassword;

    private String memberName;

    private String memberPhone;

    private int memberStatus;
}
