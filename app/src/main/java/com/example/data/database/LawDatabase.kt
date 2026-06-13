package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.LawDao
import com.example.data.model.*

@Database(
    entities = [
        LawArticle::class,
        Plaintiff::class,
        Prisoner::class,
        AdminAudit::class,
        AIReport::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LawDatabase : RoomDatabase() {
    abstract fun lawDao(): LawDao

    companion object {
        @Volatile
        private var INSTANCE: LawDatabase? = null

        fun getDatabase(context: Context): LawDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LawDatabase::class.java,
                    "yemen_justice_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
