package com.example.secondmate.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.secondmate.common.UserRole;
import com.example.secondmate.common.UserStatus;
import com.example.secondmate.dto.UserCreateDTO;
import com.example.secondmate.dto.UserUpdateDTO;
import com.example.secondmate.dto.UserUpdatePasswordDTO;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 관리자 계정 생성
    public void createAdmin() {
        // admin 계정이 존재하는 경우
        if(userRepository.findByUsername("admin").isPresent()) {
            return;
        }

        User admin = User.builder()
                         .username("admin")
                         .password(passwordEncoder.encode("1234"))
                         .name("관리자")
                         .nickname("관리자")
                         .phone("010-1234-5678")
                         .email("admin@test.com")
                         .address("-")
                         .latitude(0.0)
                         .longitude(0.0)
                         .role(UserRole.ROLE_ADMIN)
                         .status(UserStatus.ACTIVE)
                         .build();
        userRepository.save(admin);
    }

    // 회원가입
    public int register(UserCreateDTO userCreateDTO) {
        User user = User.builder()
                        .username(userCreateDTO.getUsername())
                        .password(passwordEncoder.encode(userCreateDTO.getPassword()))
                        .name(userCreateDTO.getName())
                        .nickname(userCreateDTO.getNickname())
                        .phone(userCreateDTO.getPhone())
                        .email(userCreateDTO.getEmail())
                        .address(userCreateDTO.getAddress())
                        .latitude(userCreateDTO.getLatitude())
                        .longitude(userCreateDTO.getLongitude())
                        .role(UserRole.ROLE_USER)
                        .status(UserStatus.ACTIVE)
                        .build();
        userRepository.save(user);
        return 1;
    }

    // 아이디 중복 확인
    public boolean isDuplicateId(String username) {
        return userRepository.existsByUsername(username);
    }

    // 닉네임 중복 확인
    public boolean isDuplicateNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 로그인 회원 정보 조회
    public User loginUser(String username, String password) {
        User user = userRepository.findByUsername(username)
                                  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디"));

        if(!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        return user;
    }

    public User getUser(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
    }

    // 아이디 찾기
    public String findUsername(String name, String phone, String email) {
        User user = userRepository.findByNameAndPhoneAndEmail(name, phone, email)
                                  .orElseThrow(() -> new IllegalArgumentException("입력한 정보와 일치하지 않음"));
        return user.getUsername();
    }

    // 비밀번호 찾기 정보 확인
    public void verifyPasswordResetUser(String username, String name, String phone, String email) {
        userRepository.findByUsernameAndNameAndPhoneAndEmail(username, name, phone, email)
                      .orElseThrow(() -> new IllegalArgumentException("입력한 정보와 일치하는 사용자 없음"));
    }

    // 비밀번호 찾기 후 새 비밀번호로 변경
    public int resetPassword(String username, String name, String phone, String email, String newPassword) {
        User user = userRepository.findByUsernameAndNameAndPhoneAndEmail(username, name, phone, email)
                                  .orElseThrow(() -> new IllegalArgumentException("입력한 정보와 일치하는 사용자 없음"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return 1;
    }
    
    // 회원 정보 수정
    public int updateUser(Long userId, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        user.setName(userUpdateDTO.getName());
        user.setNickname(userUpdateDTO.getNickname());
        user.setPhone(userUpdateDTO.getPhone());
        user.setEmail(userUpdateDTO.getEmail());
        user.setAddress(userUpdateDTO.getAddress());
        user.setLatitude(userUpdateDTO.getLatitude());
        user.setLongitude(userUpdateDTO.getLongitude());

        userRepository.save(user);
        return 1;
    }

    // 비밀번호 수정
    public int updatePassword(Long userId, UserUpdatePasswordDTO updatePasswordDTO) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        
        // 기존 비밀번호 확인
        if(!passwordEncoder.matches(updatePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        // 새 비밀번호 변경
        user.setPassword(passwordEncoder.encode(updatePasswordDTO.getNewPassword()));
        userRepository.save(user);
        return 1;
    }

    // 회원 탈퇴
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자"));
        userRepository.delete(user);
    }
}
