package com.getir.library_management.service.impl;

import com.getir.library_management.dto.user.UserResponseDto;
import com.getir.library_management.dto.user.UpdateUserRequestDto;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.ExceptionMessages;
import com.getir.library_management.exception.custom.UserNotFoundException;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.interfaces.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Dependencies for data access and object mapping
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // Updates a user's details
    @Override
    public UserResponseDto updateUser(Long id, UpdateUserRequestDto request) {
        // Retrieve user by ID or throw exception if not found
        User user = userRepository.findById(request.getId())
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        // Update user details
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // Save updated user to the database
        User updatedUser = userRepository.save(user);

        // Return mapped user response
        return modelMapper.map(updatedUser, UserResponseDto.class);
    }

    // Retrieves a single user by ID
    @Override
    public UserResponseDto getUserById(Long id) {
        // Retrieve user or throw exception if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        // Return mapped user response
        return modelMapper.map(user, UserResponseDto.class); // Direct mapping
    }

    // Retrieves all users in the system
    @Override
    public List<UserResponseDto> getAllUsers() {
        // Map all User entities to UserResponseDto and collect as a list
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponseDto.class)) // Map each User to UserResponseDto
                .toList();
    }

    // Permanently deletes a user from the database
    @Override
    public void hardDeleteUser(Long id) {
        // Retrieve user or throw exception if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        // Delete the user record from the database
        userRepository.delete(user);
    }

    // Marks a user as deleted without removing the record (soft delete)
    @Override
    public void softDeleteUser(Long id) {
        // Retrieve user or throw exception if not found
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(ExceptionMessages.USER_NOT_FOUND));

        // Set the deleted flag to true and save
        user.setMarkedAsDeleted(true);
        userRepository.save(user);
    }
}
