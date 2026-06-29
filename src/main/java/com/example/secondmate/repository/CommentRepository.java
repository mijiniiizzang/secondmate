package com.example.secondmate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.secondmate.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 댓글 내림차순으로 불러오기
    List<Comment> findByProduct_ProductIdOrderByCreatedAtDesc(Long productId);

    // 특정 회원이 쓴 댓글 불러오기
    List<Comment> findByUser_UserId(Long userId);

    // 상품 게시물을 지우면 댓글도 삭제
    void deleteByProduct_ProductId(Long productId);
}
