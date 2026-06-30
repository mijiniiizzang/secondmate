package com.example.secondmate.security;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.secondmate.common.UserStatus;
import com.example.secondmate.entity.User;
import com.example.secondmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 클라이언트에서 전송된 사용자 아이디(username)
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));

        // 정지 기간이 끝났으면 자동으로 정상 계정으로 변경
        if (user.getStatus() == UserStatus.SUSPENDED
                && user.getSuspendedUntil() != null
                && !LocalDateTime.now().isBefore(user.getSuspendedUntil())) {
            
            user.setStatus(UserStatus.ACTIVE);
            user.setSuspendedUntil(null);
        }

        // 정지 기간이면 로그인 차단
        if (user.getStatus() == UserStatus.SUSPENDED) {
            String suspendedUntil = user.getSuspendedUntil().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            throw new DisabledException(user.getSuspensionCount() + "|" + suspendedUntil);
        }

        // user 데이터를 details 형태애 적용하여 클라이언트로 전송
        // user 데이터에는 username, password, role, 사용자 정보 등이 존재
        return new AccountDetails(user);
    }
}
