package com.smartpixel.themovielist.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKey(
    @PrimaryKey val query: String,
    val nextPage: Int?
)