package com.groo.asoccer.domain.member.member.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRestoreRequest {

    private String memberLoginId;

    private String memberPassword;
}
