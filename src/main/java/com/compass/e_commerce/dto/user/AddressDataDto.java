package com.compass.e_commerce.dto.user;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AddressDataDto(

         @NotBlank
         String street,

         @NotNull
         @Min(1)
         Integer number,

         @NotBlank
         String neighborhood,

         @NotBlank
         String city,

         @NotBlank
         String state,

         @NotNull
         @Size(min = 8, max = 8)
         String postalCode
) {
}
