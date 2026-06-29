package com.example.secondmate.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.secondmate.entity.User;

public class AccountDetails implements UserDetails{
    
    // 로그인한 사용자의 account 정보 저장
    private User user;

    public AccountDetails(User user) {
        this.user = user;
    }

    // 로그인한 사용자의 권한 정보를 적용
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRole().name()));
    }

    // 로그인한 사용자의 패스워드 적용
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    // 로그인한 사용자의 아이디 적용
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    public User getUser() {
        return user;
    }

    public Long getUserId() {
        return user.getUserId();
    }

    // 로그인 했을 때 닉네임 보이게 하기
    public String getNickname() {
        return user.getNickname();
    }
}
