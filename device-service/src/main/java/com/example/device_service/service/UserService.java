package com.example.device_service.service;

import com.example.device_service.dto.DeviceDto;
import com.example.device_service.dto.UserSyncRequest;
import com.example.device_service.model.Device;
import com.example.device_service.model.User;
import com.example.device_service.repository.DeviceRepository;
import com.example.device_service.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void syncUsers(UserSyncRequest request, boolean delete){
        Set<UUID> incomingUserIds = request.getUserIds();
        if(!delete) {

            Set<UUID> existingUserIds = userRepository.findAll().stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());

            Set<UUID> newUserIds = incomingUserIds.stream()
                    .filter(id -> !existingUserIds.contains(id))
                    .collect(Collectors.toSet());

            if (!newUserIds.isEmpty()) {
                newUserIds.forEach(id -> {
                    User user = new User();
                    user.setId(id);
                    userRepository.save(user);
                });
            }
        }
        else{

            if(!incomingUserIds.isEmpty()) {
                incomingUserIds.forEach(userRepository::deleteById);
            }
        }
    }

    public User getUserById(UUID id){
        return userRepository.findById(id).get();
    }




}
