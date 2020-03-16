package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.model.TabletopType

interface Datastore {
    fun getNewCounterTemplateId() : Int
    var startingLife : Int
    var numberOfPlayers: Int
    var keepScreenOn : Boolean
    var hideNavigation : Boolean
    var tabletopType: TabletopType
}