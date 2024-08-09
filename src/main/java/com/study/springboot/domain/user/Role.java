package com.study.springboot.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    // 특정한 상수 값들의 집합을 정의
    // 필드는 상태 등 상수에 관한 상태 등 추가 정보를, 메서드는 이러한 필드에 접근하거나 조작하는 역할
    // 스프링 시큐리티에서는 권한 코드에 항상 ROLE_ 이 앞에 있어야만 한다

    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자");

    private final String key;
    private final String title;

}
