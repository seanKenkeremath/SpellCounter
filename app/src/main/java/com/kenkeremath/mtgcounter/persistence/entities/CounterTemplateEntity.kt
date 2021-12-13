package com.kenkeremath.mtgcounter.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity.Companion.TABLE_COUNTER_TEMPLATES

@Entity(tableName = TABLE_COUNTER_TEMPLATES)
data class CounterTemplateEntity(
    @ColumnInfo(name = COLUMN_COUNTER_TEMPLATE_ID)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "color")
    val color: Int? = null,
    @ColumnInfo(name = "symbolId")
    val symbolId: Long = CounterSymbol.DEFAULT_ID,
    @ColumnInfo(name = "uri")
    val uri: String? = null,
    @ColumnInfo(name = "link_to_player")
    val linkToPlayer: Boolean = false,
    //Default symbol tokens should not be removable
    @ColumnInfo(name = "deletable")
    val deletable: Boolean = false
) {
    companion object {
        const val TABLE_COUNTER_TEMPLATES = "counter_templates"
        const val COLUMN_COUNTER_TEMPLATE_ID = "counter_template_id"
    }

    override fun toString(): String {
        return "CounterTemplateEntity(id=$id, name=$name, color=$color, symbolId=$symbolId, uri=$uri, linkToPlayer=$linkToPlayer, deletable=$deletable)"
    }
}