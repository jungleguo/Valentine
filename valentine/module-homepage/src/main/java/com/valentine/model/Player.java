package com.valentine.model;

import com.valentine.DTO.PlayerDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Setter
@Getter
public class Player {
    private String id;
    private String username;
    private int chips;
    private int bet;
    private UserRole role = UserRole.PLAYER; // Dealer | S-Blind | B-blind
    public PlayerStatus status = PlayerStatus.READY;
    private Boolean isActive = false;
    private EvaluatedHand Best;
    public List<Poker> hand = new ArrayList<>();

    public enum PlayerStatus {READY, FOLD, POOL}

    public enum UserRole {DEALER, SMALL_BLIND, BIG_BLIND, GUN, PLAYER}

    public void fold() {
        this.status = PlayerStatus.FOLD;
        this.isActive = false;
        this.hand.clear();
        this.setBest(null);
    }

    public void check() {
        this.status = PlayerStatus.POOL;
        this.isActive = false;
    }

    public void call(int bet) {
        this.status = PlayerStatus.POOL;
        this.isActive = false;
        this.bet = this.bet+bet;
    }

    public void raise(int bet) {
        this.call(bet);
    }

    public void shuffle(List<Poker> pokers) {
        Collections.shuffle(pokers);
    }

    public void license(List<Poker> pokers) {
        hand.addAll(pokers);
    }

    public void earnChips(int chip) {
        this.chips = this.chips + chip;
    }

    public void nextGame() {
        status = PlayerStatus.READY;
        hand.clear();
        bet = 0;
        this.setBest(null);
    }
}
