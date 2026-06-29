package com.example.secondmate.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.secondmate.dto.CommentDTO;
import com.example.secondmate.security.AccountDetails;
import com.example.secondmate.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

    // 댓글 등록
    @PostMapping
    public ResponseEntity<Integer> create(@RequestBody CommentDTO commentDTO, @AuthenticationPrincipal AccountDetails accountDetails) {
        // 로그인 안함
        if(accountDetails == null) {
            return ResponseEntity.status(401).build();
        }

        //로그인한 사용자 닉네임 보내기
        commentDTO.setWriter(accountDetails.getNickname());
        int result = commentService.saveComment(commentDTO);
        return ResponseEntity.ok(result);
    }

    // 게시글 별 댓글 목록 조회
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(commentService.getCommentsByProductId(productId));
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Integer> update(
            @RequestBody CommentDTO commentDTO, 
            @PathVariable Long commentId,
            @AuthenticationPrincipal AccountDetails accountDetails) {
        // 로그인했니?
        if(accountDetails == null) {
            return ResponseEntity.status(401).build();
        }

        // 댓글 작성자만 수정 가능
        boolean isOwner = commentService.isCommentOwner(commentId, accountDetails.getUserId());
        if(!isOwner) {
            return ResponseEntity.status(403).build();
        }

        int result = commentService.updateComment(commentId, commentDTO);

        return ResponseEntity.ok(result);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> delete(@PathVariable Long commentId, @AuthenticationPrincipal AccountDetails accountDetails) {
        // 로그인했니?
        if(accountDetails == null) {
            return ResponseEntity.status(401).build();
        }

        // 댓글 작성자만 삭제 가능
        boolean isOwner = commentService.isCommentOwner(commentId, accountDetails.getUserId());
        if(!isOwner) {
            return ResponseEntity.status(403).build();
        }

        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }
}
