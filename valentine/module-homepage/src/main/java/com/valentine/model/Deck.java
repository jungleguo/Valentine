package com.valentine.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Deck {
    private final List<Poker> cards = new ArrayList<>();
    private int dealIndex = 0;
    private static final String[] suits = new String[]{"♠", "♥", "♦", "♣"};
    private static final String[] ranks = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final List<String> lRanks = Arrays.asList(ranks);

    public Deck() {
        initialize();
    }

    private void initialize() {
        var cards = Arrays.stream(suits).flatMap(s -> {
            return Arrays.stream(ranks).map(rank -> {
                Poker singlePoker = new Poker();
                singlePoker.setId(rank + s);
                singlePoker.setSuit(s);
                singlePoker.setRank(rank);
                int value = lRanks.indexOf(rank);
                singlePoker.setValue(value + 2);

                return singlePoker;
            });
        }).collect(Collectors.toList());

        this.cards.addAll(cards);
    }

    public void shuffle() {
        Collections.shuffle(cards);
        dealIndex = 0;
    }

    public void burnCard() {
        if (dealIndex >= cards.size()) {
            throw new IllegalStateException("No cards left to burn");
        }
        dealIndex++;
    }

    public Poker dealCard() {
        return cards.get(dealIndex++);
    }
}
