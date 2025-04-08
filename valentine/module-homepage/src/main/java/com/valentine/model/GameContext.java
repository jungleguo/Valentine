package com.valentine.model;

import com.valentine.DTO.PlayerDTO;
import com.valentine.DTO.PotPoolDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class GameContext {
    public int currentBetLevel;
    public int requiredCallAmount;
    public int dealerIndex;
    public PlayerDTO actionPlayer;
    public PlayerDTO lastRaiser;
    public PlayerDTO dealer;
    public PlayerDTO smallBlind;
    public PlayerDTO bigBlind;
    public List<PlayerDTO> players;
    public GameState state;
    public List<Poker> communityCards;
    public Map<Integer, PotPoolDTO> pools;

    public GameContext() {
        this.players = new ArrayList<>();
        this.state = GameState.INIT;
        this.communityCards = new ArrayList<>();
        this.pools = new HashMap<>();
    }

    public void rotateContext() {
        this.currentBetLevel = 0;
        this.dealerIndex = 0;
        this.actionPlayer = null;
        this.lastRaiser = null;
        this.dealer = null;
        this.smallBlind = null;
        this.bigBlind = null;
        players.clear();
        state = GameState.INIT;
        communityCards.clear();
        pools.clear();
    }

    public void resetState() {
        this.currentBetLevel = 0;
        this.requiredCallAmount = 0;
        this.lastRaiser = null;
        this.players.forEach(PlayerDTO::resetRoundState);
    }

    public void initialPlayers(List<GamePlayer> players) {
        for (GamePlayer p : players) {
            var player = this.players.stream().filter(i -> i.userId.equals(p.id)).findFirst();
            if (player.isEmpty()) {
                this.players.add(p.toPlayerDTO());
            }
        }
    }

    public void initialPotPool(int index, PotPool pool) {
        var exists = this.pools.get(index);
        if (exists == null)
            this.pools.put(index, pool.toDTO());
    }

    public void updatePool(PotPool pool) {
        var exists = this.pools.values().stream().filter(i -> i.id == pool.id).findFirst();
        if (exists.isPresent()){
            var currentPool = exists.get();
            var dto = pool.toDTO();
            currentPool.pot = dto.pot;
            currentPool.players = dto.players;
            currentPool.winners = dto.winners;
        } else {
            this.pools.put(pool.id, pool.toDTO());
        }
    }

    public void updatePlayer(GamePlayer player) {
        var currentResult = this.players.stream().filter(i -> i.userId.equals(player.id)).findFirst();
        if (currentResult.isPresent()) {
            var existPlayer = currentResult.get();
            existPlayer.isActive = player.isActive;
            existPlayer.actedThisRound = player.actedThisRound;
            existPlayer.chips = player.chips;
            existPlayer.currentBets = player.currentBet;
            existPlayer.isAllIn = player.isAllIn;
        }
    }

    public void  playersShowHands(GamePlayer player) {
        var currentResult = this.players.stream().filter(i -> i.userId.equals(player.id)).findFirst();
        if (currentResult.isPresent()) {
            var existsPlayer = currentResult.get();
            existsPlayer.cards = player.holeCards.stream().map(Poker::ToCardDTO).toList();
        }
    }

    public void updateCommunityCards(List<Poker> communityCards) {
        for (Poker card : communityCards) {
            var existsCard = this.communityCards.stream().filter(i -> i.getId().equals(card.getId())).findFirst();
            if (existsCard.isEmpty())
                this.communityCards.add(card);
        }
    }

    public void nextGame() {
        this.state = GameState.PRE_FLOP;
        this.currentBetLevel = 0;
        this.requiredCallAmount = 0;
        this.dealerIndex = 0;
        this.players.forEach(PlayerDTO::nextGame);
        this.pools.clear();
        this.communityCards.clear();
        this.actionPlayer = null;
        this.lastRaiser = null;
        this.dealer = null;
        this.smallBlind = null;
        this.bigBlind = null;
    }

    public void setWinners(List<GamePlayer> players) {

    }
}
