package com.example.speedray.data

import androidx.lifecycle.LiveData


// source : https://www.youtube.com/watch?v=lwAvI3WDXBY&list=PLSrm9z4zp4mEPOfZNV9O-crOhoMa0G2-o&index=3
// This is useful for code separation meaning if one day i decide to change Room Lib to another solution for data persistence
//  i can change The other files without needing to change This one.

class SprintRepository(private  val sprintDao : SprintDao){

    val readAllData : LiveData<List<Sprint>> = sprintDao.readAllData()
    fun getAllSprints() : List<Sprint> {
        return sprintDao.getAllSprints()
    }

    fun getNSprints(numberOfSprints : Int) : List<Sprint> {
       return sprintDao.showLimitedMixedData(numberOfSprints)
    }

    fun getNAccelerations(numberOfSprints : Int) : List<Sprint> {
        return sprintDao.showLimitedAccelerationData(numberOfSprints)
    }

    fun getNTopEnds(numberOfSprints : Int) : List<Sprint> {
        return sprintDao.showLimitedTopEndData(numberOfSprints)
    }

    fun getAccelerations(): List<Sprint>{
        return sprintDao.getAccelerations()
    }


    fun getTopEnds():List<Sprint>{
        return sprintDao.getTopEnds()
    }


    fun getBestTopEnd():Sprint {
        return sprintDao.getBestTopEnd()
    }


    fun getLatestTopEnd():Sprint {
        return sprintDao.getLatestTopEnd()
    }


    fun getBestAcceleration(): Sprint{
        return sprintDao.getBestAcc()
    }

    fun getLatestAcceleration(): Sprint{
        return sprintDao.getLatestAcc()
    }

    suspend fun addSprint(sprint: Sprint){
        sprintDao.addSprint(sprint)
    }

    suspend fun deleteSprint(sprint:Sprint){
        sprintDao.deleteSprint(sprint)
    }

    suspend fun clearAllSprints(){
        sprintDao.clearSprints()
    }


    fun chooseWeightedOrNotAccelerations(weighted: Boolean): List<Sprint>{
        return sprintDao.chooseWeightedOrNotAccelerations(weighted)
    }
    fun chooseWeightedOrNotTopEnds(weighted: Boolean): List<Sprint>{
       return sprintDao.chooseWeightedOrNotTopEnds(weighted)
    }

    fun getDistancesOfTopEnds(weighted: Boolean): List<Int>{
        return sprintDao.getDistancesOfTopEnds(weighted)
    }

    fun getDistancesOfAccelerations(weighted: Boolean): List<Int>{
        return sprintDao.getDistancesOfAccelerations(weighted)
    }






}