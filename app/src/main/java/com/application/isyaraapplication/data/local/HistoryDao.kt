package com.application.isyaraapplication.data.local

import androidx.room.*
import com.application.isyaraapplication.data.model.HistoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryItem)

    @Query("SELECT * FROM history_items WHERE userId = :userId ORDER BY timestamp DESC")
    fun getHistoryForUser(userId: String): Flow<List<HistoryItem>>

    @Query("DELETE FROM history_items WHERE userId = :userId")
    suspend fun clearHistoryForUser(userId: String)
}