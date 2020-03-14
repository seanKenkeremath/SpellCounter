package com.kenkeremath.mtgcounter.dagger

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.kenkeremath.mtgcounter.persistence.*
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(val application: Application) {

    @Provides
    @Singleton
    fun providesContext(): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun providesMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    fun providesDatastore(context: Context, moshi: Moshi): Datastore {
        return DatastoreImpl(context, moshi)
    }

    @Provides
    @Singleton
    fun providesDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "template_database"
        ).build()
    }

    @Provides
    fun providesGameRepository(database: AppDatabase, datastore: Datastore): GameRepository {
        return GameRepositoryImpl(database, datastore)
    }
}
