package com.websocket.game.service;

import com.websocket.game.exception.NotFoundException;
import com.websocket.game.model.*;
import com.websocket.game.storage.rpositories.PlayerRepository;
import com.websocket.game.storage.rpositories.RatingRecordRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class PointCounterService {

    private final RatingRecordRepository ratingRecordRepository;
    private final PlayerRepository playerRepository;

    @Transactional
    public void finalCount(Game game) throws NotFoundException {
        PlayerEntity player1 = this.playerRepository.findByLogin(game.getPlayer1().getLogin())
                .orElseThrow(() -> new NotFoundException("Player with login: " + game.getPlayer1().getLogin() + " is not found"));
        PlayerEntity player2 = this.playerRepository.findByLogin(game.getPlayer2().getLogin())
                .orElseThrow(() -> new NotFoundException("Player with login: " + game.getPlayer1().getLogin() + " is not found"));

        RatingRecord ratingRecord1 = this.getOrCreateRatingRecord(player1);
        RatingRecord ratingRecord2 = this.getOrCreateRatingRecord(player2);

        this.setGameQuantity(ratingRecord1);
        this.setGameQuantity(ratingRecord2);

        this.setVictoryQuantity(game, ratingRecord1, ratingRecord2);

        this.ratingRecordRepository.save(ratingRecord1);
        this.ratingRecordRepository.save(ratingRecord2);
    }

    private void setVictoryQuantity(Game game, RatingRecord ratingRecord1, RatingRecord ratingRecord2) {
        if(game.getDeadHeat() != null && game.getDeadHeat() == true){
            this.increasePointQuantity(ratingRecord1, Point.FIVE.getValue());
            this.increasePointQuantity(ratingRecord2, Point.FIVE.getValue());
        } else if (TicToe.X.equals(game.getWinner())) {
            this.increaseVictoryQuantity(ratingRecord1);
            this.increasePointQuantity(ratingRecord1, Point.TEN.getValue());
            this.increaseLossQuantity(ratingRecord2);
        } else {
            this.increaseVictoryQuantity(ratingRecord2);
            this.increasePointQuantity(ratingRecord2, Point.TEN.getValue());
            this.increaseLossQuantity(ratingRecord1);
        }
    }

    private void increaseVictoryQuantity(RatingRecord ratingRecord) {
        Integer victoryQuantity = ratingRecord.getVictoryQuantity();
        victoryQuantity += 1;
        ratingRecord.setVictoryQuantity(victoryQuantity);
    }

    private void increaseLossQuantity(RatingRecord ratingRecord) {
        int lossQuantity = ratingRecord.getLossQuantity();
        lossQuantity += 1;
        ratingRecord.setLossQuantity(lossQuantity);
    }

    private void increasePointQuantity(RatingRecord ratingRecord, Integer points) {
        int pointQuantity = ratingRecord.getPointQuantity();
        pointQuantity += points;
        ratingRecord.setPointQuantity(pointQuantity);
    }

    private void setGameQuantity(RatingRecord ratingRecord) {
        int gameQuantity = ratingRecord.getGameQuantity();
        gameQuantity += 1;
        ratingRecord.setGameQuantity(gameQuantity);
    }

    private RatingRecord getOrCreateRatingRecord(PlayerEntity playerEntity) {
        Optional<RatingRecord> byPlayerLogin = this.ratingRecordRepository.findByPlayerLogin(playerEntity.getLogin());
        if (byPlayerLogin.isEmpty()) {
            RatingRecord ratingRecord = new RatingRecord();
            ratingRecord.setPlayer(playerEntity);
            return this.ratingRecordRepository.save(ratingRecord);
        } else {
            return byPlayerLogin.get();
        }
    }

    public List<RatingRecord> getRatingRecords(){
        List<RatingRecord> all = this.ratingRecordRepository.findAll();
        all.sort(Comparator.comparingInt(RatingRecord::getPointQuantity).reversed());
        return all;
    }
}
