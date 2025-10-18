package com.example.speedray.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.Date

data class SprintPerfInfo(val description: String,
                          val avgSpeed: Float,
                          val dayOfPerf: Date,
                          val distance: Int,
                          val time: Float)


class ProgressionViewModel(application: Application): AndroidViewModel(application){

    var latestPerf = MutableLiveData<SprintPerfInfo>()
    var bestPerf = MutableLiveData<SprintPerfInfo>()
    private val repository: SprintRepository
    init {
        val sprintDao = SprintDatabase.getDatabase(application).sprintDao()
        repository = SprintRepository(sprintDao)
    }
    private val bestTopEnd by lazy {
        repository.getBestTopEnd
    }

    private val latestTopEnd by lazy{
        repository.getLatestTopEnd
    }

    private val bestAcceleration by lazy{
        repository.getBestAcceleration
    }

    private val latestAcceleration by lazy{
        repository.getLatestAcceleration
    }


    fun onTopEndLoaded(){


        val latestTopEndPerf = SprintPerfInfo(
            description = "Latest",
            latestTopEnd.distanceBetweenGates/latestTopEnd.time,
            latestTopEnd.dateOfSprint,
            latestTopEnd.distanceBetweenGates,
            latestTopEnd.time
        )

        val bestTopEndPerf = SprintPerfInfo(
            description = "Best",
            bestTopEnd.distanceBetweenGates/bestTopEnd.time,
            bestTopEnd.dateOfSprint,
            bestTopEnd.distanceBetweenGates,
            bestTopEnd.time
        )

        latestPerf.value = latestTopEndPerf
        bestPerf.value = bestTopEndPerf
    }
    fun onAccelerationLoaded(){

        val latestAccelerationPerf = SprintPerfInfo(
            description = "Latest",
            latestAcceleration.distanceBetweenGates/latestAcceleration.time,
            latestAcceleration.dateOfSprint,
            latestAcceleration.distanceBetweenGates,
            latestAcceleration.time
        )

        val bestAccelerationPerf = SprintPerfInfo(
            description = "Best",
            bestAcceleration.distanceBetweenGates/bestAcceleration.time,
            bestAcceleration.dateOfSprint,
            bestAcceleration.distanceBetweenGates,
            bestAcceleration.time
        )

        latestPerf.value = latestAccelerationPerf
        bestPerf.value = bestAccelerationPerf
    }

}