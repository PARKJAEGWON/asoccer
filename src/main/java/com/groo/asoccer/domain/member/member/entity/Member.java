package com.groo.asoccer.domain.member.member.entity;

import com.groo.asoccer.global.baseEntity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;


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


//    @JsonIgnore
    private String memberRefreshToken;

//    private Long memberLeaderTeamId;
}
