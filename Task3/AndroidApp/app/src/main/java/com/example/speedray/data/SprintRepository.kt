package com.example.speedray.data

import androidx.lifecycle.LiveData


// source : https://www.youtube.com/watch?v=lwAvI3WDXBY&list=PLSrm9z4zp4mEPOfZNV9O-crOhoMa0G2-o&index=3
// This is useful for code separation meaning if one day i decide to change Room Lib to another solution for data persistence
//  i can change The other files without needing to change This one.

class SprintRepository(private  val sprintDao : SprintDao){

    val readAllData : LiveData<List<Sprint>> = sprintDao.readAllData()

    suspend fun addSprint(sprint: Sprint){
        sprintDao.addSprint(sprint)
    }

}