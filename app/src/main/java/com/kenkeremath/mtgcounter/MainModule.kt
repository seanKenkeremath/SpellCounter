package com.kenkeremath.mtgcounter

import android.content.Context
import androidx.room.Room
import com.kenkeremath.mtgcounter.persistence.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    fun providesDatastore(@ApplicationContext appContext: Context, moshi: Moshi): Datastore {
        return DatastoreImpl(appContext, moshi)
    }

    @Provides
    @Singleton
    fun providesDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext.applicationContext,
            AppDatabase::class.java,
            "template_database"
        ).build()
    }

    @Provides
    fun providesGameRepository(database: AppDatabase, datastore: Datastore): GameRepository {
        return GameRepositoryImpl(database, datastore)
    }
}