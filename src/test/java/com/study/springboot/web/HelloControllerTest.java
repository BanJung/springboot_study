package com.study.springboot.web;

import com.study.springboot.config.auth.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = HelloController.class,
            excludeFilters = {
            @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE,classes = SecurityConfig.class)
            }
)
/*
@ExtendWith(SpringExtension.class)
JUnit4 :@RunWith(SpringRunner.class)
Spring의 기능을 JUnit 5 테스트에 적용하도록 확장
*/

 /*
@WebMvcTest(controllers = HelloController.class)
Web(Spring MVC)에 집중할 수 있는 어노테이션
선언할 경우 @Controller, @ControllerAdivce 등 외부 연동과 관련된 부분만 사용할 수 있음
단, @Service, @Component, @Repository 등은 X. 즉, JPA 테스트 X
*/

public class HelloControllerTest {

    @Autowired
    private MockMvc mvc;
    /*
    스프링이 관리하는 빈(Bean)을 주입받음(Autowired)
    여기서 주입받은 빈은 *MockMvc로, 웹 API를 테스트할 때 사용
    * MockMvc : 스프링 MVC 테스트의 시작점
     */
    @Test
    @WithMockUser(roles = "USER")
    public void hello가_리턴된다() throws Exception{
        String hello="hello";
        mvc.perform(get("/hello"))
                .andExpect(status().isOk())
                .andExpect(content().string(hello));
        /*
        1. MockMvc를 통해 /hello 주소로 HTTP GET을 요청
            체이닝이 지원되어 아래와 같이 여러 검증 기능을 이어서 선언 가능
        2. mvc.perform의 결과(HTTP Header의 Status)를 검증 : OK(200)인지 검증
        3. mvc.perform의 결과(응답 본문 내용)를 검증 : "hello"가 맞는지 검증

         */
    }

    @Test
    @WithMockUser(roles = "USER")
    public void helloDto가_리턴된다() throws Exception{
        String name="hello";
        int amount=1000;
        mvc.perform(
                get("/hello/dto")
                        .param("name",name)
                        .param("amount",String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name",is(name)))
                .andExpect(jsonPath("$.amount",is(amount)));

    }
}