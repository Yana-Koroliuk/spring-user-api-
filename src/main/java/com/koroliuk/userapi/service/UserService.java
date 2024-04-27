package com.koroliuk.userapi.service;

import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User createUser(UserDTO userDto);
    User updateUser(Long id, UserDTO userDto);
    User patchUpdateUser(Long id, UserDTO userDto);
    void deleteUser(Long id);
    List<User> findByBirthDateRange(LocalDate start, LocalDate end);
}