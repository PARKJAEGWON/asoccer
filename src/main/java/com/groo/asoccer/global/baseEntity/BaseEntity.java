package com.groo.asoccer.global.baseEntity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass //baseEntity라는걸 알려주는 역할 상속받은 entity들은 이클래스의 필드를 공통적으로 매핑 함 (따로 db테이블은 생성하지않음)
@EntityListeners(AuditingEntityListener.class) //Auditing를 활성화해서 생성일 수정일 등을 자동으로 감지하기 위한 JPA 이벤트 리스너
@AllArgsConstructor(access = AccessLevel.PROTECTED)//(access = AccessLevel.PROTECTED) 이 부분은 생성자가 생성 될 때 PROTECTED로 만들어줘서 상속 받은 클래스에서만 사용하게 만듬
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @CreatedDate
    private LocalDateTime createDateTime;

    @LastModifiedDate
    private LocalDateTime modifyDateTime;
}
