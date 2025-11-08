package com.example.speedray.data


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedray.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Date

data class SprintPerfInfo(val id: Int? =null,
                          val description: String? =null,
                          val avgSpeed: Float? =null,
                          val dayOfPerf: Date?=null,
                          val distance: Int?=null,
                           val time: Float?=null)

// as i will be using StateFlow and it needs a non null initial value,I will
// assign to it a SprintPerfInfo with null parameters ,I could add a loading
// icon as long as dayOfPerf is null, in case of the DB interaction is slow (Spoiler: it's not).


class ProgressionViewModel(sprintDatabase: SprintDatabase): ViewModel(){

    private var _latestPerf = MutableStateFlow(SprintPerfInfo())
    private var _bestPerf = MutableStateFlow(SprintPerfInfo())

    private var _topEndClickable = MutableStateFlow(false)
    private var _accelerationClickable = MutableStateFlow(true)

    var latestPerf = _latestPerf.asStateFlow()
    var bestPerf = _bestPerf.asStateFlow()

    var topEndClickable = _topEndClickable.asStateFlow()
    var accelerationClickable = _accelerationClickable.asStateFlow()

    // the practice above is used for protection as state flow is an Immutable object (read-only)

    private val repository: SprintRepository
    lateinit var bestTopEnd : Sprint

    lateinit var latestTopEnd: Sprint

    lateinit var bestAcceleration : Sprint
    lateinit var latestAcceleration : Sprint
    init {

        val sprintDao = sprintDatabase.sprintDao()
        repository = SprintRepository(sprintDao)
        // for testing ui

        if(repository.getAllSprints().isEmpty()) {
            val sprints = SprintDataGenerator.generateRandomSprints(5)
            val accelerations = SprintDataGenerator.generateRandomSprints(5,true)
           viewModelScope.launch(Dispatchers.IO) {

                for (i in 0..<5) {
                    // just for testing purposes the real data will be added by the FAB in the mainActivity
                    repository.addSprint(sprints[i])
                    repository.addSprint(accelerations[i])
                }


                onTopEndLoaded()
            }
        }
        else{
           onTopEndLoaded()
        }
    }





    fun onTopEndLoaded() {


        viewModelScope.launch(Dispatchers.IO) {
            try {
                latestTopEnd = repository.getLatestTopEnd()
                bestTopEnd = repository.getBestTopEnd()
                val latestTopEndPerf = SprintPerfInfo(
                    id = latestTopEnd.id,
                    description = "Latest",
                    (latestTopEnd.distanceBetweenGates / latestTopEnd.time) * 3.6f,
                    latestTopEnd.dateOfSprint,
                    latestTopEnd.distanceBetweenGates,
                    latestTopEnd.time
                )

                val bestTopEndPerf = SprintPerfInfo(
                    id = bestTopEnd.id,
                    description = "Best",
                    (bestTopEnd.distanceBetweenGates / bestTopEnd.time) * 3.6f,
                    bestTopEnd.dateOfSprint,
                    bestTopEnd.distanceBetweenGates,
                    bestTopEnd.time
                )

                _latestPerf.value = latestTopEndPerf
                _bestPerf.value = bestTopEndPerf
                _topEndClickable.value = false
                _accelerationClickable.value = true
            }
            catch (t: Throwable){

                    Log.d(TAG,"Caught throwable during onTopEndLoaded : $t")
                    _latestPerf.value = SprintPerfInfo()
                    _bestPerf.value = SprintPerfInfo()
                    _topEndClickable.value = false
                    _accelerationClickable.value = true

            }
        }
    }
    fun onAccelerationLoaded(){


        viewModelScope.launch(Dispatchers.IO){
            try {


                latestAcceleration = repository.getLatestAcceleration()
                bestAcceleration = repository.getBestAcceleration()

                val latestAccelerationPerf = SprintPerfInfo(
                    id = latestAcceleration.id,
                    description = "Latest",
                    (latestAcceleration.distanceBetweenGates / latestAcceleration.time) * 3.6f,
                    latestAcceleration.dateOfSprint,
                    latestAcceleration.distanceBetweenGates,
                    latestAcceleration.time
                )

                val bestAccelerationPerf = SprintPerfInfo(
                    id = bestAcceleration.id,
                    description = "Best",
                    (bestAcceleration.distanceBetweenGates / bestAcceleration.time) * 3.6f,
                    bestAcceleration.dateOfSprint,
                    bestAcceleration.distanceBetweenGates,
                    bestAcceleration.time
                )


                _latestPerf.value = latestAccelerationPerf
                _bestPerf.value = bestAccelerationPerf
                _topEndClickable.value = true
                _accelerationClickable.value = false
            }
            catch (t: Throwable){
                Log.d(TAG,"Caught throwable during onAccelerationLoaded: $t")
                _latestPerf.value = SprintPerfInfo()
                _bestPerf.value = SprintPerfInfo()
                _topEndClickable.value = true
                _accelerationClickable.value = false

            }
        }
    }

}