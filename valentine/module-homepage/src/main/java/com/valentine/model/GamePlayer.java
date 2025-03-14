package com.valentine.model;

import com.valentine.DTO.PlayerDTO;

import java.util.ArrayList;
import java.util.List;

public class GamePlayer {
    public String id;
    public String username;
    public List<Poker> holeCards = new ArrayList<>(2);
    public int chips;
    public int currentBet;
    public boolean isActive = true;
    public EvaluatedHand best;

    public GamePlayer(
            String id,
            String username,
            int initialChips) {
        this.id = id;
        this.username = username;
        this.chips = initialChips;
    }

    public void resetRoundState() {
        currentBet = 0;
        holeCards.clear();
    }

    public PlayerDTO toPlayerDTO() {
        var playerDTO = new PlayerDTO();
        playerDTO.userId = this.id;
        playerDTO.userName = this.username;
        playerDTO.isActive = this.isActive;
        playerDTO.chips = this.chips;
        playerDTO.bets = this.currentBet;
        return playerDTO;
    }
}
