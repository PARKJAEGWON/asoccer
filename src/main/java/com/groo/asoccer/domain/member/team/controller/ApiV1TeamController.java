package com.groo.asoccer.domain.member.team.controller;

import com.groo.asoccer.domain.member.team.dto.request.TeamCreateRequestDto;
import com.groo.asoccer.domain.member.team.entity.Team;
import com.groo.asoccer.domain.member.team.service.TeamService;
import com.groo.asoccer.global.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams")
public class ApiV1TeamController {
    private final TeamService teamService;
    private final JwtProvider jwtProvider;

    //팀 생성
    @PostMapping("")
    public Team create(@Valid @RequestBody TeamCreateRequestDto teamCreateRequestDto,
                       HttpServletRequest httpServletRequest){
        String accessToken = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies) {
                if("accessToken".equals(cookie.getName())){
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }
        if(accessToken == null){
            throw new RuntimeException("인증이 필요합니다.");
        }
        Map<String, Object> claims = jwtProvider.getClaims(accessToken);
        Long memberId = ((Integer) claims.get("memberId")).longValue();

        Team team = teamService.create(
                memberId,
                teamCreateRequestDto.getTeamName(),
                teamCreateRequestDto.getTeamLogoUrl(),
                teamCreateRequestDto.getTeamAverageAge()
                );
        return team;
    }
}
