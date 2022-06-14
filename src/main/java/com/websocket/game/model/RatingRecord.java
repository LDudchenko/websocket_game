package com.websocket.game.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class RatingRecord {
    @Id
    @GeneratedValue
    private Long id;

    @OneToOne
    private PlayerEntity player;

    private int gameQuantity;

    private int victoryQuantity;

    private int lossQuantity;

    private int pointQuantity;
}
