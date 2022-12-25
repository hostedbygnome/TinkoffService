package ru.urfu.tinkoffservice.service;

import lombok.Getter;

public enum AvailableClassCodes {
    MOEX("MOEX"),
    LSE("LSE"),
    MOEX_EVENING_WEEKEND("MOEX_EVENING_WEEKEND"),
    SPBMX("SPBMX"),
    TQBR("TQBR");

    private String classCard;
    AvailableClassCodes(String classCard) {
        this.classCard = classCard;
    }
}
