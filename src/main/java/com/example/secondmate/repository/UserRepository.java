package com.example.secondmate.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // 로그인용
    Optional<User> findByUsername(String username);

    // 아이디 중복 체크
    boolean existsByUsername(String username);

    // 닉네임 중복 체크
    boolean existsByNickname(String nickname);

    // 아이디 찾기
    Optional<User> findByNameAndPhoneAndEmail(String name, String phone, String email);

    // 비밀번호 찾기
    Optional<User> findByUsernameAndNameAndPhoneAndEmail(String username, String name, String phone, String email);
}
