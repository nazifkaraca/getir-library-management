package com.getir.library_management.service.interfaces;

import com.getir.library_management.dto.user.UserResponseDto;
import com.getir.library_management.dto.user.UpdateUserRequestDto;

import java.util.List;

public interface UserService {
    UserResponseDto updateUser(Long id, UpdateUserRequestDto request);
    UserResponseDto getUserById(Long id);
    List<UserResponseDto> getAllUsers();
    void hardDeleteUser(Long id);
    void softDeleteUser(Long id);
}
