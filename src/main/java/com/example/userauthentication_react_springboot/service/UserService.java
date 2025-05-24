package com.example.userauthentication_react_springboot.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.example.userauthentication_react_springboot.model.User;

public interface UserService extends UserDetailsService {
  User save(User user);
  Optional<User> findById(Long id);
  Optional<User> findByUsername(String username);
  Optional<User> findByEmail(String email);
  List<User> findAll();
  void deleteById(Long id);
  boolean existsByUsername(String username);
  boolean existsByEmail(String email);
  void changePassword(Long id, String currentPassword, String newPassword);
  void resetPassword(Long id, String newPassword);
}
