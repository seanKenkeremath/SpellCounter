package com.kenkeremath.mtgcounter.model

import com.kenkeremath.mtgcounter.view.TableLayoutPosition

enum class TabletopType(val numberOfPlayers: Int, val positions: List<TableLayoutPosition>) {
    //If layout applies to any number, -1 is returned for numberOfPlayers
    NONE(-1, emptyList()),
    LIST(-1, emptyList()),
    ONE_HORIZONTAL(1, listOf(TableLayoutPosition.SOLO_PANEL)),
    TWO_HORIZONTAL(2, listOf(TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_1)),
    TWO_VERTICAL(2, listOf(TableLayoutPosition.TOP_PANEL, TableLayoutPosition.BOTTOM_PANEL)),
    THREE_CIRCLE(3, listOf(TableLayoutPosition.TOP_PANEL, TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_1)),
    THREE_ACROSS(3, listOf(TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_2)),
    FOUR_CIRCLE(4, listOf(TableLayoutPosition.TOP_PANEL, TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_1, TableLayoutPosition.BOTTOM_PANEL)),
    FOUR_ACROSS(4, listOf(TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.LEFT_PANEL_2,TableLayoutPosition.RIGHT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_2)),
    FIVE_CIRCLE(5, listOf(TableLayoutPosition.TOP_PANEL, TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.LEFT_PANEL_2,TableLayoutPosition.RIGHT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_2)),
    FIVE_ACROSS(5, listOf(TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.LEFT_PANEL_2,TableLayoutPosition.RIGHT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_2, TableLayoutPosition.RIGHT_PANEL_3)),
    SIX_CIRCLE(6, listOf(TableLayoutPosition.TOP_PANEL, TableLayoutPosition.TOP_PANEL, TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.LEFT_PANEL_2,TableLayoutPosition.RIGHT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_2, TableLayoutPosition.BOTTOM_PANEL)),
    SIX_ACROSS(6, listOf(TableLayoutPosition.LEFT_PANEL_1, TableLayoutPosition.LEFT_PANEL_2, TableLayoutPosition.LEFT_PANEL_3, TableLayoutPosition.RIGHT_PANEL_1, TableLayoutPosition.RIGHT_PANEL_2, TableLayoutPosition.RIGHT_PANEL_3));

    companion object {

        //returns the tabletop types that are compatible with a given number of players
        fun getListForNumber(numberOfPlayers: Int) : List<TabletopType> {
            val list = mutableListOf<TabletopType>()
            for (value in values()) {
                if (value.numberOfPlayers == numberOfPlayers) {
                    list.add(value)
                }
            }
            if (numberOfPlayers > 1) {
                list.add(LIST)
            }
            return list
        }
    }
}