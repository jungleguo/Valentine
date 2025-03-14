package com.valentine.DTO;

import com.valentine.model.GameState;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class GameStateDTO {
    private String gameId;
    private GameState currentState;
    private List<CardDTO> communityCards;
    private List<PlayerStateDTO> players;
    private int pot;
    private String currentPlayerId;
    private int currentBetLevel;

    public void setCurrentPlayer(String currentPlayerId) {
        this.currentPlayerId = currentPlayerId;
    }

    public void setPhase(GameState currentState) {
        this.currentState = currentState;
    }
}
