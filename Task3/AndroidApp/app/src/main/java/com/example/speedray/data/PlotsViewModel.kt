package com.example.speedray.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.speedray.ui.GlobalPlotChoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlotsViewModel(sprintDatabase: SprintDatabase): ViewModel() {
    private var _listOfTimes = MutableStateFlow(emptyList<Float>())
    private var _listOfAverageSpeed = MutableStateFlow(emptyList<Float>())

    private var listOfPlottedData = emptyList<Sprint>()

    private var _listOfShowcasedDistances = MutableStateFlow(emptyList<Int>())

    var listOfTimes = _listOfTimes.asStateFlow()
    var listOfAverageSpeed = _listOfAverageSpeed.asStateFlow()
    var listOfShowcasedDistances = _listOfShowcasedDistances.asStateFlow()

    private val repository: SprintRepository
    init {
        val sprintDao = sprintDatabase.sprintDao()
        repository = SprintRepository(sprintDao)
    }
    fun onPlotsLoaded(plotChoice: GlobalPlotChoice,weighted: Boolean){
        viewModelScope.launch(Dispatchers.IO) {
            loadDistances(plotChoice,weighted)
            when(plotChoice){
                GlobalPlotChoice.TOP_END -> {
                    val listOfSprints = repository.chooseWeightedOrNotTopEnds(weighted)
                    val times = mutableListOf<Float>()
                    val avgSpeeds = mutableListOf<Float>()
                    listOfSprints.forEach { sprint ->

                        times.add(sprint.time)
                        avgSpeeds.add((sprint.distanceBetweenGates/sprint.time)*3.6f)
                    }
                    _listOfAverageSpeed.value = avgSpeeds
                    _listOfTimes.value=times
                    listOfPlottedData = listOfSprints
                }
                GlobalPlotChoice.ACCELERATION -> {
                    val listOfSprints = repository.chooseWeightedOrNotAccelerations(weighted)
                    val times = mutableListOf<Float>()
                    val avgSpeeds = mutableListOf<Float>()
                    listOfSprints.forEach { sprint ->
                        times.add(sprint.time)
                        avgSpeeds.add(sprint.distanceBetweenGates/sprint.time*3.6f)
                    }
                    _listOfAverageSpeed.value = avgSpeeds
                    _listOfTimes.value=times
                    listOfPlottedData = listOfSprints
                }
            }



        }
    }
    fun changeShowcasedData(distance:Int){
        val times = mutableListOf<Float>()
        listOfPlottedData.forEach { sprint ->
            if (sprint.distanceBetweenGates==distance){
                times.add(sprint.time)
            }
        }
        _listOfTimes.value = times
    }

    fun loadDistances(plotChoice: GlobalPlotChoice,weighted: Boolean){
        viewModelScope.launch(Dispatchers.IO){

            when(plotChoice){

                GlobalPlotChoice.ACCELERATION -> {

                    _listOfShowcasedDistances.value = repository.getDistancesOfAccelerations(weighted)
                }
                GlobalPlotChoice.TOP_END -> {
                    _listOfShowcasedDistances.value = repository.getDistancesOfTopEnds(weighted)
                }

            }
        }


    }



}