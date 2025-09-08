package com.example.bankcards.controller;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.enums.Role;
import com.example.bankcards.exception.custom.NotFoundException;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserResponse userResponse;
    private UserRequest userRequest;
    @BeforeEach
    void setUp() {
         userResponse = new UserResponse(1L, "admin", Role.ADMIN);
         userRequest = new UserRequest("userName", "password", Role.USER);
    }

    @Test
    void getUserById_Success() throws Exception {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(userResponse);

        mockMvc.perform(get("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.status").value("OK"))
                .andExpect(jsonPath("$.data.id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.role").value(userResponse.role().name()));

        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void getUserById_NotFound() throws Exception {
        Long userId = 1L;
        when(userService.getUserById(userId))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        mockMvc.perform(get("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));

        verify(userService, times(1)).getUserById(userId);
    }


    @Test
    void getAllUsers_Success() throws Exception {
        Page<UserResponse> page = new PageImpl<>(List.of(userResponse));
        when(userService.getAllUsers(any())).thenReturn(page);

        mockMvc.perform(get("/api/users")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.data.content[0].id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.content[0].username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.content[0].role").value(userResponse.role().name()))
                .andExpect(jsonPath("$.data.page.number").value(0))
                .andExpect(jsonPath("$.data.page.totalElements").value(1))
                .andExpect(jsonPath("$.data.page.totalPages").value(1));


        verify(userService, times(1)).getAllUsers(any(Pageable.class));
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(userRequest)).thenReturn(userResponse);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.data.id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.role").value(userResponse.role().name()));

        verify(userService, times(1)).createUser(userRequest);
    }


    @Test
    void deleteUser_Success() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }


    @Test
    void deleteUser_NotFound() throws Exception {
        Long userId = 1L;
        doThrow(new NotFoundException("Пользователь не найден"))
                .when(userService).deleteUser(userId);

        mockMvc.perform(delete("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("error"))
                .andExpect(jsonPath("$.data").doesNotExist())
                .andExpect(jsonPath("$.error").value("Пользователь не найден"))
                .andExpect(jsonPath("$.status").value("NOT_FOUND"));

        verify(userService, times(1)).deleteUser(userId);
    }


    @Test
    void updateUser_WithResult_Success() throws Exception {
        Long userId = 1L;
        when(userService.getUserById(userId)).thenReturn(userResponse);
        doNothing().when(userService).updateUser(userId, userRequest);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .param("withResult", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(userResponse.id()))
                .andExpect(jsonPath("$.data.username").value(userResponse.username()))
                .andExpect(jsonPath("$.data.role").value(userResponse.role().name()));

        verify(userService, times(1)).updateUser(userId, userRequest);
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    void updateUser_WithoutResult_Success() throws Exception {
        Long userId = 1L;
        doNothing().when(userService).updateUser(userId, userRequest);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .param("withResult", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(userService, times(1)).updateUser(userId, userRequest);
        verify(userService, never()).getUserById(anyLong());
    }

}