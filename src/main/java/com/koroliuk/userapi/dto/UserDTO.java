package com.koroliuk.userapi.dto;

import com.koroliuk.userapi.validation.ValidationGroups.OnPatch;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Data
@Builder
public class UserDTO {

    @NotBlank(message = "Email should not be blank")
    @Email(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            message = "Invalid email format", groups = OnPatch.class)
    private String email;

    @NotBlank(message = "First name should not be blank")
    private String firstName;

    @NotBlank(message = "Last name should not be blank")
    private String lastName;

    @NotNull(message = "Birth date should not be null")
    @Past(message = "The birth date must be in the past", groups = OnPatch.class)
    private LocalDate birthDate;

    private String address;

    @Pattern(regexp = "^\\+?[0-9. ()-]{10,25}$",
            message = "Invalid phone number format",
            groups = OnPatch.class)
    private String phoneNumber;
}