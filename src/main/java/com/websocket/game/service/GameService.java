package com.websocket.game.service;


import com.websocket.game.exception.InvalidGameException;
import com.websocket.game.exception.InvalidParamException;
import com.websocket.game.exception.NotFoundException;
import com.websocket.game.model.Game;
import com.websocket.game.model.GamePlay;
import com.websocket.game.model.Player;
import com.websocket.game.model.TicToe;
import com.websocket.game.storage.GameStorage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.OptionalInt;
import java.util.UUID;

import static com.websocket.game.model.GameStatus.*;

@Service
@AllArgsConstructor
public class GameService {

    private final PointCounterService pointCounterService;
    private final PlayerService playerService;

    public Game createGame(Player player) {
        Game game = new Game();
        game.setBoard(new int[3][3]);
        game.setGameId(UUID.randomUUID().toString());
        game.setPlayer1(player);
        game.setStatus(NEW);
        GameStorage.getInstance().setGame(game);
        this.playerService.saveUser(player);
        return game;
    }

    public Game connectToGame(Player player2, String gameId) throws InvalidParamException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gameId)) {
            throw new InvalidParamException("Game with provided id doesn't exist");
        }
        Game game = GameStorage.getInstance().getGames().get(gameId);

        if (game.getPlayer2() != null) {
            throw new InvalidGameException("Game is not valid anymore");
        }

        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        this.playerService.saveUser(player2);
        return game;
    }

    public Game connectToRandomGame(Player player2) throws NotFoundException {
        Game game = GameStorage.getInstance().getGames().values().stream()
                .filter(it -> it.getStatus().equals(NEW))
                .findFirst().orElseThrow(() -> new NotFoundException("Game not found"));
        game.setPlayer2(player2);
        game.setStatus(IN_PROGRESS);
        GameStorage.getInstance().setGame(game);
        this.playerService.saveUser(player2);
        return game;
    }

    public Game gamePlay(GamePlay gamePlay) throws NotFoundException, InvalidGameException {
        if (!GameStorage.getInstance().getGames().containsKey(gamePlay.getGameId())) {
            throw new NotFoundException("Game not found");
        }

        Game game = GameStorage.getInstance().getGames().get(gamePlay.getGameId());
        if (game.getStatus().equals(FINISHED)) {
            throw new InvalidGameException("Game is already finished");
        }

        int[][] board = game.getBoard();
        board[gamePlay.getCoordinateX()][gamePlay.getCoordinateY()] = gamePlay.getType().getValue();

        Boolean xWinner = checkWinner(game.getBoard(), TicToe.X);
        Boolean oWinner = checkWinner(game.getBoard(), TicToe.O);

        if (xWinner) {
            game.setWinner(TicToe.X);
            this.pointCounterService.finalCount(game);
        } else if (oWinner) {
            game.setWinner(TicToe.O);
        } else if (checkDeadHeat(board)){
            game.setDeadHeat(true);
        }

        game.setLastStepType(gamePlay.getType());

        GameStorage.getInstance().setGame(game);
        return game;
    }

    private boolean checkDeadHeat(int[][] board) {
        int[] ints = this.convert2DArrayTo1D(board);
        OptionalInt first = Arrays.stream(ints).filter(x -> x == 0).findFirst();
        return !first.isPresent();
    }

    private Boolean checkWinner(int[][] board, TicToe ticToe) {
        int[] boardArray = this.convert2DArrayTo1D(board);

        int[][] winCombinations = {{0, 1, 2}, {3, 4, 5}, {6, 7, 8}, {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, {0, 4, 8}, {2, 4, 6}};
        for (int i = 0; i < winCombinations.length; i++) {
            int counter = 0;
            for (int j = 0; j < winCombinations[i].length; j++) {
                if (boardArray[winCombinations[i][j]] == ticToe.getValue()) {
                    counter++;
                    if (counter == 3) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private int[] convert2DArrayTo1D(int[][] board) {
        int[] boardArray = new int[9];
        int counterIndex = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                boardArray[counterIndex] = board[i][j];
                counterIndex++;
            }
        }
        return boardArray;
    }

}
