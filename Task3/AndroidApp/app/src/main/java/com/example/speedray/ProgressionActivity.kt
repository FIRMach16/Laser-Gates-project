package com.example.speedray


import android.content.Intent

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.example.speedray.data.ProgressionViewModel
import com.example.speedray.data.SprintDataGenerator
import com.example.speedray.data.SprintDatabase
import com.example.speedray.ui.MyProgressionNavHost
import com.example.speedray.ui.ProgressionActivityLayout
import com.example.speedray.ui.ProgressionScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



class ProgressionActivity : ComponentActivity() {
    private lateinit var progressionViewModel : ProgressionViewModel
    private lateinit var sprintDatabase: SprintDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sprintDatabase = SprintDatabase.getDatabase(this)
        lifecycleScope.launch (Dispatchers.IO){
               progressionViewModel = ProgressionViewModel(sprintDatabase)
        }

        setContent {
//            ProgressionScreen({switchToLiveData()},progressionViewModel)
            MyProgressionNavHost({switchToLiveData()},progressionViewModel)
        }





    }
    fun switchToLiveData(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }
}


