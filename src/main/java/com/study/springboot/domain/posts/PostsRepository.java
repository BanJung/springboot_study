package com.study.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts,Long> {
    // Posts 클래스로 Database를 접근하게 해줄 JpaRepository
    // DB Layer 접근자 aka Dao(mybatis)
    // JpaRepository<Posts,Long>를 상속하면 기본적인 CRUD 메서드 자동 생성
    // Entity 클래스와 해당 Entity Repository는 함께 위치해야함
    // Entity 클래스는 기본 Repository 없이는 제대로 역할할 수 없음
}
