package com.example.userauthentication_react_springboot.dto;

import com.example.userauthentication_react_springboot.model.Role;
import com.example.userauthentication_react_springboot.model.User;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String username;

    private Collection<Role> roles;

    public UserDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.lastName = user.getLastName();
        this.firstName = user.getFirstName();
        this.username = user.getUsername();
        this.roles = user.getRole();
    }

}
