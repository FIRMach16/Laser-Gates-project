package com.example.speedray.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.DeleteTable
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SprintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSprint(sprint: Sprint)

    @Query("DELETE FROM sprints_table")
    suspend fun ClearSprints()

    @Query("SELECT * FROM sprints_table ORDER BY dateOfSprint DESC" )
    fun readAllData(): LiveData<List<Sprint>>


}