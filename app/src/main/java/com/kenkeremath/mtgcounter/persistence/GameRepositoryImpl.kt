package com.kenkeremath.mtgcounter.persistence

import com.kenkeremath.mtgcounter.model.TabletopType
import javax.inject.Inject


class GameRepositoryImpl @Inject constructor(
    private val datastore: Datastore,
) : GameRepository {
    override var startingLife: Int
        get() = datastore.startingLife
        set(value) {
            datastore.startingLife = value
        }
    override var numberOfPlayers: Int
        get() = datastore.numberOfPlayers
        set(value) {
            datastore.numberOfPlayers = value
        }
    override var keepScreenOn: Boolean
        get() = datastore.keepScreenOn
        set(value) {
            datastore.keepScreenOn = value
        }
    override var tabletopType: TabletopType
        get() = datastore.tabletopType
        set(value) {
            datastore.tabletopType = value
        }
    override var hideNavigation: Boolean
        get() = datastore.hideNavigation
        set(value) {
            datastore.hideNavigation = value
        }
}

