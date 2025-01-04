package com.example.user_service.service;

import com.example.user_service.dto.*;
import com.example.user_service.model.User;
import com.example.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{

    @Autowired
    private final UserRepository userRepository;

    public UUID createUser(UserRequest userRequest){
        User user = User.builder()
                .username(userRequest.getUsername())
                .password(userRequest.getPassword())
                .role(userRequest.getRole())
                .build();

        userRepository.save(user);
        return user.getId();
    }

    public UserSyncRequest getAllUsers() {
        List<User> users = userRepository.findAll();

        return UserSyncRequest.builder()
                .userIds(users.stream().map(User::getId).collect(Collectors.toSet()))
                .build();
    }


    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .password(user.getPassword())
                .build();
    }

    public void deleteById(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()){
            userRepository.deleteById(id);
        }
        else{
            throw new RuntimeException("User not found with the id: "+id);
        }
    }

    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<com.example.user_service.model.User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            var userObj = user.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword())
                    .roles(userObj.getRole())
                    .build();
        } else {
            throw new UsernameNotFoundException(username);
        }
    }

    public UUID getIdByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::getId).orElse(null);
    }

}
