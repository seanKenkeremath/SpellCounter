package com.kenkeremath.mtgcounter.persistence.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

@Entity
public class PlayerProfileWithCountersEntity {
    @Embedded
    public PlayerProfileEntity profile;
    @Relation(
            parentColumn = PlayerProfileEntity.COLUMN_PLAYER_PROFILE_NAME,
            entityColumn = CounterTemplateEntity.COLUMN_COUNTER_TEMPLATE_ID,
            associateBy = @Junction(
                    value = PlayerProfileCounterTemplateCrossRefEntity.class,
                    parentColumn = PlayerProfileCounterTemplateCrossRefEntity.COLUMN_PLAYER_PROFILE_ID,
                    entityColumn = PlayerProfileCounterTemplateCrossRefEntity.COLUMN_COUNTER_TEMPLATE_ID
            )
    )
    public List<CounterTemplateEntity> counters;

    @Override
    public String toString() {
        return "PlayerProfileWithCountersEntity{" +
                "profile=" + profile +
                ", counters=" + counters +
                '}';
    }
}
