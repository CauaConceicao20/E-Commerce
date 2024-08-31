package com.compass.e_commerce.model.pk;

import com.compass.e_commerce.model.Game;
import com.compass.e_commerce.model.Sale;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Embeddable
public class SaleGamePK implements Serializable {
    private static final long serialVersionUID = 1L;
    
    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

}
