package com.example.secondmate.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.secondmate.common.UserStatus;
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

    // [관리자] 회원 찾기
    @Query("""
            SELECT u FROM User u
            WHERE (:status IS NULL OR u.status = :status)
            AND (:keyword IS NULL OR u.username LIKE %:keyword%
                                  OR u.name LIKE %:keyword%
                                  OR u.nickname LIKE %:keyword%)
            """)
    Page<User> searchUsers(
        @Param("status") UserStatus status,
        @Param("keyword") String keyword,
        Pageable pageable
    );

    long countByStatus(UserStatus status);
}
