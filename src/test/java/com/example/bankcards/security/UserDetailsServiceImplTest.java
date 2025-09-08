
package com.example.bankcards.security;

import com.example.bankcards.entity.User;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPasswordHash("passwordHash");
        testUser.setRole(Role.USER);
    }

    @Test
    void loadUserByUsername_WithExistingUser_ShouldReturnUserDetails() {
        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testUser");

        assertNotNull(userDetails);
        assertEquals("testUser", userDetails.getUsername());
        assertEquals("passwordHash", userDetails.getPassword());

        verify(userRepository).findByUsername("testUser");
    }

    @Test
    void loadUserByUsername_WithNonExistingUser_ShouldThrowException() {
        when(userRepository.findByUsername("nonExistUser")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("nonExistUser")
        );

        assertEquals("Пользователь не найден", exception.getMessage());
        verify(userRepository).findByUsername("nonExistUser");
    }


}