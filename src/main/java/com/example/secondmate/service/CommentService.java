package com.example.secondmate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.dto.CommentDTO;
import com.example.secondmate.entity.Comment;
import com.example.secondmate.entity.Product;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.CommentRepository;
import com.example.secondmate.repository.ProductRepository;
import com.example.secondmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // 댓글 등록 : 댓글 추가 + 게시글 테이블 수정
    @Transactional
    public int saveComment(CommentDTO commentDTO) {
        Product product = productRepository.findById(commentDTO.getProductId())
                                           .orElseThrow(() -> new IllegalArgumentException("상품 없음"));

        User user = userRepository.findById(commentDTO.getUserId())
                                  .orElseThrow(() -> new IllegalArgumentException("유저 없음"));

        Comment comment = Comment.builder()
                                 .content(commentDTO.getContent())
                                 .product(product)
                                 .user(user)
                                 .build();

        product.setCommentCount(product.getCommentCount() + 1);
        commentRepository.save(comment);
        return 1;
    }

    // 특정 상품의 댓글 전체 조회
    public List<CommentDTO> getCommentsByProductId(Long productId) {
        List<Comment> comments = commentRepository.findByProduct_ProductIdOrderByCreatedAtDesc(productId);

        return comments.stream().map(this::toDTO)
                                .collect(Collectors.toList());
    }

    // 댓글 수정
    public int updateComment(Long commentId, CommentDTO commentDTO) {
        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> new IllegalArgumentException("댓글 없음"));
        comment.setContent(commentDTO.getContent());
        commentRepository.save(comment);
        return 1;
    }

    // 댓글 삭제 : 댓글 제거 + 상품 테이블 수정
    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId).get();
        commentRepository.deleteById(commentId);
        Product product = productRepository.findById(comment.getProduct().getProductId()).get();
        product.setCommentCount(product.getCommentCount() - 1);
        productRepository.save(product);
    }

    // Entity -> DTO
    private CommentDTO toDTO(Comment comment) {
        return CommentDTO.builder()
                         .commentId(comment.getCommentId())
                         .writer(comment.getUser().getNickname())
                         .content(comment.getContent())
                         .createdAt(comment.getCreatedAt())
                         .productId(comment.getProduct().getProductId())
                         .build();
    }

    // 댓글 작성자인지 확인하기
    public boolean isCommentOwner(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                                           .orElseThrow(() -> new IllegalArgumentException("없는 댓글 번호"));
        return comment.getUser().getUserId().equals(userId);
    }
}
