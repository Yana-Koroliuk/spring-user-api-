package com.koroliuk.userapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koroliuk.userapi.dto.UserDTO;
import com.koroliuk.userapi.model.User;
import com.koroliuk.userapi.service.UserService;
import com.koroliuk.userapi.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User user;
    private UserDTO userDto;

    @BeforeEach
    public void setup() {
        user = User.builder()
                .id(1L)
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .phoneNumber("+380670891268")
                .build();

        userDto = UserDTO.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .phoneNumber("+380670891268")
                .build();
    }

    @Test
    public void createUser_WhenPostUser_ShouldReturnUser() throws Exception {
        UserDTO userDto = createUserDTO();
        User newUser = createUser();
        given(userService.create(any(UserDTO.class))).willReturn(newUser);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email", is(newUser.getEmail())))
                .andExpect(jsonPath("$.firstName", is(newUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(newUser.getLastName())))
                .andExpect(jsonPath("$.birthDate", is("1990-01-01")))
                .andExpect(jsonPath("$.address", is(newUser.getAddress())))
                .andExpect(jsonPath("$.phoneNumber", is(newUser.getPhoneNumber())));
    }

    @Test
    public void updateUser_WhenPutUser_ShouldReturnUpdatedUser() throws Exception {
        UserDTO userDto = createUserDTO();
        User updatedUser = createUser();
        given(userService.update(any(Long.class), any(UserDTO.class))).willReturn(updatedUser);
        mvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail())))
                .andExpect(jsonPath("$.firstName", is(updatedUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(updatedUser.getLastName())))
                .andExpect(jsonPath("$.birthDate", is("1990-01-01")))
                .andExpect(jsonPath("$.address", is(updatedUser.getAddress())))
                .andExpect(jsonPath("$.phoneNumber", is(updatedUser.getPhoneNumber())));
    }

    @Test
    public void patchUpdateUser_WhenPatchUserWithPartialData_ShouldUpdateSpecifiedFields() throws Exception {
        UserDTO partialUserDto = UserDTO.builder()
                .email("partial.update@example.com")
                .address("789 Partial Ave")
                .build();
        User originalUser = createUser();
        User partiallyUpdatedUser = User.builder()
                .id(1L)
                .email(partialUserDto.getEmail())
                .firstName(originalUser.getFirstName())
                .lastName(originalUser.getLastName())
                .birthDate(originalUser.getBirthDate())
                .address(partialUserDto.getAddress())
                .phoneNumber(originalUser.getPhoneNumber())
                .build();

        given(userService.patchUpdate(eq(1L), any(UserDTO.class))).willReturn(partiallyUpdatedUser);

        mvc.perform(patch("/users/1")
                //todo: other methods
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is(partiallyUpdatedUser.getEmail())))
                .andExpect(jsonPath("$.firstName", is(originalUser.getFirstName())))
                .andExpect(jsonPath("$.lastName", is(originalUser.getLastName())))
                .andExpect(jsonPath("$.birthDate", is(originalUser.getBirthDate().toString())))
                .andExpect(jsonPath("$.address", is(partiallyUpdatedUser.getAddress())))
                .andExpect(jsonPath("$.phoneNumber", is(originalUser.getPhoneNumber())));
    }

    @Test
    public void deleteUser_WhenDeleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).delete(1L);

        mvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @ParameterizedTest
    @CsvSource({
            "1990-01-01, 1990-12-31",
            "1990-01-01, ",
            ", 1990-12-31"
    })
    public void findByBirthDateRange_WhenGetUsers_ShouldReturnUsers(String start, String end) throws Exception {
        User user = createUser();
        List<User> users = Collections.singletonList(user);

        LocalDate startDate = start != null ? LocalDate.parse(start) : null;
        LocalDate endDate = end != null ? LocalDate.parse(end) : null;

        given(userService.findByBirthDateRange(eq(startDate), eq(endDate))).willReturn(users);

        mvc.perform(get("/users/search")
                        .param("start", start != null ? start : "")
                        .param("end", end != null ? end : ""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())))
                .andExpect(jsonPath("$[0].firstName", is(user.getFirstName())))
                .andExpect(jsonPath("$[0].lastName", is(user.getLastName())))
                .andExpect(jsonPath("$[0].birthDate", is("1990-01-01")))
                .andExpect(jsonPath("$[0].address", is(user.getAddress())))
                .andExpect(jsonPath("$[0].phoneNumber", is(user.getPhoneNumber())));
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .phoneNumber("+380670891268")
                .build();
    }

    private UserDTO createUserDTO() {
        return UserDTO.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .phoneNumber("+380670891268")
                .build();
    }
}
