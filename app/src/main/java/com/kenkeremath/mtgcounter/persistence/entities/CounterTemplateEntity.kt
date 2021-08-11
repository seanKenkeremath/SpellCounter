package com.kenkeremath.mtgcounter.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity.Companion.TABLE_COUNTER_TEMPLATES

@Entity(tableName = TABLE_COUNTER_TEMPLATES)
data class CounterTemplateEntity(
    @ColumnInfo(name = COLUMN_COUNTER_TEMPLATE_ID)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "starting_amount")
    val startingAmount: Int = 0,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "color")
    val color: Int = 0,
    @ColumnInfo(name = "link_to_player")
    val linkToPlayer: Boolean = false
//TODO: Symbol
) {
    companion object {
        const val TABLE_COUNTER_TEMPLATES = "counter_templates"
        const val COLUMN_COUNTER_TEMPLATE_ID = "counter_template_id"
    }

    override fun toString(): String {
        return "CounterTemplateEntity(id=$id, startingAmount=$startingAmount, name=$name, color=$color, linkToPlayer=$linkToPlayer)"
    }
}