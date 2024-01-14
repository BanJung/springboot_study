package com.study.springboot.web.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class HelloResponseDtoTest {

    @Test
    public void 롬복_기능_테스트(){
        String name="test";
        int amount=1000;

        HelloResponseDto dto=new HelloResponseDto(name,amount);

        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);
        /*
        검증하고 싶은 대상을 메소드 인자로 받음
        메서드 체이닝이 지원되어 isEqualTo같이 메서드 이어서 사용가능
        isEqualTo는 assertThat의 값과 isEqualTo값이 같은 때만 성공
         */

    }
}