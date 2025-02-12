package com.compass.e_commerce.dto.user;

import com.compass.e_commerce.annotations.UniqueCpf;
import com.compass.e_commerce.annotations.UniqueEmail;
import com.compass.e_commerce.annotations.UniqueLoginUser;
import com.compass.e_commerce.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


public record UserRegistrationDto(

        @UniqueLoginUser
        String username,

        @Size(min = 8, max = 14)
        String password,

        @Email
        @UniqueEmail
        @NotBlank
        String email,

        @UniqueCpf
        @Size(min = 11, max= 11)
        String cpf,

        @Size(min = 15, max = 15)
        @Pattern(regexp = "^\\(\\d{2}\\) \\d{5}-\\d{4}$", message = "Número de telefone inválido")
        String phone,

        @NotNull
        @Valid
        AddressDataDto address

) {
}
