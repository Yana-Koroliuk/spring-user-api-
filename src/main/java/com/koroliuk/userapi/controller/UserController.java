package com.koroliuk.userapi.controller;

import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;
import com.koroliuk.userapi.service.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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
        return new ResponseEntity<>(userService.createUser(userDto), HttpStatus.CREATED);

    }
}
