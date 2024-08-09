package com.study.springboot.config.auth.dto;

import com.study.springboot.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {
    // 인증된 사용자 정보만 필요
    /*
    SessionUser 클래스가 필요한 이유
    : User 정보를 담은 클래스를 직렬화*해야하기 때문
    서버에서 클라이언트로 사용자 정보를 전송하고, 사용자의 상태를 저장하고 관리하기 위해 직렬화 필요
    그러나 User 클래스는 엔티티이기 때문에 이를 직렬화 시킨다면 관계를 맺은 엔티티도 직렬화 대상
    성능 이슈, 부수 효과가 발생할 수 있기 때문에 '직렬화 기능을 가진 세션 Dto(=SessionUser)'를 사용
     */

    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}