package com.example.secondmate.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.secondmate.entity.User;
import com.example.secondmate.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountDetailsService implements UserDetailsService{
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 클라이언트에서 전송된 사용자 아이디(username)
        User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 사용자"));

        // user 데이터를 details 형태애 적용하여 클라이언트로 전송
        // user 데이터에는 username, password, role, 사용자 정보 등이 존재
        return new AccountDetails(user);
    }
}
