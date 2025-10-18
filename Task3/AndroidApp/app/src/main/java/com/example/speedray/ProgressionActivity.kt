package com.example.speedray


import android.content.Intent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.speedray.data.ProgressionViewModel

import com.example.speedray.ui.ProgressionActivityLayout
import java.util.Date


class ProgressionActivity : ComponentActivity() {
    val progressionViewModel = ProgressionViewModel(this.application)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressionViewModel.onTopEndLoaded()
        setContent {
            ProgressionActivityLayout({switchToLiveData()},progressionViewModel)
        }
    }
    fun switchToLiveData(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }
}


