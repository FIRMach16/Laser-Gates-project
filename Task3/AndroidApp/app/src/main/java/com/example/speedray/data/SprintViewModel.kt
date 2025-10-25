package com.example.speedray.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SprintViewModel(application: Application): AndroidViewModel(application) {
    private val readAllData : LiveData<List<Sprint>>
    private val repository: SprintRepository

    // initializer block runs code during object initialization
    init{
        val sprintDao = SprintDatabase.getDatabase(application).sprintDao()
        repository = SprintRepository(sprintDao)
        readAllData = repository.readAllData
    }
    fun addSprint(sprint: Sprint){
        viewModelScope.launch(Dispatchers.IO){
            repository.addSprint(sprint)
        }
    }
}