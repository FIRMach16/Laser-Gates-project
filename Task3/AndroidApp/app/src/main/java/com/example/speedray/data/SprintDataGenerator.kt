package com.example.speedray.data

import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId
import java.util.Date
import kotlin.random.Random

class SprintDataGenerator {

    companion object{
        fun generateRandomSprints(nbOfSprints: Int,acceleration: Boolean = false): List<Sprint>{
            val listOfSprints = mutableListOf<Sprint>()
            // can be val because listOfSprints points to a mutable object
            // however i can not reassign another list to listOfSprints
            var distanceOfBuildUp:Int
            var timeModifier: Float //acceleration is always slower than top end speed
            // so i'll add 0.8s to the range of top end time
            if(acceleration){
                distanceOfBuildUp = 0
                timeModifier = 0.8f
            }
            else{
                distanceOfBuildUp = 30
                timeModifier = 0.0f
            }

            for(i in 0..<nbOfSprints){
                val date = generateRandomDate(Pair(2024,2026),
                    Pair(15,20))
                val distance = Random.nextInt(1,3)*10 // 10m , 20m or 30m
                val timeInSeconds =
                    ((distance/10.0f - (0.2 - Random.nextInt(0,40)*0.01f))+timeModifier).toFloat() // 10m => time:[0.8s,1.2s]
                val sprint = Sprint(time = timeInSeconds,
                    dateOfSprint = date, distanceBetweenGates = distance,
                    distanceOfBuildUp = distanceOfBuildUp,
                    exitSpeed = 35.0f, entrySpeed = 35.0f, // in testing cases these don't matter much
                    weighted = false, weight =0)
                listOfSprints.add(sprint)

            }

            return listOfSprints
        }

        fun generateRandomDate(
            yearLimits: Pair<Int,Int>,
            hourLimits: Pair<Int,Int>
        ): Date{

            val year = Random.nextInt(yearLimits.component1(),yearLimits.component2())
            val monthNb = Random.nextInt(1,12)
            val month = Month.of(monthNb)
            val dayLimit = month.length((year%4)==0)
            val day = Random.nextInt(1,dayLimit)


            val date = Date.from(LocalDateTime.of(
                year,
                monthNb,
                day,
                Random.nextInt(hourLimits.component1(),hourLimits.component2()),
                Random.nextInt(0,59),
                Random.nextInt(0,59)).atZone(ZoneId.systemDefault()).toInstant())
            return date
        }
    }
}