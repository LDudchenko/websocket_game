package com.websocket.game.controller.dto;

import com.websocket.game.model.Player;
import lombok.Data;

@Data
public class ConnectRequest {
    private Player player;
    private String gameId;
}
