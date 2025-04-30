package com.groo.asoccer.domain.member.team.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class TeamCreateRequestDto {

    @NotNull
    private String teamName;

    private String teamLogoUrl;

    private String teamAverageAge;
}
