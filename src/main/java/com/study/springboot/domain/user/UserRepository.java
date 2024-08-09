package com.study.springboot.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    //Optional 클래스는 null 값을 포함할 수 있는 객체를 감싸는 용도로 사용
    //용자가 존재하면 Optional 객체 안에 해당 User 객체가 담기고,
    // 존재하지 않으면 빈 Optional 객체가 반환
}
