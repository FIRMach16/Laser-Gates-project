package com.example.speedray.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import java.time.LocalDateTime
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith
import java.time.Month
import java.time.ZoneId
import java.util.Date
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class SprintDatabaseTest: TestCase() {

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

    private lateinit var db: SprintDatabase
    private lateinit var dao : SprintDao



    @Before
    public override fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context,
            SprintDatabase::class.java).build()

        dao = db.sprintDao()

    }

    @After
    fun closeDb(){
        db.close()
    }

    @Test
    fun addAndCheckSprint(): Unit= runBlocking{
        val nbOfSprints = 5
        val listOfSprints = generateRandomSprints(nbOfSprints)

        for (i in 0..<nbOfSprints){
            dao.addSprint(listOfSprints[i])
        }
        val sprints = dao.readAllData().value

       assertThat(sprints?.contains(listOfSprints[0]) == true && sprints.size == nbOfSprints)
    }
    @Test
    fun deleteSprintCheck():Unit = runBlocking{
        val nbOfSprints = 5
        val listOfSprints = generateRandomSprints(nbOfSprints)

        for (i in 0..<nbOfSprints){
            dao.addSprint(listOfSprints[i])
        }

        dao.deleteSprint(listOfSprints[0])

        val sprints = dao.readAllData().value
        assertThat(sprints?.size==nbOfSprints-1 && sprints?.contains(listOfSprints[0]) == false)
    }
    @Test
    fun checkIfDeleteAll(): Unit =runBlocking {
        val nbOfSprints = 5
        val listOfSprints = generateRandomSprints(nbOfSprints)

        for (i in 0..<nbOfSprints){
            dao.addSprint(listOfSprints[i])
        }

        dao.clearSprints()
        val sprints = dao.readAllData().value
        assertThat(sprints?.isEmpty())

    }

    @Test
    fun checkIfLatestTopEnd():Unit = runBlocking {
        val nbOfSprints = 5
        val listOfSprints = generateRandomSprints(nbOfSprints)

        for (i in 0..<nbOfSprints){
            dao.addSprint(listOfSprints[i])
        }
        val latestToEnd = dao.getLatestTopEnd()
        val dates = dao.getSprintDates()

        assertThat(dates.max()==latestToEnd.dateOfSprint)
    }


}