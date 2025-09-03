package com.example.bankcards.service.impl;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import com.example.bankcards.exception.custom.NotFoundException;
import com.example.bankcards.exception.custom.UserAlreadyExistsException;
import com.example.bankcards.mapper.UserMapper;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse createUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.username())) {
            throw new UserAlreadyExistsException("Пользователь " + userRequest.username()+ " уже существует");
        }
        User user = userMapper.toUser(userRequest, passwordEncoder);

        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    @Override
    public UserResponse getUserById(Long id) {
        return userMapper.toUserResponse(getUserEntityById(id));
    }

    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> userPage = userRepository.findAll(pageable);
        return userPage.map(userMapper::toUserResponse);
    }

    @Override
    public void updateUser(Long id, UserRequest userRequest) {
        User user = getUserEntityById(id);
        if (!user.getUsername().equals(userRequest.username()) && userRepository.existsByUsername(userRequest.username())){
            throw new UserAlreadyExistsException("Пользователь " + userRequest.username()+ " уже существует");
        }
        userMapper.updateUserFromRequest(userRequest, user, passwordEncoder);
        userRepository.save(user);
    }

    @Override
    public User getUserEntityById(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
    }
    @Override
    public void deleteUser(Long id) {
        User user = getUserEntityById(id);
        userRepository.delete(user);
    }
}
