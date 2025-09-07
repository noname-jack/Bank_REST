package com.example.bankcards.controller;

import com.example.bankcards.dto.ApiResponseDto;
import com.example.bankcards.dto.request.UserRequest;
import com.example.bankcards.dto.response.UserResponse;
import com.example.bankcards.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @Operation(
            summary = "Получение информации о пользователе по ID",
            description = "Возвращает данные пользователя по его ID. Доступно для администраторов или самого пользователя."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно найден"

            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("#id == authentication.principal.id or hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponse>> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponseDto.success(userService.getUserById(id)));
    }



    @Operation(
            summary = "Получение списка всех пользователей (для администратора)",
            description = "Возвращает постраничный список всех пользователей. Доступно только администраторам."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Список пользователей успешно получен"
    )
    @GetMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<PagedModel<UserResponse>>> getAllUsers(
            @PageableDefault(size = 20) Pageable pageable){
        Page<UserResponse> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(ApiResponseDto.success(new PagedModel<>(users)));
    }


    @Operation(
            summary = "Создание нового пользователя (для администратора)",
            description = "Создаёт нового пользователя на основе переданных данных. Доступно только администраторам."
    )
    @ApiResponse(
            responseCode = "201",
            description = "Пользователь успешно создан"
    )
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDto<UserResponse>> createUser(@RequestBody @Valid UserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(userService.createUser(request), HttpStatus.CREATED));
    }


    @Operation(
            summary = "Удаление пользователя по ID (для администратора)",
            description = "Удаляет пользователя по его ID. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Пользователь успешно удалён",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }



    @Operation(
            summary = "Обновление данных пользователя по ID (для администратора)",
            description = "Обновляет данные пользователя по его ID. Доступно только администраторам."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Пользователь успешно обновлён (с возвратом данных)",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class, subTypes = {UserResponse.class})
                    )
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "Пользователь успешно обновлён (без возврата данных)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Пользователь не найден",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponseDto.class)
                    )
            )
    })
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
