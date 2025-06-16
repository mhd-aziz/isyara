package com.application.isyaraapplication.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.application.isyaraapplication.data.model.HistoryItem

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}