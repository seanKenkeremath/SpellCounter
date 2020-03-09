package com.kenkeremath.mtgcounter.model

enum class TabletopType(val numberOfPlayers: Int) {
    //If layout applies to any number, -1 is returned for numberOfPlayers
    NONE(-1),
    LIST(-1),
    ONE_HORIZONTAL(1),
    TWO_HORIZONTAL(2),
    TWO_VERTICAL(2),
    THREE_CIRCLE(3),
    THREE_ACROSS(3),
    FOUR_CIRCLE(4),
    FOUR_ACROSS_HORIZONTAL(4),
    FOUR_ACROSS_VERITCAL(4),
    FIVE_CIRCLE(5),
    FIVE_ACROSS(5),
    SIX_CIRCLE(6),
    SIX_ACROSS(6);
}