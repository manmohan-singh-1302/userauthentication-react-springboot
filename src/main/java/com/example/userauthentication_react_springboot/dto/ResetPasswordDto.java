package com.example.userauthentication_react_springboot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordDto {

    private String newPassword;

    private String confirmPassword;
}
