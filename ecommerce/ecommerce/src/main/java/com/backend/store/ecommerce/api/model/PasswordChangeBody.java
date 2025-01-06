package com.backend.store.ecommerce.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Data
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

}
