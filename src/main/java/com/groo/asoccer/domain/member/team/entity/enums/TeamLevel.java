package com.groo.asoccer.domain.member.team.entity.enums;

import lombok.Getter;

@Getter
public enum TeamLevel {
    ROOKIE,
    ELEMENTARY,
    DYNAMIC,
    CHALLENGER,
    BREAKER,
    ACE,
    SPECIAL;
}


//    ROOKIE("R"),
//    ELEMENTARY("E"),
//    DYNAMIC("D"),
//    CHALLENGER("C"),
//    BREAKER("B"),
//    ACE("A"),
//    SPECIAL("S");

//    private final String code;

//    TeamLevel(String code) {
//        this.code = code;
//    }
//
//    //백엔드에서 직접 참조를 해서 상수값으로 불러와야 하는 걸 fromCode를 이용해서 db에 저장하듯이 호출할 수 있음
//    public static TeamLevel fromCode(String code) {
//        //values() ENUM에서 제공하는 메소드 ENUM타입의 정의된 모든 상수들을 배열로 반환해준다
//        for (TeamLevel level : values()) {
//            if (level.code.equals(code)) {
//                return level;
//            }
//        }
//        throw new IllegalArgumentException("Invalid  Code:" + code);
//    }
//}

//프롬코드를 사용하지않으면 백엔드에서 이렇게 사용해야함
//String code = "R";  // DB에서 가져온 코드 값
//if ("R".equals(code)) {
//    level = TeamLevel.ROOKIE;
//}

//프롬코드를 사용하면 이렇게 사용함
//String code = "R";  // DB에서 가져온 코드 값
//TeamLevel level = TeamLevel.fromCode(code);  // 자동으로 ROOKIE 반환