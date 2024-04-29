package com.koroliuk.userapi.service;

import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;
import com.koroliuk.userapi.repository.UserRepository;
import com.koroliuk.userapi.validation.UpdateUtils;
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
    public User create(UserDTO userDto) {
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

    @Override
    public User update(Long id, UserDTO userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        if (isAgeValid(userDto.getBirthDate())) {
            user.setBirthDate(userDto.getBirthDate());
        } else {
            throw new IllegalArgumentException("User must be at least " + minimumAge + " years old.");
        }
        user.setEmail(userDto.getEmail());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setAddress(userDto.getAddress());
        user.setPhoneNumber(userDto.getPhoneNumber());

        return userRepository.save(user);
    }

    public User patchUpdate(Long id, UserDTO userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));

        UpdateUtils.updateIfChanged(existingUser::setEmail, userDto.getEmail(), existingUser::getEmail);
        UpdateUtils.updateIfChanged(existingUser::setFirstName, userDto.getFirstName(), existingUser::getFirstName);
        UpdateUtils.updateIfChanged(existingUser::setLastName, userDto.getLastName(), existingUser::getLastName);
        UpdateUtils.updateIfChanged(existingUser::setAddress, userDto.getAddress(), existingUser::getAddress);
        UpdateUtils.updateIfChanged(existingUser::setPhoneNumber, userDto.getPhoneNumber(), existingUser::getPhoneNumber);

        if (userDto.getBirthDate() != null && isBirthDateChangedAndValid(existingUser.getBirthDate(), userDto.getBirthDate())) {
            existingUser.setBirthDate(userDto.getBirthDate());
        }
        return userRepository.save(existingUser);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id " + id + " not found");
        }
        userRepository.deleteById(id);
    }

    @Override
    public List<User> findByBirthDateRange(LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            if (start.isAfter(end)) {
                throw new IllegalArgumentException("The 'from' date must be before the 'to' date.");
            }
            return userRepository.findByBirthDateBetween(start, end);
        } else if (start != null) {
            return userRepository.findByBirthDateAfter(start);
        } else if (end != null) {
            return userRepository.findByBirthDateBefore(end);
        } else {
            return userRepository.findAll();
        }
    }

    private boolean isAgeValid(LocalDate birthDate) {
        return Period.between(birthDate, LocalDate.now()).getYears() >= minimumAge;
    }

    private boolean isBirthDateChangedAndValid(LocalDate currentBirthDate, LocalDate newBirthDate) {
        if (newBirthDate.equals(currentBirthDate)) {
            return false;
        }
        if (!isAgeValid(newBirthDate)) {
            throw new IllegalArgumentException("User must be at least " + minimumAge + " years old.");
        } else {
            return true;
        }
    }
}