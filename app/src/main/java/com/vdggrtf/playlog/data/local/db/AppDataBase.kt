package com.vdggrtf.playlog.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vdggrtf.playlog.data.local.dao.GameDao
import com.vdggrtf.playlog.data.local.entity.GameEntity

@Database(entities = [GameEntity::class], version = 2, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}