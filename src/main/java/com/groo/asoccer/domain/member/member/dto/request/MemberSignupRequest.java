package com.groo.asoccer.domain.member.member.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequest {
    @NotNull
    private String memberLoginId;
    @NotNull
    private String memberPassword;
    @NotNull
    private String memberName;
    @NotNull
    private String memberPhone;
    @NotNull
    private int memberStatus;
}
