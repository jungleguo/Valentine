package com.valentine.DTO;

import com.valentine.model.PlayerAction;
import lombok.Data;

@Data
public class PlayerActionRequest {

    private String gameId;

    private String playerId;

    private PlayerAction action;

    private Integer amount;
}
