package com.example.speedray


import android.content.Intent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import com.example.speedray.ui.ProgressionActivityLayout
import java.util.Date


class ProgressionActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_progression)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
        setContent {
            ProgressionActivityLayout({switchToLiveData()})
        }
    }
    fun switchToLiveData(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

    }
}
data class SprintPerfInfo(val description: String,
                          val avgSpeed: Float,
                          val dayOfPerf: Date,
                          val distance: Int,
                          val time: Float)


