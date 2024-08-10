package com.study.springboot.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
// 어노테이션이 생성될 수 있는 위치 지정 : 메서드의 파리미터로 선언된 객체에서만 사용 가능
@Retention(RetentionPolicy.RUNTIME)
// 어노테이션이 런타임에도 존재하도록 설정
public @interface LoginUser {
}
