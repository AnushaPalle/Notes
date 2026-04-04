package com.shopflow.monolith.users.web;

import com.shopflow.monolith.users.application.UserService;
import com.shopflow.monolith.users.domain.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
class UserController {

    record CreateUserRequest(@NotBlank String name, @NotBlank @Email String email) {
    }

    record UserResponse(Long id, String name, String email) {
        static UserResponse from(User u) {
            return new UserResponse(u.getId(), u.getName(), u.getEmail());
        }
    }

    private final UserService service;

    UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return UserResponse.from(service.create(request.name(), request.email()));
    }

    @GetMapping("/{id}")
    UserResponse get(@PathVariable Long id) {
        return UserResponse.from(service.get(id));
    }

    @GetMapping
    List<UserResponse> list() {
        return service.list().stream().map(UserResponse::from).toList();
    }
}
