package com.koroliuk.userapi.service;

import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;
import com.koroliuk.userapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Value("${user.min.age}")
    private int minimumAge;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User createUser(UserDTO userDto) {
        if (!isAgeValid(userDto.getBirthDate())) {
            throw new IllegalArgumentException("User must be at least " + minimumAge + " years old.");
        }

        User user = User.builder()
                .email(userDto.getEmail())
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .birthDate(userDto.getBirthDate())
                .address(userDto.getAddress())
                .phoneNumber(userDto.getPhoneNumber())
                .build();
        return userRepository.save(user);
    }

    private boolean isAgeValid(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= minimumAge;
    }

}
