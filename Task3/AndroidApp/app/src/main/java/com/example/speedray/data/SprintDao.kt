package com.example.speedray.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.Date

@Dao
interface SprintDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSprint(sprint: Sprint)

    @Delete
    suspend fun deleteSprint(sprint: Sprint)

    @Query("DELETE FROM sprints_table")
    suspend fun clearSprints()

    @Query("SELECT * FROM sprints_table ORDER BY dateOfSprint DESC")
    fun readAllData(): LiveData<List<Sprint>>

    @Query("SELECT * FROM sprints_table  WHERE distanceOfBuildUp>0 ORDER BY time/distanceBetweenGates DESC LIMIT 1")
    fun getBestTopEnd(): Sprint

    @Query("SELECT * FROM sprints_table WHERE distanceOfBuildUp>0 ORDER BY dateOfSprint DESC LIMIT 1")
    fun getLatestTopEnd(): Sprint

    @Query("SELECT * FROM sprints_table  WHERE distanceOfBuildUp=0 ORDER BY time/distanceBetweenGates DESC LIMIT 1")
    fun getBestAcc(): Sprint

    @Query("SELECT * FROM sprints_table  WHERE distanceOfBuildUp=0 ORDER BY dateOfSprint DESC LIMIT 1")
    fun getLatestAcc(): Sprint

    // for testing purposes
    @Query("SELECT dateOfSprint FROM sprints_table")
    fun getSprintDates():  List<Date>



}