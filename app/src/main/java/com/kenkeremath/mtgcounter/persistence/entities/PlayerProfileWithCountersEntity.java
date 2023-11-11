package com.kenkeremath.mtgcounter.persistence.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

import javax.annotation.Nullable;

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

    @Relation(
            parentColumn = "life_counter_id",
            entityColumn = "counter_template_id"
    )
    @Nullable
    public CounterTemplateEntity lifeCounter;

    @Override
    public String toString() {
        return "PlayerProfileWithCountersEntity{" +
                "profile=" + profile +
                ", life counter" + lifeCounter +
                ", counters=" + counters +
                '}';
    }
}
