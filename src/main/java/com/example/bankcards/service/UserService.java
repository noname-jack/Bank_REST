package com.example.bankcards.service;

import com.example.bankcards.entity.User;

public interface UserService {
    User getUserByUsername(String username);
}
