package com.example.speedray.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import androidx.room.TypeConverters

@Database(entities = [Sprint::class], version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class SprintDatabase: RoomDatabase() {
    abstract fun sprintDao(): SprintDao

    companion object{
        // equivalent to static in java
        // Ensuring that class follow the singleton Design Pattern
        @Volatile
        private var INSTANCE: SprintDatabase? =null

        fun getDatabase(context: Context): SprintDatabase{

            val tmpInstance = INSTANCE
            if(tmpInstance!= null){
                return tmpInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SprintDatabase::class.java,
                    "sprints_database"
                ).build()
                INSTANCE =instance
                return instance
            }
        }
    }
}