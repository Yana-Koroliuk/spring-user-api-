package com.koroliuk.userapi.controller;

import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;
import com.koroliuk.userapi.service.UserServiceImpl;
import com.koroliuk.userapi.validation.OnPatch;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDto) {
        User user = userService.create(userDto);
        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDto) {
        User updatedUser = userService.update(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> patchUpdateUser(@PathVariable Long id,
                                                @Validated(OnPatch.class) @RequestBody UserDTO userDto) {
        User updatedUser = userService.patchUpdate(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> findByBirthDateRange(
            @RequestParam(value = "start", required = false) LocalDate start,
            @RequestParam(value = "end", required = false) LocalDate end) {
        List<User> users = userService.findByBirthDateRange(start, end);
        return ResponseEntity.ok(users);
    }
}