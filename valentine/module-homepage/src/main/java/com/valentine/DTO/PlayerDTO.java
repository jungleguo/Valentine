package com.valentine.DTO;

public class PlayerDTO {
    public String userId;
    public String userName;
    public boolean isActive;
    public boolean actedThisRound;
    public int chips;
    public int currentBets;

    public void resetRoundState() {
        this.currentBets = 0;
        this.actedThisRound = false;
    }
}
