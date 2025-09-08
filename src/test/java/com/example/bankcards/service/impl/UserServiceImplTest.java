package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.custom.NotFoundException;
import com.example.bankcards.exception.custom.UserAlreadyExistsException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest userRequest;
    private User user;
    private UserResponse userResponse;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequest("testUser", "password123", Role.USER);

        user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setPasswordHash("encodedPassword");
        user.setRole(Role.USER);

        userResponse = new UserResponse(1L, "testUser", Role.USER);

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void createUser_WithNewUsername_ShouldCreateUserSuccessfully() {
        when(userRepository.existsByUsername("testUser")).thenReturn(false);
        when(userMapper.toUser(userRequest, passwordEncoder)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.createUser(userRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).existsByUsername("testUser");
        verify(userMapper).toUser(userRequest, passwordEncoder);
        verify(userRepository).save(user);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void createUser_WithExistingUsername_ShouldThrowException() {
        when(userRepository.existsByUsername("testUser")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(userRequest)
        );

        assertEquals("Пользователь testUser уже существует", exception.getMessage());

        verify(userRepository).existsByUsername("testUser");
        verify(userMapper, never()).toUser(any(), any());
        verify(userRepository, never()).save(any());
        verify(userMapper, never()).toUserResponse(any());
    }



    @Test
    void getUserById_WithExistingId_ShouldReturnUserResponse() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).findById(1L);
        verify(userMapper).toUserResponse(user);
    }

    @Test
    void getUserById_WithNonExistingId_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(999L)
        );

        assertEquals("Пользователь не найден", exception.getMessage());

        verify(userRepository).findById(999L);
        verify(userMapper, never()).toUserResponse(any());
    }

    @Test
    void getAllUsers_WithPageable_ShouldReturnPageOfUsers() {
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        UserResponse userResponse2 = new UserResponse(2L, "user2", Role.USER);

        List<User> users = List.of(user, user2);
        Page<User> userPage = new PageImpl<>(users, pageable, users.size());

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toUserResponse(user)).thenReturn(userResponse);
        when(userMapper.toUserResponse(user2)).thenReturn(userResponse2);

        Page<UserResponse> result = userService.getAllUsers(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(userResponse, result.getContent().get(0));
        assertEquals(userResponse2, result.getContent().get(1));
        assertEquals(0, result.getNumber());
        assertEquals(10, result.getSize());

        verify(userRepository).findAll(pageable);
        verify(userMapper).toUserResponse(user);
        verify(userMapper).toUserResponse(user2);
    }


    @Test
    void updateUser_WithSameUsername_ShouldUpdateSuccessfully() {
        UserRequest updateRequest = new UserRequest("testUser", "newPassword", Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUser(1L, updateRequest);

        verify(userRepository).findById(1L);
        verify(userRepository, never()).existsByUsername(any());
        verify(userMapper).updateUserFromRequest(updateRequest, user, passwordEncoder);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_WithNewUniqueUsername_ShouldUpdateSuccessfully() {
        UserRequest updateRequest = new UserRequest("newUsername", "newPassword", Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("newUsername")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        userService.updateUser(1L, updateRequest);

        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("newUsername");
        verify(userMapper).updateUserFromRequest(updateRequest, user, passwordEncoder);
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_WithExistingUsername_ShouldThrowException() {
        UserRequest updateRequest = new UserRequest("existingUser", "newPassword", Role.USER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.updateUser(1L, updateRequest)
        );

        assertEquals("Пользователь existingUser уже существует", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("existingUser");
        verify(userMapper, never()).updateUserFromRequest(any(), any(), any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_WithNonExistingId_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.updateUser(999L, userRequest)
        );

        assertEquals("Пользователь не найден", exception.getMessage());

        verify(userRepository).findById(999L);
        verify(userRepository, never()).existsByUsername(any());
        verify(userMapper, never()).updateUserFromRequest(any(), any(), any());
        verify(userRepository, never()).save(any());
    }


    @Test
    void getUserEntityById_WithExistingId_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserEntityById(1L);

        assertNotNull(result);
        assertEquals(user, result);

        verify(userRepository).findById(1L);
    }

    @Test
    void getUserEntityById_WithNonExistingId_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserEntityById(999L)
        );

        assertEquals("Пользователь не найден", exception.getMessage());

        verify(userRepository).findById(999L);
    }

    @Test
    void deleteUser_WithExistingId_ShouldDeleteSuccessfully() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).findById(1L);
        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_WithNonExistingId_ShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.deleteUser(999L)
        );

        assertEquals("Пользователь не найден", exception.getMessage());

        verify(userRepository).findById(999L);
        verify(userRepository, never()).delete(any());
    }


}