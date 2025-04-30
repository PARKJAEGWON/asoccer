package com.groo.asoccer.domain.member.member.entity;

import com.groo.asoccer.domain.member.mappingMemberTeam.entity.MappingMemberTeam;
import com.groo.asoccer.global.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//상속받은곳에서 @NoArgsConstructor(access = AccessLevel.PROTECTED) 사용시 에러 터짐 중복 오류같은데 알아봐야할 것 같음
@SuperBuilder
public class Member extends BaseEntity {


    @NotNull
    @Column(nullable = false, length = 50, unique = true)
    private String memberLoginId;

    @NotNull
    @Column(nullable = false)
    private String memberPassword;

    @NotNull
    @Column(nullable = false, length = 50)
    private String memberName;

    @NotNull
    @Column(nullable = false, length = 15)
    private String memberPhone;

    @NotNull
    @Column(nullable = false)
    private int memberStatus = 0; // 0:이용중, 8:회원탈퇴, 9:정지

    private LocalDateTime withdrawDateTime;

//    @JsonIgnore
    private String memberRefreshToken;

    @OneToMany(mappedBy = "member")
    private List<MappingMemberTeam> memberTeams = new ArrayList<>();

//    private Long memberLeaderTeamId;
}
