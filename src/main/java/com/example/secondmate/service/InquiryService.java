package com.example.secondmate.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.secondmate.common.InquiryStatus;
import com.example.secondmate.common.InquiryType;
import com.example.secondmate.dto.InquiryDTO;
import com.example.secondmate.entity.Inquiry;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.InquiryRepository;
import com.example.secondmate.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    // 문의 작성
    @Transactional
    public void createInquiry(Long userId, InquiryDTO inquiryDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .title(inquiryDTO.getTitle())
                .content(inquiryDTO.getContent())
                .inquiryType(inquiryDTO.getInquiryType())
                .inquiryStatus(InquiryStatus.WAITING)
                .build();

        inquiryRepository.save(inquiry);
    }

    // 로그인한 사용자의 문의 목록
    public Page<Inquiry> getMyInquiryList(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));

        return inquiryRepository.findByUserOrderByRegDateDesc(user, pageable);
    }

    // 문의 상세
    public Inquiry getInquiry(Long inquiryId) {
        return inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의"));
    }

    // 관리자 전체 문의 조회
    public Page<Inquiry> getAdminInquiryList(
            InquiryStatus inquiryStatus,
            List<InquiryType> inquiryTypes,
            Pageable pageable) {

        return inquiryRepository.searchAdminInquiries(
                inquiryStatus,
                inquiryTypes,
                pageable);
    }

    // 관리자 답변 등록
    @Transactional
    public void answerInquiry(Long inquiryId, String answer) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의"));

        inquiry.setAnswer(answer);
        inquiry.setAnsweredAt(LocalDateTime.now());
        inquiry.setInquiryStatus(InquiryStatus.ANSWERED);

        notificationService.createInquiryAnsweredNotification(inquiry);
    }

    // 답변 대기/완료 수
    public long getWaitingInquiryCount() {
        return inquiryRepository.countByInquiryStatus(InquiryStatus.WAITING);
    }

    public long getAnsweredInquiryCount() {
        return inquiryRepository.countByInquiryStatus(InquiryStatus.ANSWERED);
    }

    // 사용자 문의 수
    public long getMyInquiryCount(Long userId) {
        return inquiryRepository.countByUser_UserId(userId);
    }
}
