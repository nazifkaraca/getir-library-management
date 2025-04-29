package com.getir.library_management.service.impl;

import com.getir.library_management.dto.User.UserResponseDto;
import com.getir.library_management.dto.User.UpdateUserRequestDto;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.custom.UserNotFoundException;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private static final String USER_NOT_FOUND = "User not found.";

    // Update
    @Override
    public UserResponseDto updateUser(Long id, UpdateUserRequestDto request) {
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        User updatedUser = userRepository.save(user);

        return modelMapper.map(updatedUser, UserResponseDto.class);
    }

    // Get user by id
    @Override
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        return modelMapper.map(user, UserResponseDto.class); // Direct mapping
    }

    // Get all users
    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class)) // Map each User to UserResponseDto
                .toList();
    }

    // Hard Delete
    @Override
    public void hardDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        userRepository.delete(user);
    }

    // Soft Delete
    @Override
    public void softDeleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));

        user.setMarkedAsDeleted(true);
        userRepository.save(user);
    }
}
