package com.valentine.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ChipPool {
    public int chip;
    public int round = 0;
    public boolean isBalanced;
    public  PoolStatus status = PoolStatus.ACTIVE;
    private static final List<Player> players = new ArrayList<>();

    public enum PoolStatus {ACTIVE, FREEZE}

    public void onPlayerBet(Player player) {
        var exists = players.stream().filter(p -> p.getId().equals(player.getId()))
                .findFirst();
        if (exists.isEmpty()) {
            players.add(player);
        }
    }

    public int getChip() {
        this.chip = players.stream().map(p -> p.getBet()).mapToInt(Integer::intValue).sum();
        return this.chip;
    }

    public boolean getIsBalanced() {
        this.isBalanced = players.stream()
                .filter(this::filterToBeBalancedPlayer)
                .collect(Collectors.groupingBy(p -> p.getBet()))
                .size() == 1;
        return this.isBalanced;
    }

    private boolean filterToBeBalancedPlayer(Player player) {
        var isInPoolPlayer = player.status.equals(Player.PlayerStatus.POOL);
        if (round == 0) {
            return !player.getRole().equals(Player.UserRole.SMALL_BLIND)
                    && isInPoolPlayer;
        }else {
            return isInPoolPlayer;
        }
    }

    public void settleChips(List<Poker> communityPokers) {
        // Group by result to deal with the draw
        // There may have multiple winners
        List<Player> winners = this.players.stream()
                .filter(player -> player.status.equals(Player.PlayerStatus.POOL))
                .map(player -> {
                    player.hand.addAll(communityPokers);
                    return player;
                })
                .map(player -> {
                    EvaluatedHand playerBest = PokerHandEvaluator.generateCombinations(player.hand).stream()
                            .map(PokerHandEvaluator::evaluate)
                            .max(Comparator.naturalOrder())
                            .get();
                    player.setBest(playerBest);
                    return player;
                })
                .sorted(Comparator.comparing(Player::getBest))
                .collect(Collectors.groupingBy(p -> p.getBest().type))
                .entrySet()
                .stream().findFirst().get().getValue();

        int earnedChips = chip / winners.size();
        for (Player winner : winners) {
            winner.earnChips(earnedChips);
        }
    }
}
