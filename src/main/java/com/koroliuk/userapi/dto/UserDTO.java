package com.koroliuk.userapi.dto;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
public class UserDTO {

    @NotBlank(message = "Email should not be blank")
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email format")
    private String email;
    @NotBlank(message = "First name should not be blank")
    private String firstName;

    @NotBlank(message = "Last name should not be blank")
    private String lastName;

    @NotNull(message = "Birth date should not be null")
    @Past(message = "The birth date must be in the past")
    private LocalDate birthDate;

    private String address;

    @Pattern(regexp = "^\\+?[0-9. ()-]{10,25}$", message = "Invalid phone number format")
    private String phoneNumber;
}