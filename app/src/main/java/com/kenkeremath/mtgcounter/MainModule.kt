package com.kenkeremath.mtgcounter

import android.content.Context
import androidx.room.Room
import com.kenkeremath.mtgcounter.legacy.MigrationHelper
import com.kenkeremath.mtgcounter.persistence.*
import com.kenkeremath.mtgcounter.persistence.images.ImageApi
import com.kenkeremath.mtgcounter.persistence.images.ImageRepository
import com.kenkeremath.mtgcounter.persistence.images.ImageRepositoryImpl
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
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
    @Singleton
    fun providesMigrationHelper(appDatabase: AppDatabase, datastore: Datastore): MigrationHelper {
        return MigrationHelper(datastore, appDatabase)
    }

    @Provides
    @Singleton
    fun provideImageApi(): ImageApi {
        return Retrofit.Builder()
            .baseUrl("https://www.google.com") //using dynamic urls. Need a dummy base url
            .build()
            .create(ImageApi::class.java)
    }

    @Provides
    @Singleton
    fun providesGameRepository(datastore: Datastore): GameRepository {
        return GameRepositoryImpl(datastore)
    }

    @Provides
    @Singleton
    fun providesProfileRepository(database: AppDatabase, datastore: Datastore): ProfileRepository {
        return ProfileRepositoryImpl(database, datastore)
    }

    @Provides
    @Singleton
    fun providesImageRepository(
        @ApplicationContext appContext: Context,
        imageApi: ImageApi,
    ): ImageRepository {
        return ImageRepositoryImpl(appContext, imageApi)
    }
}