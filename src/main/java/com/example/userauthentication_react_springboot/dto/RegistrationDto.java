package com.example.userauthentication_react_springboot.dto;

import com.example.userauthentication_react_springboot.model.Role;
import com.example.userauthentication_react_springboot.model.User;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private String password;

    private Collection<Role> roles;

    public User toUser(){
        User user = new User();
        user.setFirstName(this.firstName);
        user.setLastName(this.lastName);
        user.setEmail(this.email);
        user.setUsername(this.username);
        user.setPassword(this.password);
        user.setRole(new ArrayList<>(Collections.singleton(new Role("USER"))));
        return user;
    }
}
