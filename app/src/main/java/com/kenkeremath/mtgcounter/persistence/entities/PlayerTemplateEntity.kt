package com.kenkeremath.mtgcounter.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kenkeremath.mtgcounter.persistence.entities.PlayerTemplateEntity.Companion.TABLE_PLAYER_TEMPLATES

@Entity(tableName = TABLE_PLAYER_TEMPLATES)
data class PlayerTemplateEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_PLAYER_TEMPLATE_NAME)
    val name: String = "",
    //Default profiles should not be removable
    @ColumnInfo(name = "deletable")
    val deletable: Boolean = false
) {

    companion object {
        const val TABLE_PLAYER_TEMPLATES = "player_templates"
        const val COLUMN_PLAYER_TEMPLATE_NAME = "player_template_name"
    }

    override fun toString(): String {
        return "PlayerTemplateEntity(name='$name')"
    }
}


