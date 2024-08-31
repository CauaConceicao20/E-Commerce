package com.compass.e_commerce.dto.sale;

import com.compass.e_commerce.dto.game.GameDto;
import com.compass.e_commerce.model.SaleGame;

import java.time.LocalDateTime;
import java.util.List;

public record SaleReportListDto(
        Long saleId,
        LocalDateTime saleDate,
        String userName,
        List<GameDto> products

) {
/*
    public SaleReportListDto(SaleGame saleGame) {
        this(saleGame.getSale().getId(),
                saleGame.getSale().getDateTime(),
                saleGame.getSale().getUser().getLogin(),
                saleGame.getSale().getUser().getEmail(),
                saleGame.getGame().getName(),
                saleGame.getGame().getDescription(),
                saleGame.getQuantity(),
                saleGame.getPrice()

        );
    }

 */
}

