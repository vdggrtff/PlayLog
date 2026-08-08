package com.vdggrtf.playlog.di

import android.content.Context
import androidx.room.Room
import com.vdggrtf.playlog.data.local.dao.PlaylistDao
import com.vdggrtf.playlog.data.local.db.AppDataBase
import com.vdggrtf.playlog.data.local.entity.GAME_DB_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Provides
    @Singleton
    fun provideAppDataBase(@ApplicationContext context: Context): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            GAME_DB_NAME,
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideGameDao(appDataBase: AppDataBase) = appDataBase.gameDao()

    @Provides
    @Singleton
    fun providePlaylistDao(database: AppDataBase): PlaylistDao {
        return database.playlistDao
    }
}