package com.valentine.model;

import java.util.List;

public class EvaluatedHand implements Comparable<EvaluatedHand> {
    public final HandType type;
    public final List<Integer> comparisonKey;

    EvaluatedHand(HandType type, List<Integer> comparisonKey) {
        this.type = type;
        this.comparisonKey = comparisonKey;
    }

    @Override
    public int compareTo(EvaluatedHand other) {
        int typeCompare = Integer.compare(this.type.ordinal(), other.type.ordinal());
        if (typeCompare != 0) return typeCompare;

        for (int i = 0; i < this.comparisonKey.size(); i++) {
            int keyCompare = Integer.compare(
                    this.comparisonKey.get(i),
                    other.comparisonKey.get(i)
            );
            if (keyCompare != 0) return keyCompare;
        }
        return 0;
    }
}
