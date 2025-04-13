package com.smartpixel.themovielist.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(remoteKey: RemoteKey)

    @Query("SELECT * FROM remote_keys WHERE query = :query")
    suspend fun remoteKeyByQuery(query: String): RemoteKey?

    @Query("DELETE FROM remote_keys WHERE query = :query")
    suspend fun deleteByQuery(query: String)
}