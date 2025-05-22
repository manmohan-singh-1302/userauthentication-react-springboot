package com.example.userauthentication_react_springboot.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "otptoken")
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;

    private String otp;

    @OneToOne
    @JoinColumn(name = "id", unique = true)
    private User user;

    private LocalDateTime expiryTime;
}
