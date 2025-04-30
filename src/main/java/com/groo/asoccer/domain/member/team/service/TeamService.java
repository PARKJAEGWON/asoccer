package com.groo.asoccer.domain.member.team.service;

import com.groo.asoccer.domain.member.mappingMemberTeam.entity.MappingMemberTeam;
import com.groo.asoccer.domain.member.mappingMemberTeam.entity.enums.TeamApplicationStatus;
import com.groo.asoccer.domain.member.mappingMemberTeam.entity.enums.TeamRole;
import com.groo.asoccer.domain.member.mappingMemberTeam.repository.MappingMemberTeamRepository;
import com.groo.asoccer.domain.member.member.entity.Member;
import com.groo.asoccer.domain.member.member.repository.MemberRepository;
import com.groo.asoccer.domain.member.team.entity.Team;
import com.groo.asoccer.domain.member.team.entity.enums.TeamLevel;
import com.groo.asoccer.domain.member.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final MappingMemberTeamRepository mappingMemberTeamRepository;

    //팀 생성
    public Team create(Long memberId,String teamName, String teamLogoUrl,String teamAverageAge){
        Optional<Member> optionalMember = memberRepository.findById(memberId);
        if(optionalMember.isEmpty()){
            throw new IllegalArgumentException("회원이 존재하지 않습니다.");
        }
        Member member = optionalMember.get();

        Team team = new Team();
        team.setTeamName(teamName);
        team.setTeamLogoUrl(teamLogoUrl);
        team.setTeamAverageAge(teamAverageAge);
        team.setTeamLevel(TeamLevel.ROOKIE);

        if (teamRepository.existsByTeamName(team.getTeamName())) {
            throw new IllegalArgumentException("이미 존재하는 팀 이름입니다.");
        }

        teamRepository.save(team);

        MappingMemberTeam mappingMemberTeam = new MappingMemberTeam();
        mappingMemberTeam.setMember(member);
        mappingMemberTeam.setTeam(team);
        mappingMemberTeam.setTeamRole(TeamRole.LEADER);
        mappingMemberTeam.setTeamApplicationStatus(TeamApplicationStatus.APPROVED);

        return team;
    }
}
