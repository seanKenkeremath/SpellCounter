package com.kenkeremath.mtgcounter.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity(
    tableName = PlayerCounterTemplateCrossRefEntity.TABLE_PLAYER_COUNTER_CROSS_REFS,
    primaryKeys = [PlayerCounterTemplateCrossRefEntity.COLUMN_PLAYER_TEMPLATE_ID,
        PlayerCounterTemplateCrossRefEntity.COLUMN_COUNTER_TEMPLATE_ID]
)
data class PlayerCounterTemplateCrossRefEntity(
    @ColumnInfo(name = COLUMN_PLAYER_TEMPLATE_ID)
    val playerTemplateId: String,
    @ColumnInfo(name = COLUMN_COUNTER_TEMPLATE_ID)
    val counterTemplateId: Int
) {
    companion object {
        const val TABLE_PLAYER_COUNTER_CROSS_REFS = "player_counter_cross_refs"
        const val COLUMN_PLAYER_TEMPLATE_ID = "x_player_template_id"
        const val COLUMN_COUNTER_TEMPLATE_ID = "x_counter_template_id"
    }
}