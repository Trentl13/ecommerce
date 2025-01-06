package com.backend.store.ecommerce.api.model;

import jakarta.validation.constraints.*;
import lombok.Data;

//Validation dependency ot spring initializr
@Data
public class RegistrationBody {
    @NotNull
    @NotBlank
    @Size(min = 3, max = 255)
    private String username;
    @NotNull
    @NotBlank
    @Email
    private String email;
    @NotNull
    @NotBlank
    @Size(min = 6, max = 32)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$")
    //Minimum eight characters, at least one letter and one number
    private String password;
    @NotNull
    @NotBlank
    private String firstName;
    @NotNull
    @NotBlank
    private String lastName;

}
