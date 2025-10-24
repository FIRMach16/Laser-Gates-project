package com.example.speedray.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SprintDatabaseTest: TestCase() {



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
        val listOfSprints = SprintDataGenerator.generateRandomSprints(nbOfSprints)

        for (i in 0..<nbOfSprints){
            dao.addSprint(listOfSprints[i])
        }
        val sprints = dao.readAllData().value

       assertThat(sprints?.contains(listOfSprints[0]) == true && sprints.size == nbOfSprints)
    }
    @Test
    fun deleteSprintCheck():Unit = runBlocking{
        val nbOfSprints = 5
        val listOfSprints = SprintDataGenerator.generateRandomSprints(nbOfSprints)

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
        val listOfSprints = SprintDataGenerator.generateRandomSprints(nbOfSprints)

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
        val listOfSprints = SprintDataGenerator.generateRandomSprints(nbOfSprints)

        for (i in 0..<nbOfSprints){
            dao.addSprint(listOfSprints[i])
        }
        val latestToEnd = dao.getLatestTopEnd()
        val dates = dao.getSprintDates()

        assertThat(dates.max()==latestToEnd.dateOfSprint)
    }

    @Test
    fun checkIfBestAvgSpeed():Unit = runBlocking{

        val nbOfSprints = 10
        val listOfSprints = SprintDataGenerator.generateRandomSprints(nbOfSprints)

        for (i in 0..<nbOfSprints){
            dao.addSprint(listOfSprints[i])
        }
        val sprints = dao.readAllData().value
        //calculate max avg speed from times and distances
        val maxTopSpeedFromCalculation = calculateMaxAvgSpeed(sprints)
        val bestTopSpeedSprint = dao.getBestTopEnd()
        val maxTopEndFromQuery = (bestTopSpeedSprint.distanceBetweenGates/bestTopSpeedSprint.time)
        assertThat(maxTopSpeedFromCalculation==maxTopEndFromQuery)


    }

    fun calculateMaxAvgSpeed(sprints: List<Sprint>?) : Float{
        val listOfAvgSpeeds = mutableListOf<Float>()
        if (sprints != null) {
            for(sprint in sprints){

                listOfAvgSpeeds.add(sprint.distanceBetweenGates/sprint.time)
            }
            return listOfAvgSpeeds.max()
        }

        return 0.0f

    }

    @Test
    fun checkIfBestAcceleration(): Unit = runBlocking{
        val nbOfAccelerations = 10
        val listOfAccelerations = SprintDataGenerator.generateRandomSprints(nbOfAccelerations,true)
        for (i in 0..<nbOfAccelerations){
            dao.addSprint(listOfAccelerations[i])
        }
        val nbOfTopEnds = 10
        val listOfTopEnd = SprintDataGenerator.generateRandomSprints(nbOfTopEnds)
        for (i in 0..<nbOfTopEnds){
            dao.addSprint(listOfTopEnd[i])
        }
        val sprints = dao.readAllData().value
        val accelerations = dao.getAccelerations()
        //calculate max avg speed from times and distances
        val maxAccelerationFromCalculation = calculateMaxAvgSpeed(accelerations)
        val bestAccelerationSprint = dao.getBestAcc()
        val maxAccelerationFromQuery = (bestAccelerationSprint.distanceBetweenGates/bestAccelerationSprint.time)
        assertThat((maxAccelerationFromCalculation==maxAccelerationFromQuery) &&(sprints?.size == nbOfAccelerations+nbOfTopEnds))






    }


}