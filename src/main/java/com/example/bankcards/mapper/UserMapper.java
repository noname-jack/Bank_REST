package com.example.bankcards.mapper;

import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.password.PasswordEncoder;

@Mapper(componentModel ="spring")
public interface UserMapper {


    UserResponse toUserResponse(User user);

    @Mapping(target = "passwordHash", expression = "java(passwordEncoder.encode(userRequest.password()))")
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankCards", ignore = true)
    User toUser(UserRequest userRequest, PasswordEncoder passwordEncoder);

    @Mapping(target = "passwordHash", expression = "java(passwordEncoder.encode(userRequest.password()))")
    @Mapping(target = "authorities", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "bankCards", ignore = true)
    void updateUserFromRequest(UserRequest userRequest, @MappingTarget User user, PasswordEncoder passwordEncoder);

}
