package com.valentine.model;

import com.valentine.DTO.PotPoolDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PotPool {
    public int id;
    public int pot;
    public List<GamePlayer> players;
    public List<GamePlayer> winners;

    public PotPool(int id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.winners = new ArrayList<>();
    }

    public void onBetting(GamePlayer player) {
        var exists = this.players.stream().filter(i -> i.id.equals(player.id)).findFirst();
        if (exists.isEmpty())
            this.players.add(player);
    }

    public void onSettlement() {
        this.winners = this.players.stream()
                .collect(Collectors.groupingBy(i -> i.best))
                .entrySet()
                .stream().findFirst()
                .map(Map.Entry::getValue).get();
        var settledPot = this.pot / this.winners.size();
        for (GamePlayer winner : this.winners) {
            winner.chips += settledPot;
        }
    }

    public PotPoolDTO toDTO() {
        var dto = new PotPoolDTO();
        dto.id = this.id;
        dto.pot = this.pot;
        dto.players = this.players.stream().map(GamePlayer::toPlayerDTO).toList();
        dto.winners = this.winners.stream().map(GamePlayer::toPlayerDTO).toList();
        return dto;
    }
}
