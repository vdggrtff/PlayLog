package com.vdggrtf.playlog.di

import android.content.Context
import androidx.room.Room
import com.vdggrtf.playlog.data.local.db.AppDataBase
import com.vdggrtf.playlog.data.local.entity.DB_NAME
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
            DB_NAME,
        ).fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }

    @Provides
    @Singleton
    fun provideGameDao(appDataBase: AppDataBase) = appDataBase.gameDao()

}