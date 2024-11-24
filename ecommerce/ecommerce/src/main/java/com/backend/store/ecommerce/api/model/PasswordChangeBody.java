package com.backend.store.ecommerce.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordChangeBody {

    @NotNull
    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$") //Minimum eight characters, at least one letter and one number
    private String newPassword1;
    @NotNull
    @NotBlank
    @Size(min = 3, max = 30)
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}$") //Minimum eight characters, at least one letter and one number
    private String newPassword2;
    public String getNewPassword1() {
        return newPassword1;
    }
    public String getNewPassword2() {
        return newPassword2;
    }

}
