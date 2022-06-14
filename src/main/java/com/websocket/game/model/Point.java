package com.websocket.game.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Point {
    FIVE(5), TEN(10);
    private int value;
}
