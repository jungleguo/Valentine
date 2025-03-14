package com.valentine.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAction {
    private String userId;
    public Action action;
    private int chips;

    public enum Action { CHECK, FOLD, CALL, RAISE, ALLIN }
}
