package com.websocket.game.model;

import lombok.Data;

@Data
public class Game {

    private String gameId;
    private Player player1;
    private Player player2;
    private GameStatus status;
    private int[][] board;
    private TicToe winner;
    private TicToe lastStepType;
    private Boolean deadHeat;

}
