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
    fun readAllData(): LiveData<List<Sprint>> //In this case i think LiveData is not very useful
    // because if i need an observer on the data i can do it in a view model
    // and also LiveData.value may not give the latest value on a background thread
    @Query("SELECT * FROM sprints_table ORDER BY dateOfSprint DESC LIMIT :numberOfSprints")
    fun showLimitedMixedData(numberOfSprints: Int): List<Sprint>

    @Query("SELECT * FROM sprints_table WHERE distanceOfBuildUp>0 ORDER BY dateOfSprint DESC LIMIT :numberOfSprints")
    fun showLimitedTopEndData(numberOfSprints: Int) : List<Sprint>

    @Query("SELECT * FROM sprints_table WHERE distanceOfBuildUp=0 ORDER BY dateOfSprint DESC LIMIT :numberOfSprints")
    fun showLimitedAccelerationData(numberOfSprints: Int) : List<Sprint>


    @Query("SELECT * FROM sprints_table ORDER BY dateOfSprint DESC")
    fun getAllSprints(): List<Sprint>

    @Query("SELECT * FROM sprints_table  WHERE distanceOfBuildUp>0 ORDER BY distanceBetweenGates/time DESC LIMIT 1")
    fun getBestTopEnd(): Sprint

    @Query("SELECT * FROM sprints_table WHERE distanceOfBuildUp>0 ORDER BY dateOfSprint DESC LIMIT 1")
    fun getLatestTopEnd(): Sprint

    @Query("SELECT * FROM sprints_table  WHERE distanceOfBuildUp=0 ORDER BY distanceBetweenGates/time DESC LIMIT 1")
    fun getBestAcc(): Sprint

    @Query("SELECT * FROM sprints_table  WHERE distanceOfBuildUp=0 ORDER BY dateOfSprint DESC LIMIT 1")
    fun getLatestAcc(): Sprint

    @Query("SELECT dateOfSprint FROM sprints_table")
    fun getSprintDates():  List<Date>

    @Query("SELECT * FROM sprints_table WHERE distanceOfBuildUp = 0 ORDER BY dateOfSprint DESC")
    fun getAccelerations() : List<Sprint>
    @Query("SELECT * FROM sprints_table WHERE distanceOfBuildUp>0 ORDER BY dateOfSprint DESC")
    fun getTopEnds(): List<Sprint>

    // for PlotViewModel
    @Query("SELECT * FROM sprints_table WHERE distanceOfBuildUp>0 AND weighted=:weighted ORDER BY dateOfSprint ASC")
    fun chooseWeightedOrNotTopEnds(weighted: Boolean):List<Sprint>

    @Query("SELECT * FROM sprints_table WHERE distanceOfBuildUp=0 AND weighted=:weighted ORDER BY dateOfSprint ASC")
    fun chooseWeightedOrNotAccelerations(weighted: Boolean): List<Sprint>

    @Query("SELECT DISTINCT distanceBetweenGates FROM sprints_table WHERE distanceOfBuildUp>0 AND weighted=:weighted")
    fun getDistancesOfTopEnds(weighted: Boolean): List<Int>


    @Query("SELECT DISTINCT distanceBetweenGates FROM sprints_table WHERE distanceOfBuildUp=0 AND weighted=:weighted")
    fun getDistancesOfAccelerations(weighted: Boolean): List<Int>


}