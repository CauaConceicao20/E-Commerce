package com.compass.e_commerce.dto.buy;

public record BuyDetailsDto(

        String debtorName,
        String cpf,
        String value,
        String informationName1,
        String informationContent1,
        String informationName2,
        String informationContent2
) {
}
