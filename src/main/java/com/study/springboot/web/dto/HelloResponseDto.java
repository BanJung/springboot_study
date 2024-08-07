package com.study.springboot.web.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
// 선언된 모든 필드의 get 메서드 생성

@RequiredArgsConstructor
//선언된 모든 final 필드만 포함된 생성자 생성

//Dto는 계층 간에 데이터 교환을 위한 객체

public class HelloResponseDto {

    private final String name;
    private final int amount;

}
