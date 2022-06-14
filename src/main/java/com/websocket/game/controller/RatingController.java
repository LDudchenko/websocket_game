package com.websocket.game.controller;

import com.websocket.game.model.RatingRecord;
import com.websocket.game.service.PointCounterService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@AllArgsConstructor
public class RatingController {
    private final PointCounterService pointCounterService;

    @GetMapping("/rating")
    public ResponseEntity<List<RatingRecord>> getRatingRecords(){
        return ResponseEntity.ok(this.pointCounterService.getRatingRecords());
    }
}
