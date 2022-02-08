package com.kenkeremath.mtgcounter.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kenkeremath.mtgcounter.model.player.PlayerColor
import com.kenkeremath.mtgcounter.model.counter.CounterSymbol
import com.kenkeremath.mtgcounter.model.counter.CounterTemplateModel
import com.kenkeremath.mtgcounter.persistence.entities.CounterTemplateEntity.Companion.TABLE_COUNTER_TEMPLATES

@Entity(tableName = TABLE_COUNTER_TEMPLATES)
data class CounterTemplateEntity(
    @ColumnInfo(name = COLUMN_COUNTER_TEMPLATE_ID)
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "name")
    val name: String? = null,
    @ColumnInfo(name = "colorId")
    val colorId: Long = PlayerColor.DEFAULT_ID,
    @ColumnInfo(name = "symbolId")
    val symbolId: Long = CounterSymbol.DEFAULT_ID,
    @ColumnInfo(name = "startingValue")
    val startingValue: Int = 0,
    @ColumnInfo(name = "uri")
    val uri: String? = null,
    @ColumnInfo(name = "full_art")
    val isFullArtImage: Boolean = false,
    @ColumnInfo(name = "date_added")
    val dateAdded: Long = 0L,
    @ColumnInfo(name = "link_to_player")
    val linkToPlayer: Boolean = false,
    //Default symbol tokens should not be removable
    @ColumnInfo(name = "deletable")
    val deletable: Boolean = false
) {

    constructor(counterTemplateModel: CounterTemplateModel): this(
        id = counterTemplateModel.id,
        name = counterTemplateModel.name,
        colorId = counterTemplateModel.color.colorId,
        symbolId = counterTemplateModel.symbol.symbolId,
        uri = counterTemplateModel.uri,
        isFullArtImage = counterTemplateModel.isFullArtImage,
        startingValue = counterTemplateModel.startingValue,
        dateAdded = counterTemplateModel.dateAdded?.time ?: 0,
        deletable = counterTemplateModel.deletable,
        linkToPlayer = false
    )
    companion object {
        const val TABLE_COUNTER_TEMPLATES = "counter_templates"
        const val COLUMN_COUNTER_TEMPLATE_ID = "counter_template_id"
    }

    override fun toString(): String {
        return "CounterTemplateEntity(id=$id, name=$name, colorId=$colorId, symbolId=$symbolId, startingValue=$startingValue, uri=$uri, dateAdded=$dateAdded, linkToPlayer=$linkToPlayer, deletable=$deletable)"
    }
}