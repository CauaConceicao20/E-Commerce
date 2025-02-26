package com.compass.e_commerce.dto.user;


public record AddressDataDto(


         String street,

         Integer number,

         String neighborhood,

         String city,

         String state,

         String postalCode
) {
}
