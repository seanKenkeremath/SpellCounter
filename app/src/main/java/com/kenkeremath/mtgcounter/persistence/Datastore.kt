package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.legacy.model.LegacyPlayerTemplateModel
import com.kenkeremath.mtgcounter.model.TabletopType

interface Datastore {

    companion object {
        /**
         * --- 1.1 = 0
         * --- 1.5 = 1
         * --- 2.0 = 1
         * we added editMode flag but that can default to false
         * --- 2.1 = 1
         * added game templates, but this requires no change
         * --- 3.0 = 2
         * rewrite, lot of changes. Using Room instead of DS, Themes not stored in DS.
         * Performing migration for any version below this to parse stored player templates into
         * Profiles and Counter Templates
         */
        const val VERSION_1_5 = 1
        const val VERSION_3_0 = 2
        const val CURRENT_VERSION = VERSION_3_0
    }

    var startingLife: Int
    var numberOfPlayers: Int
    var keepScreenOn: Boolean
    var hideNavigation: Boolean
    var tabletopType: TabletopType
    val version: Int

    //Mark as having completed migration + setup for current app version
    fun updateVersion()

    //Legacy
    val legacyPlayerTemplates: List<LegacyPlayerTemplateModel>
}