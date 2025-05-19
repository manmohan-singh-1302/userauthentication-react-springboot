package com.example.userauthentication_react_springboot.service.impl;

import com.example.userauthentication_react_springboot.model.User;
import com.example.userauthentication_react_springboot.repository.UserRepository;
import com.example.userauthentication_react_springboot.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // this method saves the user to the database using save method of UserRepository.
    @Override
    public User save(User user) {
        // encode the password only if it is not encoded. Bcrypt hash starts with $2a$
        if(!user.getPassword().startsWith("$2a$")){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    // findById method takes id as an argument and returns a list of Users if present or else returns an empty object Optional<User>.
    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    // findByUsername method takes in username of type string as an input and returns the user object if found else returns
    // an empty list i.e. of type Optional<User> if not found.
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    // findAll method returns all the user present in the database. If not present it returns an empty list.
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // deleteById method takes in id of type Long as an input and deletes the user with that id from the database.
    // !!!!!!!! Implement exception handling if a user is not found with that particular id.
    @Override
    public void deleteById(Long id) {
            userRepository.deleteById(id);
    }

    // existsByUsername takes in a username of type string as an input and returns true if a user with that username exists in the database
    // or returns false if not present.
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // existsByEmail takes in an email of type string as input and returns true if a user is present in the database with the given email
    // or returns false it not found.
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // loadUserByUsername method takes in username as an input and returns user details(username, hashed password, and authorities) as an output.
    // If the user is not found then it thorws UsernameNotFoundException.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      User user =  userRepository.findByUsername(username).orElseThrow(()-> new UsernameNotFoundException("User not found with username: "+ username));
        List<SimpleGrantedAuthority> authorities = user.getRole().stream()
                .map(role-> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    /*
    * changePassword changes the current password of the user, takes id, current password and new password as
    * an input and checks if the user with the id is present in the database or not if not
    * found throws User not found exception. Checks if the current password and the given
    * current password is matching or not if not throw runtime exception. It encodes the
    * password using password encoder encode method and save the modified user.
    * */
    @Override
    public void changePassword(Long id, String currentPassword, String newPassword){
        User user = userRepository.findById(id).orElseThrow(()->
                new RuntimeException("User not found"));

        if(!passwordEncoder.matches(currentPassword, user.getPassword())){
            throw new RuntimeException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
