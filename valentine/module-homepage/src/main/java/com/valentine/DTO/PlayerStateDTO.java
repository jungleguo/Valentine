package com.valentine.DTO;

import lombok.Data;

import java.util.List;

@Data
public class PlayerStateDTO {
    private String playerId;
    private int chips;
    private int currentBet;
    private boolean isActive;
    private List<CardDTO> holeCards;
}
