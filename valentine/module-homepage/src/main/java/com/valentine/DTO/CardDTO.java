package com.valentine.DTO;

import com.valentine.model.Poker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@Getter
@Setter
public class CardDTO {
    private Integer rank;  // 对前端隐藏时设为null
    private String suit;

    public static List<CardDTO> fromCards(List<CardDTO> cards) {
        return cards.stream()
                .map(c -> new CardDTO(c.getRank(), String.valueOf(c.getSuit())))
                .collect(Collectors.toList());
    }
}
