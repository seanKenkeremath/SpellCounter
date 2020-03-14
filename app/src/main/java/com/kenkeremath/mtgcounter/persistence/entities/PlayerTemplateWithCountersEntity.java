package com.kenkeremath.mtgcounter.persistence.entities;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Junction;
import androidx.room.Relation;

import java.util.List;

@Entity
public class PlayerTemplateWithCountersEntity {
    @Embedded
    public PlayerTemplateEntity template;
    @Relation(
            parentColumn = PlayerTemplateEntity.COLUMN_PLAYER_TEMPLATE_NAME,
            entityColumn = CounterTemplateEntity.COLUMN_COUNTER_TEMPLATE_ID,
            associateBy = @Junction(
                    value = PlayerCounterTemplateCrossRefEntity.class,
                    parentColumn = PlayerCounterTemplateCrossRefEntity.COLUMN_PLAYER_TEMPLATE_ID,
                    entityColumn = PlayerCounterTemplateCrossRefEntity.COLUMN_COUNTER_TEMPLATE_ID
            )
    )
    public List<CounterTemplateEntity> counters;

    @Override
    public String toString() {
        return "PlayerTemplateWithCountersEntity{" +
                "template=" + template +
                ", counters=" + counters +
                '}';
    }
}
