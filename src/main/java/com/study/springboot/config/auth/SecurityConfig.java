package com.study.springboot.config.auth;

import com.study.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
// Spring Security 설정들을 활성화
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    // 다른 컴포넌트에서 의존성 주입을 통해 사용할거다!
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers->headers.frameOptions(frameOptions-> frameOptions.disable()));
        /*
         h2-console 화면을 사용하기 위해 해당 옵션 disable
         1. csrf 공격 보호 비활성화
         CSRF; 공격자가 사용자의 인증 세션을 악용하여 원치 않는 요청을 보낼 수 있게 하는 공격에 대한 보호를 비활성화한다는 의미
         2. X-Frame-Options 헤더 비활성화
         클릭재킹(clickjacking); 사용자가 모르는 사이에 다른 웹사이트의 프레임 안에 악성 콘텐츠가 로드되어 클릭을 유도하는 공격
         을 방지하기 위한 HTTP 헤더인 X-Frame-Options을 비활성화
         */

        http
                .authorizeRequests()
                .requestMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile").permitAll()
                .requestMatchers("/api/v1/**").hasRole(Role.USER.name())
                .anyRequest().authenticated()
                .and()
                .logout(logout->logout.logoutSuccessUrl("/"));
        /*
        1. authorizeRequests : URL별 권한 관리 설정
        2. requestMatchers : 권한 관리 대상 지정(URL, HTTP 메소드 별)
        3. anyRequest : 설정된 값 이외 나머지 URL에 대해
            authenticated : 인증된 사용자만 허용
        4. logout(logout->logout.logoutSuccessUrl("/")) : 로그아웃 성공 시 / 주소로 이동
         */

        http
                .oauth2Login(oauth2->oauth2.userInfoEndpoint(userInfo-> userInfo
                        .userService(customOAuth2UserService)));
        /*
        1. oauth2 로그인 성공 이후, 사용자 정보를 가저올 설정 담당
        2. userService : 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록
        3. 리소스 서버(소셜 서비스들)에서 사용자의 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시
         */


        return http.build();
    }


}
