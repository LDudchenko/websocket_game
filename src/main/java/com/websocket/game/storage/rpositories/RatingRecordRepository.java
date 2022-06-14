package com.websocket.game.storage.rpositories;

import com.websocket.game.model.RatingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RatingRecordRepository extends JpaRepository<RatingRecord, Long> {
    Optional<RatingRecord> findByPlayerLogin(String login);
}
