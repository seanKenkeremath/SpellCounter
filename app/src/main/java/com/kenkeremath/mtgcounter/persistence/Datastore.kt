package com.kenkeremath.mtgcounter.persistence

interface Datastore {
    fun getNewCounterTemplateId() : Int
}