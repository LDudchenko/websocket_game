package com.websocket.game.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class PlayerEntity {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique=true)
    private String login;
}
