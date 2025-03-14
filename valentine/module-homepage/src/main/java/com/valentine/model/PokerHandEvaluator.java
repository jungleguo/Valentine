package com.valentine.model;

import java.util.*;
import java.util.stream.Collectors;

public class PokerHandEvaluator {

    public static List<List<Poker>> generateCombinations(List<Poker> pokers) {
        List<List<Poker>> result = new ArrayList<>();
        combine(pokers, 0, new ArrayList<>(), result);
        return result;
    }

    private static void combine(
            List<Poker> pokers,
            int start,
            List<Poker> current,
            List<List<Poker>> result
    ) {
        if (current.size() == 5) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < pokers.size(); i++) {
            current.add(pokers.get(i));
            combine(pokers, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public static EvaluatedHand evaluate(List<Poker> hand) {
        hand = new ArrayList<>(hand);
        Collections.sort(hand);

        boolean isFlush = isFlush(hand);
        boolean isStraight = isStraight(hand);
        int[] valueCounts = getValueCounts(hand);

        if(isFlush && isStraight && hand.get(0).getValue() == 14 && hand.get(4).getValue() == 10) {
            return  new EvaluatedHand(HandType.ROYAL_FLUSH, getComparisonKey(hand, HandType.ROYAL_FLUSH));
        }

        // 同花顺
        if (isFlush && isStraight) {
            return new EvaluatedHand(HandType.STRAIGHT_FLUSH, getComparisonKey(hand, HandType.STRAIGHT_FLUSH));
        }

        // 四条
        if (valueCounts[0] == 4) {
            return new EvaluatedHand(HandType.FOUR_OF_A_KIND, getComparisonKey(hand, HandType.FOUR_OF_A_KIND));
        }

        // 葫芦
        if (valueCounts[0] == 3 && valueCounts[1] == 2) {
            return new EvaluatedHand(HandType.FULL_HOUSE, getComparisonKey(hand, HandType.FULL_HOUSE));
        }

        // 同花
        if (isFlush) {
            return new EvaluatedHand(HandType.FLUSH, getComparisonKey(hand, HandType.FLUSH));
        }

        // 顺子
        if (isStraight) {
            return new EvaluatedHand(HandType.STRAIGHT, getComparisonKey(hand, HandType.STRAIGHT));
        }

        // 三条
        if (valueCounts[0] == 3) {
            return new EvaluatedHand(HandType.THREE_OF_A_KIND, getComparisonKey(hand, HandType.THREE_OF_A_KIND));
        }

        // 两对
        if (valueCounts[0] == 2 && valueCounts[1] == 2) {
            return new EvaluatedHand(HandType.TWO_PAIR, getComparisonKey(hand, HandType.TWO_PAIR));
        }

        // 一对
        if (valueCounts[0] == 2) {
            return new EvaluatedHand(HandType.ONE_PAIR, getComparisonKey(hand, HandType.ONE_PAIR));
        }

        // 高牌
        return new EvaluatedHand(HandType.HIGH_CARD, getComparisonKey(hand, HandType.HIGH_CARD));
    }

    private static List<Integer> getComparisonKey(List<Poker> pokers, HandType type) {
        List<Integer> key = new ArrayList<>();
        List<Poker> sorted = new ArrayList<>(pokers);
        sorted.sort(Comparator.reverseOrder());
        switch (type) {
            case ROYAL_FLUSH:
            case STRAIGHT_FLUSH:
                key.add(pokers.get(0).getValue());
                break;
            case FOUR_OF_A_KIND:
                int quadValue = getValueWithCount(pokers, 4);
                key.add(quadValue);
                key.add(sorted.stream().filter(p -> p.getValue() != quadValue).findFirst().get().getValue());
                break;
            case FULL_HOUSE:
                int tripleValue = getValueWithCount(pokers, 3);
                int pairValue = getValueWithCount(pokers, 2);
                key.add(tripleValue);
                key.add(pairValue);
                break;
            case FLUSH:
            case HIGH_CARD:
                sorted.forEach(p -> key.add(p.getValue()));
                break;
            case STRAIGHT:
                key.add(isWheel(pokers) ? 5 : sorted.get(0).getValue());
                break;
            case THREE_OF_A_KIND:
                int triple = getValueWithCount(pokers, 3);
                key.add(triple);
                sorted.stream().filter(p -> p.getValue() != triple)
                        .forEach(p -> key.add(p.getValue()));
                break;
            case TWO_PAIR:
                List<Integer> pairs = getValuesWithCount(pokers, 2);
                int kicker = sorted.stream()
                        .filter(p -> !pairs.contains(p.getValue()))
                        .findFirst().get().getValue();
                key.addAll(pairs);
                key.add(kicker);
                break;
            case ONE_PAIR:
                int pair = getValueWithCount(pokers, 2);
                key.add(pair);
                sorted.stream().filter(p -> p.getValue() != pair)
                        .forEach(p -> key.add(p.getValue()));
                break;
        }
        return key;
    }

    private static boolean isFlush(List<Poker> pokers) {
        return pokers.stream().map(c -> c.getSuit()).distinct().count() == 1;
    }

    private static boolean isStraight(List<Poker> pokers) {
        if (isWheel(pokers)) return true;

        for (int i = 0; i < 4; i++) {
            if (pokers.get(i).getValue() - pokers.get(i + 1).getValue() != 1)
                return false;
        }
        return true;
    }

    private static boolean isWheel(List<Poker> pokers) {
        return pokers.get(0).getValue() == 14
                && pokers.get(1).getValue() == 5
                && pokers.get(2).getValue() == 4
                && pokers.get(3).getValue() == 3
                && pokers.get(4).getValue() == 2;

    }

    private static int getValueWithCount(List<Poker> pokers, int count) {
        Map<Integer, Integer> valueCount = new HashMap<>();
        for (Poker poker : pokers) {
            valueCount.put(poker.getValue(), valueCount.getOrDefault(poker.getValue(), 0) + 1);
        }

        return valueCount.entrySet()
                .stream()
                .filter(e -> e.getValue() == count)
                .sorted((a, b) -> Integer.compare(b.getKey(), a.getKey()))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalArgumentException("No value with count" + count));
    }

    private static List<Integer> getValuesWithCount(List<Poker> pokers, int count) {
        Map<Integer, Integer> rankCount = new HashMap<>();
        for (Poker card : pokers) {
            rankCount.put(card.getValue(), rankCount.getOrDefault(card.getValue(), 0) + 1);
        }

        return rankCount.entrySet().stream()
                .filter(e -> e.getValue() == count)
                .sorted((a, b) -> Integer.compare(b.getKey(), a.getKey())) // 降序排序
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private static int[] getValueCounts(List<Poker> pokers) {
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Poker poker : pokers) {
            countMap.put(poker.getValue(), countMap.getOrDefault(poker.getValue(), 0) + 1);
        }

        return countMap.values().stream()
                .sorted(Comparator.reverseOrder())
                .mapToInt(i -> i)
                .toArray();
    }
}
