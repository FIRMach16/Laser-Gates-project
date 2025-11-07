package com.example.speedray.ui

import android.icu.text.DateFormat
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.speedray.R
import com.example.speedray.data.Sprint
import java.util.Date
import java.util.logging.Filter

@Composable
fun SprintsScreen(transitionToLiveData : ()-> Unit,
                  navHostController: NavHostController) {

    SprintsLayout(
        toSummaryTransition = {navHostController.navigate("Summary")},
        toPlotsTransition = {navHostController.navigate("SprintsPlots")}
    )

}
@Preview(showBackground = true)
@Composable
fun SprintsLayout(
    toSummaryTransition : ()-> Unit={},
    toPlotsTransition : ()->Unit={}
){
    // should show a filtering bar with types of sprints and number of sprints to showcase
    // then a lazy column of sprints
    Column( verticalArrangement = Arrangement.Top) {
        NavigationBar(
            isSprintsClickable = false,
            isSummaryClickable = true,
            isPlotsClickable = true,
            toPlotsTransition = {},
            toSprintsTransition = toPlotsTransition,
            toSummaryTransition = toSummaryTransition
        )
        SprintsFilter()
        SprintsList()
    }
}
@Preview(showBackground = true)
@Composable
fun SprintsFilter(){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        SprintsTypeFilter()
        SprintsNumberMenu()
    }
}
@Preview(showBackground = true)
@Composable
fun SprintsList(){
    LazyColumn(
    ) {
        item {
        SprintItem()
        }
        item {
            SprintItem()
        }
        item {
            SprintItem()
        }
        item {
            SprintItem()
        }
        item {
            SprintItem()
        }


    }
}

enum class SprintItemHeight{
    NORMAL,EXPANDED
}

@Composable
fun SprintItem(sprint: Sprint = Sprint(
    time = 0.0f, entrySpeed = 35.0f, exitSpeed = 35.0f,
    distanceBetweenGates = 0, distanceOfBuildUp = 0,
    dateOfSprint = Date(), weight = 0, weighted = false
)){
    var itemState by remember { mutableStateOf(SprintItemHeight.NORMAL) }
    val transition = updateTransition(targetState = itemState)

    val boxHeight by transition.animateDp() {state ->
        when (state) {
            SprintItemHeight.EXPANDED -> 300.dp
            SprintItemHeight.NORMAL -> 100.dp
        }
    }

    Card(
        modifier = Modifier.clickable(true, onClick = {
            itemState = when(itemState){
                SprintItemHeight.NORMAL -> SprintItemHeight.EXPANDED
                SprintItemHeight.EXPANDED -> SprintItemHeight.NORMAL
            }
        })  .height(boxHeight)
            .fillMaxWidth()
            .padding(5.dp)

    ){

        when(itemState){
            SprintItemHeight.NORMAL -> {
                NormalDescription(sprint)
            }
            SprintItemHeight.EXPANDED -> {
                ExpandedDescription(sprint)
            }
        }


    }


}
@Composable
fun NormalDescription(sprint: Sprint){

    Row(Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(

            painterResource(when(sprint.distanceOfBuildUp){
                0 -> R.drawable.ic_acceleration
                else -> R.drawable.ic_top_end
            }),
            contentDescription = "Sprint type icon",
            modifier = Modifier.weight(0.15f).fillMaxHeight(0.4f)
        )
        Row(
            modifier = Modifier.weight(0.7f).fillMaxHeight(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Column(Modifier.weight(0.3f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.DateOfSprint))
                Text(
                    DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(sprint.dateOfSprint).toString(),
                    textAlign = TextAlign.Center)
            }
            Spacer(modifier = Modifier.fillMaxWidth(0.05f))

            Column(Modifier.weight(0.3f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.distanceBetweenGates))
                Text(sprint.distanceBetweenGates.toString()+" m")
            }

            Spacer(modifier = Modifier.fillMaxWidth(0.05f))

            Column(Modifier.weight(0.3f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.time))
                Text(sprint.time.toString()+" s")
            }

        }
        Icon(

            painterResource(R.drawable.ic_delete),
            contentDescription = "Sprint type icon",
            modifier = Modifier.weight(0.15f)
                        .fillMaxHeight(0.4f)
                        .clickable(true, onClick = {/* Function that delete entry takes sprint as argument will
                                                                need to see if i have to include the reload of the screen
                                                                 or will db as liveData will suffice */})
        )
    }

}
@Composable
fun ExpandedDescription(sprint: Sprint){
    val descriptionSize = 11.sp
    Row(modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically) {
        Icon(

            painterResource(when(sprint.distanceOfBuildUp){
                0 -> R.drawable.ic_acceleration
                else -> R.drawable.ic_top_end
            }),
            contentDescription = "Sprint type icon",
            modifier = Modifier.weight(0.15f).fillMaxHeight(0.4f)
        )

        Column(modifier = Modifier.weight(0.7f).fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Row(Modifier.fillMaxHeight(0.33f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                Column(Modifier.weight(0.3f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Text(stringResource(R.string.DateOfSprint), fontSize = descriptionSize)
                    Text(
                        DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(sprint.dateOfSprint).toString(),
                        textAlign = TextAlign.Center, fontSize = descriptionSize)
                }
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))

                Column(Modifier.weight(0.3f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Text(stringResource(R.string.distanceBetweenGates),fontSize = descriptionSize)
                    Text(sprint.distanceBetweenGates.toString()+" m", fontSize = descriptionSize)
                }

                Spacer(modifier = Modifier.fillMaxWidth(0.05f))

                Column(Modifier.weight(0.3f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {
                    Text(stringResource(R.string.time), fontSize = descriptionSize)
                    Text(sprint.time.toString()+" s", fontSize = descriptionSize)
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))


            Row(Modifier.fillMaxHeight(0.33f).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                Column(Modifier.weight(0.3f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {

                    Text(stringResource(R.string.entrySpeed), fontSize = descriptionSize)
                    Text(sprint.entrySpeed.toString()+" KM/H", fontSize = descriptionSize)

                }
                Spacer(modifier = Modifier.fillMaxWidth(0.05f))

                Column(Modifier.weight(0.3f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {

                    Text(stringResource(R.string.exitSpeed), fontSize = descriptionSize)
                    Text(sprint.exitSpeed.toString()+" KM/H", fontSize = descriptionSize)

                }

                Spacer(modifier = Modifier.fillMaxWidth(0.05f))

                Column(Modifier.weight(0.3f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center) {

                    Text(stringResource(R.string.buildUpDistance),
                        textAlign = TextAlign.Center, fontSize = descriptionSize)
                    Text(sprint.distanceOfBuildUp.toString()+" m", fontSize = descriptionSize)

                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.1f))
            Row(Modifier.fillMaxHeight(0.33f), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

                Column(Modifier.weight(0.5f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Weighted?", fontSize = descriptionSize)
                    Text(when(sprint.weighted){
                        true -> "Yes"
                        false -> "No"
                    }, fontSize = descriptionSize)
                }
                Spacer(modifier = Modifier.fillMaxWidth(0.03f))
                Column(Modifier.weight(0.5f).fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.weightAmount), fontSize = descriptionSize)
                    Text(sprint.weight.toString()+" Kg", fontSize = descriptionSize)
                }
            }
        }
        Icon(

            painterResource(R.drawable.ic_delete),
            contentDescription = "Sprint type icon",
            modifier = Modifier.weight(0.15f)
                .fillMaxHeight(0.4f)
                .clickable(true, onClick = {/* Function that delete entry takes sprint as argument will
                                                                need to see if i have to include the reload of the screen
                                                                 or will db as liveData will suffice */})
        )
    }
}


@Composable
fun SprintsNumberMenu(
    sprintNumberOption: String ="All"
){

        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(1.0f)
        ) {
            IconButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(sprintNumberOption)
                    Spacer(modifier = Modifier.width(10.dp))
                    Icon(
                        painterResource(R.drawable.ic_bottom_arrow),
                        contentDescription = "More options"
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("10") },
                    onClick = { /* Do something... */ }
                )
                DropdownMenuItem(
                    text = { Text("20") },
                    onClick = { /* Do something... */ }
                )
                DropdownMenuItem(
                    text = { Text("50") },
                    onClick = { /* Do something... */ }
                )
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = { /* Do something... */ }
                )
            }
        }
    }
@Composable
fun SprintsTypeFilter(
    allFilter: Boolean =  false,
    topEndFilter:  Boolean = true,
    accelerationFilter: Boolean = true
){
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface (modifier = Modifier
            .fillMaxWidth(0.1f)
            .clickable(allFilter, onClick = {  }),
            shape = RoundedCornerShape(7.dp),
            color = if (allFilter) {MaterialTheme.colorScheme.primary}
                    else{MaterialTheme.colorScheme.secondary}
        )
        {
            Text(
                "All",
                textAlign = TextAlign.Center

            )
        }
        Spacer(Modifier.fillMaxWidth(0.066f))
        Surface (modifier = Modifier
            .fillMaxWidth(0.2f)
            .clickable(true, onClick = {  }),
            shape = RoundedCornerShape(7.dp),
            color = if (topEndFilter) {MaterialTheme.colorScheme.inversePrimary}
                    else{MaterialTheme.colorScheme.secondary}
        )
        {
            Text(
                "TopEnd",
                textAlign = TextAlign.Center

            )
        }
        Spacer(Modifier.fillMaxWidth(0.066f))
        Surface (modifier = Modifier
            .fillMaxWidth(0.4f)
            .clickable(true, onClick = {  }),
            shape = RoundedCornerShape(7.dp),
            color = if (accelerationFilter) {MaterialTheme.colorScheme.inversePrimary}
            else{MaterialTheme.colorScheme.secondary}
        )
        {
            Text(
                "Acceleration",
                textAlign = TextAlign.Center

            )
        }
        Spacer(Modifier.fillMaxWidth(0.066f))
    }
}