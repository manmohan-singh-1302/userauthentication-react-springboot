package com.example.userauthentication_react_springboot.service;

import com.example.userauthentication_react_springboot.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

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
}
