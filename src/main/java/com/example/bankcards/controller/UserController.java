package com.example.bankcards.controller;

import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.success(userService.getUserById(id)));
    }

    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<PagedModel<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable){
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponseDto.success(new PagedModel<>(users)));
    }
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponse>> createUser(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(userService.createUser(request), HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponse>> updateUser(@RequestBody @Valid UserRequest request,
                                                                   @PathVariable Long id,
                                                                   @RequestParam(required = false, defaultValue = "true") boolean withResult )  {
        userService.updateUser(id, request);
        if (withResult) {
            return ResponseEntity.ok(ApiResponseDto.success(userService.getUserById(id)));
        } else {
            return ResponseEntity.noContent().build();
        }
    }


}
