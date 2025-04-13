package com.smartpixel.themovielist.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.smartpixel.themovielist.data.local.entity.MovieEntity
import com.smartpixel.themovielist.data.room.RemoteKey
import com.smartpixel.themovielist.data.room.RemoteKeyDao

@Database(entities = [MovieEntity::class, RemoteKey::class], version = 5, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun remoteKeyDao(): RemoteKeyDao
}