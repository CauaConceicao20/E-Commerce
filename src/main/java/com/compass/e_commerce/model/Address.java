package com.compass.e_commerce.model;

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
}
