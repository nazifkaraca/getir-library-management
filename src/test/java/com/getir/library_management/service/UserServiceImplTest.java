package com.getir.library_management.service;

import com.getir.library_management.dto.user.UpdateUserRequestDto;
import com.getir.library_management.dto.user.UserResponseDto;
import com.getir.library_management.entity.User;
import com.getir.library_management.exception.custom.UserNotFoundException;
import com.getir.library_management.repository.UserRepository;
import com.getir.library_management.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void updateUser_ShouldUpdateSuccessfully() {
        // Arrange
        Long userId = 1L;
        UpdateUserRequestDto request = new UpdateUserRequestDto();
        request.setId(userId);
        request.setFullName("Updated Name");
        request.setEmail("updated@getir.com");

        User existingUser = User.builder()
                .id(userId)
                .fullName("Old Name")
                .email("old@getir.com")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .fullName("Updated Name")
                .email("updated@getir.com")
                .build();

        UserResponseDto expectedDto = new UserResponseDto();
        expectedDto.setId(userId);
        expectedDto.setEmail("updated@getir.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserResponseDto.class)).thenReturn(expectedDto);

        // Act
        UserResponseDto result = userService.updateUser(userId, request);

        // Assert
        assertEquals("updated@getir.com", result.getEmail());
        verify(userRepository).save(existingUser);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        Long userId = 1L;
        User user = User.builder().id(userId).email("user@getir.com").build();
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setId(userId);
        responseDto.setEmail("user@getir.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserResponseDto.class)).thenReturn(responseDto);

        UserResponseDto result = userService.getUserById(userId);

        assertEquals("user@getir.com", result.getEmail());
    }

    @Test
    void getUserById_ShouldThrowException_WhenNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(100L));
    }

    @Test
    void getAllUsers_ShouldReturnList() {
        User user1 = User.builder().id(1L).email("a@getir.com").build();
        User user2 = User.builder().id(2L).email("b@getir.com").build();

        UserResponseDto dto1 = new UserResponseDto();
        dto1.setId(1L);
        dto1.setEmail("a@getir.com");

        UserResponseDto dto2 = new UserResponseDto();
        dto2.setId(2L);
        dto2.setEmail("b@getir.com");

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));
        when(modelMapper.map(user1, UserResponseDto.class)).thenReturn(dto1);
        when(modelMapper.map(user2, UserResponseDto.class)).thenReturn(dto2);

        List<UserResponseDto> result = userService.getAllUsers();

        assertEquals(2, result.size());
        assertEquals("b@getir.com", result.get(1).getEmail());
    }

    @Test
    void softDeleteUser_ShouldMarkAsDeleted() {
        Long userId = 1L;
        User user = User.builder().id(userId).markedAsDeleted(false).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.softDeleteUser(userId);

        assertTrue(user.isMarkedAsDeleted());
        verify(userRepository).save(user);
    }

    @Test
    void hardDeleteUser_ShouldRemoveFromDatabase() {
        Long userId = 1L;
        User user = User.builder().id(userId).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.hardDeleteUser(userId);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrow_WhenNotFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.hardDeleteUser(5L));
        assertThrows(UserNotFoundException.class, () -> userService.softDeleteUser(5L));
    }
}
