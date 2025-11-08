package com.example.speedray.data


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.speedray.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Collections.emptyList


class SprintsListViewModel(sprintDatabase: SprintDatabase): ViewModel() {

    private var _listOfShowcasedSprints= MutableStateFlow(emptyList<Sprint>())
    private var _numberOfSprintsToShowcase = MutableStateFlow("ALL")

    private var _typeOfSprintsToShowcase = MutableStateFlow("ALL")

    var typeOfSprintsToShowcase = _typeOfSprintsToShowcase.asStateFlow()
    var listOfShowcasedSprints = _listOfShowcasedSprints.asStateFlow()
    var numberOfSprintsToShowcase = _numberOfSprintsToShowcase.asStateFlow()
    private val repository: SprintRepository

    init {
        val sprintDao = sprintDatabase.sprintDao()
        repository = SprintRepository(sprintDao)

        if (!repository.getAllSprints().isEmpty()){
            _listOfShowcasedSprints.value = repository.getAllSprints()
        }
    }

    fun showcaseNSprints(
        numberOfSprints: String = numberOfSprintsToShowcase.value,
        typeOfSprints: String = typeOfSprintsToShowcase.value
    )
    {
                // default is the previous state unless the user changes it by
                // sending an argument (selecting an option)

                // update
                _numberOfSprintsToShowcase.value = numberOfSprints
                _typeOfSprintsToShowcase.value =typeOfSprints

                Log.d(TAG,"Number : $numberOfSprints , Type : $typeOfSprints ")
                viewModelScope.launch(Dispatchers.IO) {
                when(typeOfSprints) {
                    "ALL" -> {
                        when (numberOfSprints) {
                            "ALL" -> {
                                _listOfShowcasedSprints.value = repository.getAllSprints()
                            }

                            else -> {
                                _listOfShowcasedSprints.value = repository.getNSprints(
                                    numberOfSprints =
                                        numberOfSprints.toInt()
                                )
                            }
                        }
                    }

                    "TopEnd" -> {
                        when (numberOfSprints) {
                            "ALL" -> {
                                _listOfShowcasedSprints.value = repository.getTopEnds()
                            }

                            else -> {
                                _listOfShowcasedSprints.value = repository.getNTopEnds(
                                    numberOfSprints =
                                        numberOfSprints.toInt()
                                )
                            }
                        }
                    }

                    "Acceleration" -> {
                        when (numberOfSprints) {
                            "ALL" -> {
                                _listOfShowcasedSprints.value = repository.getAccelerations()
                            }

                            else -> {
                                _listOfShowcasedSprints.value =
                                    repository.getNAccelerations(numberOfSprints = numberOfSprints.toInt())
                            }
                        }

                    }

                    else -> {
                        Log.d(TAG, "What the hell!!!")
                    }
                }
                }
    }
    fun deleteSprint(sprint: Sprint){
        // this will delete from the DB and read it after deleting it
        // so we will need an I/O coroutine

        viewModelScope.launch(Dispatchers.IO){
            repository.deleteSprint(sprint)
            showcaseNSprints()
        }
    }



}