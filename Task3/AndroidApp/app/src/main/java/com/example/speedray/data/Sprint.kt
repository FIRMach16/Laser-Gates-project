package com.example.speedray.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Sprints_Table")
data class Sprint(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,// if the table is empty the id will be 0.
    val time: Float,
    val entrySpeed : Float,
    val exitSpeed : Float,
    val dateOfSprint : Date,
    val distanceBetweenGates: Int,
    val distanceOfBuildUp: Int,
    val weighted: Boolean,
    val weight: Int
)
