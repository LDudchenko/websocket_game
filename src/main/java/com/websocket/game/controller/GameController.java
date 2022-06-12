package com.websocket.game.controller;

import com.websocket.game.controller.dto.ConnectRequest;
import com.websocket.game.controller.dto.GameId;
import com.websocket.game.exception.InvalidGameException;
import com.websocket.game.exception.InvalidParamException;
import com.websocket.game.exception.NotFoundException;
import com.websocket.game.model.Game;
import com.websocket.game.model.GamePlay;
import com.websocket.game.model.Player;
import com.websocket.game.service.GameService;
import com.websocket.game.storage.GameStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/game")
public class GameController {

    private final GameService gameService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    @PostMapping("/start")
    public ResponseEntity<Game> start(@RequestBody Player player) {
        log.info("start game request: {}", player);
        return ResponseEntity.ok(gameService.createGame(player));
    }

    @PostMapping("/connect")
    public ResponseEntity<Game> connect(@RequestBody ConnectRequest request) throws InvalidParamException, InvalidGameException {
        log.info("connect request: {}", request);
        return ResponseEntity.ok(gameService.connectToGame(request.getPlayer(), request.getGameId()));
    }

    @PostMapping("/connect/random")
    public ResponseEntity<Game> connectRandom(@RequestBody Player player) throws NotFoundException {
        log.info("connect random {}", player);
        return ResponseEntity.ok(gameService.connectToRandomGame(player));
    }

    @PostMapping("/gameplay")
    public ResponseEntity<Game> gamePlay(@RequestBody GamePlay request) throws NotFoundException, InvalidGameException {
        log.info("gameplay: {}", request);
        Game game = gameService.gamePlay(request);
        simpMessagingTemplate.convertAndSend("/topic/game-progress/" + game.getGameId(), game);
        return ResponseEntity.ok(game);
    }

    @PostMapping
    public ResponseEntity<Game> getGame(@RequestBody GameId uuidId){
        log.info("fetch game: {}", uuidId.getId());
        return ResponseEntity.ok(GameStorage.getInstance().getGames().get(uuidId.getId()));
    }
}
