package com.example.secondmate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserCreateDTO {
    private String username;
    private String password;
    private String name;
    private String nickname;
    private String phone;
    private String email;
    private String address;
    private Double latitude;
    private Double longitude;
}
