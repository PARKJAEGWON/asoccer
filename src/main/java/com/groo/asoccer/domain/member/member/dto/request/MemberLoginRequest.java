package com.groo.asoccer.domain.member.member.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberLoginRequest {

    private String memberUserId;

    private String memberPassword;
}
