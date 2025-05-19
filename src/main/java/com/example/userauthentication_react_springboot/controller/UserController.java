package com.example.userauthentication_react_springboot.controller;

import com.example.userauthentication_react_springboot.dto.PasswordChangeDto;
import com.example.userauthentication_react_springboot.dto.RegistrationDto;
import com.example.userauthentication_react_springboot.dto.UserDto;
import com.example.userauthentication_react_springboot.model.User;
import com.example.userauthentication_react_springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    /* Get user by id, takes a unique ID from the URL as a parameter and calls the
    * findById method of userService to search for the user. If found, maps the User entity
    * to a UserDto or returns not found exception (HTTP 404).
    */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id){
        return userService.findById(id).map(
                user->ResponseEntity.ok(new UserDto(user)))
                        .orElse(ResponseEntity.notFound().build());
    }

    /*
    * Gets all users from the database using the findAll method of userService and maps the
    * users to a list of UserDto for sending to the client.
    * */
    @GetMapping
    public List<UserDto> getAllUsers(){
        return userService.findAll().stream().map(
                user -> new UserDto(user))
                .collect(Collectors.toList());
    }

    /*
    * createUser method creates a new user, it gets user details like firstName, lastName,
    * email, password as input from the user as an RegistrationDto object and returns and uses the
    * save method to persist the user in the database using the save method of userService.
    * Returns a ResponseEntity<UserDto> of the created user.
     */
    @PostMapping()
    public ResponseEntity<UserDto> createUser(@RequestBody RegistrationDto registrationDto){

        User user = registrationDto.toUser();
        User savedUser = userService.save(user);
        return ResponseEntity.ok(new UserDto(savedUser));
    }

    /*
    * deleteUser method deletes a user based on the id, it gets user id as an input and uses
    * the deleteById method of userService to delete the user from database. Checks for the user
    * with the id if found deletes sit and sends noContent error (HTTP 204) indicating successful
    * deletion. Else return notFound exception (HTTP 404).
    * */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id){
        if(userService.findById(id).isPresent()){
            userService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.notFound().build();
        }
    }

    /*
    * updateUser updates a user based on the id, it takes user details from the user in the form of
    * UserDto object and finds the user using the id by calling findById method of userService
    * and sets the fields as per the new details and returns the updated user details with HTTP status
    * code 200. If not found return HTTP status code 404.
    * */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto){
        return userService.findById(id)
                .map(existingUser-> {
                    existingUser.setFirstName(userDto.getFirstName());
                    existingUser.setLastName(userDto.getLastName());
                    existingUser.setEmail(userDto.getEmail());
                    existingUser.setUsername(userDto.getUsername());
                    existingUser.setRole(userDto.getRoles());
                    User updatedUser = userService.save(existingUser);
                    return ResponseEntity.ok(new UserDto(updatedUser));
                }
        ).orElse(
                ResponseEntity.notFound().build()
                );
    }

    /*
    * changePassword uses changePassword method of the userService to change the user's
    * password if successfully changed it return HTTP 200 or else return HTTP 400.
    * */
    @PutMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(@PathVariable Long id, @RequestBody PasswordChangeDto passwordChangeDto){
        try{
            userService.changePassword(id,passwordChangeDto.getCurrentPassword(), passwordChangeDto.getNewPassword());
            return ResponseEntity.notFound().build();
        }
        catch (RuntimeException e){
            return ResponseEntity.badRequest().build();
        }
    }
}
