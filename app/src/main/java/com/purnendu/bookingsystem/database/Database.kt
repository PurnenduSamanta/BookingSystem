package com.purnendu.bookingsystem.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

@androidx.room.Database(entities = [FormModel::class], version = 1)
abstract class Database : RoomDatabase() {

    abstract fun taskDao(): Dao


    companion object {
        @Volatile
        private var INSTANCE: Database? = null

        fun getDataBase(context: Context): Database {
            if (INSTANCE == null) {
                synchronized(this)
                {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        Database::class.java,
                        "DB"
                    ).build()
                }
            }
            return INSTANCE!!

        }
    }


}