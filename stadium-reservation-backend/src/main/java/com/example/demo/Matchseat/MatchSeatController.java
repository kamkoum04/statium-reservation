package com.example.demo.Matchseat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MatchSeatController {

    @Autowired
    private  MatchSeatService matchSeatService;


    @GetMapping("/public/matchSeat/{matchId}")
    public ResponseEntity<List<MatchSeatDto>> getSeatsByMatchId(@PathVariable Long matchId) {
        List<MatchSeatDto> seats = matchSeatService.getSeatsByMatchId(matchId);
        return ResponseEntity.ok(seats);
    }
}
