package com.ellipticbean.mademargin.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SavedProduct::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MadeMarginDatabase : RoomDatabase() {

    abstract fun savedProductDao(): SavedProductDao

    companion object {
        @Volatile
        private var INSTANCE: MadeMarginDatabase? = null

        fun getDatabase(
            context: Context
        ): MadeMarginDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance =
                    Room.databaseBuilder(
                        context.applicationContext,
                        MadeMarginDatabase::class.java,
                        "mademargin_database"
                    ).build()

                INSTANCE = instance

                instance
            }
        }
    }
}