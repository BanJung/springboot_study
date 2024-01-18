package com.study.springboot.web.dto;

import com.study.springboot.domain.posts.Posts;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostsSaveRequestDto {
    private String title;
    private String content;
    private String author;
    @Builder
    public PostsSaveRequestDto(String title,String content, String author){
        this.title=title;
        this.content=content;
        this.author=author;
    }


    public Posts toEntity() {
        return Posts.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
    /*
    Request/Response 클래스를 Entity 클래스로 사용해서는 안됨!
    -> Entity 클래스는 데이터베이스와 맞닿은 핵심클래스
    Entity 클래스를 기준으로 테이블이 생성되고, 스키마가 변경
    화면(Request/Response 클래스) 변경을 위해 테이블 변경은 비효율

    Entity : 수많은 서비스 클래스, 비즈니스 로직의 기준
    Request/Response용 DTO : view를 위한 클래스(자주 변경)

    View Layer와 DB Layer의 역할 분리를 철저하게!
        Controller에서 결과값으로 여러 테이블을 조인해서 줘야할 경우 -> Entity 클래스만으로 표현 어려운 경우가 많음
     */
}
