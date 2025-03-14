package com.valentine;

import com.valentine.model.EvaluatedHand;
import com.valentine.model.Poker;
import com.valentine.model.PokerHandEvaluator;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class PokeComparison {
    public  static void main(String[] args) {
        List<Poker> player1 = Arrays.asList(
                new Poker(14, "H"),
                new Poker(13, "D"), // 手牌

                new Poker(12, "H"), // 公共牌
                new Poker(11, "H"),
                new Poker(10, "H"),
                new Poker(2, "D"),
                new Poker(3, "S")
        );

        List<Poker> player3 = Arrays.asList(
                new Poker(9, "D"),
                new Poker(13, "H"), // 手牌

                new Poker(12, "S"), // 公共牌
                new Poker(11, "S"),
                new Poker(10, "S"),
                new Poker(2, "D"),
                new Poker(3, "S")
        );

//        List<Poker> player2 = Arrays.asList(
//                new Poker(9, "C"),
//                new Poker(9, "S"),
//                new Poker(9, "D"),
//                new Poker(9, "H"),
//                new Poker(12, "S"),
//                new Poker(5, "C"),
//                new Poker(2, "S")
//        );

        // 找到最佳组合
        EvaluatedHand best1 = PokerHandEvaluator.generateCombinations(player1).stream()
                .map(PokerHandEvaluator::evaluate)
                .max(Comparator.naturalOrder())
                .get();

        EvaluatedHand best3 = PokerHandEvaluator.generateCombinations(player3).stream()
                .map(PokerHandEvaluator::evaluate)
                .max(Comparator.naturalOrder())
                .get();

        System.out.println("Player1: " + best1.type + " " + best1.comparisonKey);
        System.out.println("Player2: " + best3.type + " " + best3.comparisonKey);
        System.out.println("Winner: " + (best3.compareTo(best1) > 0 ? "Player3" : "Player1"));
    }
}
