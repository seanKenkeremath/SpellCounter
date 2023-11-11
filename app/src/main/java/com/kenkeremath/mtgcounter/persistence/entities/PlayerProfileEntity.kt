package com.kenkeremath.mtgcounter.persistence.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kenkeremath.mtgcounter.persistence.entities.PlayerProfileEntity.Companion.TABLE_PLAYER_PROFILES

@Entity(tableName = TABLE_PLAYER_PROFILES)
data class PlayerProfileEntity(
    @PrimaryKey
    @ColumnInfo(name = COLUMN_PLAYER_PROFILE_NAME)
    val name: String = "",
    //Default profiles should not be removable
    @ColumnInfo(name = "deletable")
    val deletable: Boolean = false,
    @ColumnInfo(name = "life_counter_id")
    val lifeCounterId: Int? = null
) {

    companion object {
        const val TABLE_PLAYER_PROFILES = "player_profiles"
        const val COLUMN_PLAYER_PROFILE_NAME = "player_profile_name"
    }

    override fun toString(): String {
        return "PlayerProfileEntity(name='$name')"
    }
}


