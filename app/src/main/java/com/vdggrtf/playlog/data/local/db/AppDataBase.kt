package com.vdggrtf.playlog.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vdggrtf.playlog.data.local.dao.GameDao
import com.vdggrtf.playlog.data.local.dao.PlaylistDao
import com.vdggrtf.playlog.data.local.entity.GameEntity
import com.vdggrtf.playlog.data.local.entity.PlaylistEntity
import com.vdggrtf.playlog.data.local.entity.PlaylistGameCrossRef

@Database(entities = [GameEntity::class, PlaylistEntity::class, PlaylistGameCrossRef::class], version = 3, exportSchema = false)
abstract class AppDataBase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract val playlistDao: PlaylistDao
}