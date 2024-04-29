package com.koroliuk.userapi.service;

import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;
import com.koroliuk.userapi.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDTO;
    private User user;
    private final int minimumAge = 18;

    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .birthDate(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .phoneNumber("+380670891268")
                .build();

        user = User.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .birthDate(LocalDate.of(2000, 1, 1))
                .address("123 Main St")
                .phoneNumber("+380670891268")
                .build();

        ReflectionTestUtils.setField(userService, "minimumAge", minimumAge);
    }


    @Test
    void create_ValidUser_ShouldReturnUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        User createdUser = userService.create(userDTO);

        assertNotNull(createdUser);
        assertEquals(user.getId(), createdUser.getId());
    }

    @Test
    void create_InvalidAge_ShouldThrowIllegalArgumentException() {
        userDTO.setBirthDate(LocalDate.now().minusYears(minimumAge - 1));
        assertThrows(IllegalArgumentException.class, () -> userService.create(userDTO));
    }

    @Test
    void update_ExistingUser_ShouldUpdateUser() {
        user.setEmail("oldtest@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = userService.update(1L, userDTO);

        assertNotNull(updatedUser);
        assertEquals(userDTO.getEmail(), updatedUser.getEmail());
    }

    @Test
    void update_UserWithInvalidAge_ShouldThrowIllegalArgumentException() {
        userDTO.setBirthDate(LocalDate.now().minusYears(minimumAge - 1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> userService.update(1L, userDTO));
    }

    @Test
    void update_NonExistentUser_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.update(1L, userDTO));
    }

    @Test
    void patchUpdate_UserExists_ShouldPatchUser() {
        UserDTO partialUserDto = UserDTO.builder()
                .email("partial.update@example.com")
                .build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User patchedUser = userService.patchUpdate(1L, partialUserDto);

        assertNotNull(patchedUser);
        assertEquals(partialUserDto.getEmail(), patchedUser.getEmail());
        assertEquals(user.getFirstName(), patchedUser.getFirstName());
    }

    @Test
    void patchUpdate_WithBlankEmail_ShouldThrowIllegalArgumentException() {
        UserDTO partialUserDto = UserDTO.builder()
                .email("  ")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> userService.patchUpdate(1L, partialUserDto),
                "Expected patchUpdate to throw, but it did not");

        assertTrue(thrown.getMessage().contains("The new value must not be blank"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void patchUpdate_ValidBirthDateChange_ShouldUpdateBirthDate() {
        LocalDate newValidBirthDate = LocalDate.of(1990, 1, 1);
        userDTO.setBirthDate(newValidBirthDate);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.patchUpdate(1L, userDTO);
        assertEquals(newValidBirthDate, user.getBirthDate());
    }

    @Test
    void patchUpdate_SameBirthDate_ShouldNotUpdateBirthDate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User resultUser = userService.patchUpdate(1L, userDTO);

        assertEquals(user.getBirthDate(), resultUser.getBirthDate(),
                "Birth date should not change if the same date is submitted.");
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void patchUpdate_UserDoesNotExist_ShouldThrowEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.patchUpdate(1L, userDTO));
    }
    @Test
    void patchUpdate_InvalidBirthDateChange_ShouldThrowIllegalArgumentException() {
        userDTO.setBirthDate(LocalDate.now().minusYears(minimumAge - 1));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> userService.patchUpdate(1L, userDTO));
        assertEquals("User must be at least " + minimumAge + " years old.", exception.getMessage());
    }

    @Test
    void delete_UserExists_ShouldNotThrowException() {
        when(userRepository.existsById(1L)).thenReturn(true);
        assertDoesNotThrow(() -> userService.delete(1L));
        verify(userRepository).deleteById(1L);
    }

    @Test
    void delete_UserDoesNotExist_ShouldThrowEntityNotFoundException() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> userService.delete(1L));
    }

    @Test
    void findByBirthDateRange_ValidRange_ShouldReturnUsers() {
        LocalDate start = LocalDate.of(1999, 1, 1);
        LocalDate end = LocalDate.of(2001, 12, 31);
        when(userRepository.findByBirthDateBetween(start, end)).thenReturn(Collections.singletonList(user));

        List<User> users = userService.findByBirthDateRange(start, end);

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void findByBirthDateRange_OnlyStartDateProvided_ShouldReturnUsersAfterStartDate() {
        LocalDate start = LocalDate.of(2000, 1, 1);
        when(userRepository.findByBirthDateAfter(start)).thenReturn(Collections.singletonList(user));

        List<User> users = userService.findByBirthDateRange(start, null);

        assertFalse(users.isEmpty());
        verify(userRepository).findByBirthDateAfter(start);
    }

    @Test
    void findByBirthDateRange_OnlyEndDateProvided_ShouldReturnUsersBeforeEndDate() {
        LocalDate end = LocalDate.of(2002, 12, 31);
        when(userRepository.findByBirthDateBefore(end)).thenReturn(Collections.singletonList(user));

        List<User> users = userService.findByBirthDateRange(null, end);

        assertFalse(users.isEmpty());
        verify(userRepository).findByBirthDateBefore(end);
    }

    @Test
    void findByBirthDateRange_NoDatesProvided_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        List<User> users = userService.findByBirthDateRange(null, null);

        assertFalse(users.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    void findByBirthDateRange_InvalidRange_ShouldThrowIllegalArgumentException() {
        LocalDate start = LocalDate.of(2002, 1, 1);
        LocalDate end = LocalDate.of(2001, 12, 31);
        assertThrows(IllegalArgumentException.class, () -> userService.findByBirthDateRange(start, end));
    }
}