package com.study.springboot.domain.posts;

import com.study.springboot.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
@Entity
/*
Getter
클래스 내 포든 필드의 Getter 메서드 생성

NoArgsConstructor
기본생성자 public Posts() {} 생성

Entity
테이블과 링크될 클래스임을 나타냄
기본값으로 클래스의 카멜케이스 이름을 언더스코어 네이밍(_)으로 테이블 이름을 매칭
SalesManager.java -> sales_manager table
Entity 클래스에서는 절대 Setter 메서드를 만들지 않음
- 클래스의 인스턴스 값이 언제 어디서 변해야하는지 코드상으로 명확하게 구분할 수 없어짐
 */
public class Posts extends BaseTimeEntity {
    /*
    Posts 클래스는 실제 DB의 테이블과 매칭될 클래스(Entity 클래스)
    DB 데이터에 작업할 경우 실제 쿼리보다는 Entity 클래스의 수정을 통해 작업(JPA)
     */
    @Id // 해당 테이블의 PK 필드
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // PK 생성 규칙, 자동으로 증가시키는 기능
    private Long id;

    @Column(length = 500,nullable = false)
    // Column 선언을 하지 않아도 해당 클래스의 필도은 모두 칼럼
    // 그러나 기본값 외에 추가 변경이 필요한 옵션이 있으면 사용
    // 기본 사이즈 255를 500으로, 타입을 text로 변경하고 싶거나 등
    private String title;

    @Column(columnDefinition = "TEXT",nullable = false)
    private String content;

    private String author;
    @Builder
    /*
    해당 클래스의 빌더 패턴 클래스 생성
    생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함
    Setter, 생성자 대신 값을 채워 DB에 삽입
    생성자는 지금 채워야 할 필드가 무엇인지 명확하게 지정할 수 없음
    빌드는 어느 필드에 어떤 값을 채워야할지 명확하게 인지할 수 있음
     */
    public Posts(String title, String content, String author){
        this.title=title;
        this.content=content;
        this.author=author;
    }

    public void update(String title,String content){
        this.title=title;
        this.content=content;
    }
}
