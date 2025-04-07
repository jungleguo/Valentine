package com.valentine.DTO;

import java.util.ArrayList;
import java.util.List;

public class PlayerDTO {
    public String userId;
    public String userName;
    public boolean isActive;
    public boolean actedThisRound;
    public int chips;
    public int currentBets;
    public List<CardDTO> cards = new ArrayList<>();

    public void resetRoundState() {
        this.currentBets = 0;
        this.actedThisRound = false;
    }

    public void nextGame() {
        this.cards = new ArrayList<>();
    }
}
