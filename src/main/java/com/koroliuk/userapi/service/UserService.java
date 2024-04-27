package com.koroliuk.userapi.service;

import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;

public interface UserService {
    User createUser(UserDTO userDto);

}
