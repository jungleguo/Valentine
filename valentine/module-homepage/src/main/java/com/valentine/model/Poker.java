package com.valentine.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

enum HandType {
    HIGH_CARD, ONE_PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT,
    FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH, ROYAL_FLUSH
}

@Getter
@Setter
public class Poker implements Comparable<Poker> {
    private String id;
    private String rank;
    private String suit;
    private int value;

    public Poker() {

    }

    public Poker(int value, String suit) {
        this.rank = ""+value;
        this.suit = suit;
        this.value = value;
    }

    public static final String[] suits = new String[]{"♠", "♥", "♦", "♣"};
    public static final String[] ranks = new String[]{"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
    private static final List<String> lRanks = Arrays.asList(ranks);

    @Override
    public int compareTo(Poker other) {
        return Integer.compare(other.value, this.value); // 降序排序
    }

    public static List<Poker> createDeck() {
        return Arrays.stream(suits).flatMap(s -> {
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
    }

}
