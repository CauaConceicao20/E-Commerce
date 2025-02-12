package com.compass.e_commerce.model;

import com.compass.e_commerce.dto.user.AddressDataDto;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {

    private String street;

    private int number;

    private String neighborhood;

    private String city;

    private String state;

    private String postalCode;

    public Address(AddressDataDto addressDataDto) {
        this.street = addressDataDto.street();
        this.number = addressDataDto.number();
        this.neighborhood = addressDataDto.neighborhood();
        this.city = addressDataDto.city();
        this.state = addressDataDto.state();
        this.postalCode = addressDataDto.postalCode();
    }
}
