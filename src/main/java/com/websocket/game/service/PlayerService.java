package com.websocket.game.service;

import com.websocket.game.model.Player;
import com.websocket.game.model.PlayerEntity;
import com.websocket.game.storage.rpositories.PlayerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PlayerService {
    private final PlayerRepository playerRepository;

    public void saveUser(Player player) {
        PlayerEntity playerEntity = new PlayerEntity();
        playerEntity.setLogin(player.getLogin());
        Optional<PlayerEntity> foundUser = this.playerRepository.findByLogin(player.getLogin());
        if (foundUser.isEmpty()) {
            this.playerRepository.save(playerEntity);
        }
    }
}
