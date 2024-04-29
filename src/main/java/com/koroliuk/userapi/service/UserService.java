package com.koroliuk.userapi.service;

import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService {
    User create(UserDTO userDto);
    User update(Long id, UserDTO userDto);
    User patchUpdate(Long id, UserDTO userDto);
    void delete(Long id);
    List<User> findByBirthDateRange(LocalDate start, LocalDate end);
}