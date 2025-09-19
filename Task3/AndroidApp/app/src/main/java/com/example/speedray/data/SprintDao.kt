package com.example.speedray.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SprintDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSprint(sprint: Sprint)

    @Query("SELECT * FROM sprints_table ORDER BY dateOfSprint DESC" )
    fun readAllData(): LiveData<List<Sprint>>
}