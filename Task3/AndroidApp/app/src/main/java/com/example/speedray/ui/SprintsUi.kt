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
import androidx.compose.runtime.collectAsState
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
import com.example.speedray.data.SprintsListViewModel
import java.util.Date
import kotlin.Unit


fun theDoNothingFunc(a: String?,b: String?){} //this is needed to get the compose preview working
                                                // so just for dev purposes
fun theDoNothingFunc(a: Sprint){}

@Composable
fun SprintsScreen(transitionToSummary: ()-> Unit,
                  navHostController: NavHostController,
                  sprintsListViewModel: SprintsListViewModel) {

    val sprintsToShowcase by sprintsListViewModel.listOfShowcasedSprints.collectAsState()
    val numberOfSprintsToShowcase by sprintsListViewModel.numberOfSprintsToShowcase.collectAsState()
    val typeOfSprintsToShowcase by sprintsListViewModel.typeOfSprintsToShowcase.collectAsState()

    SprintsLayout(
        toSummaryTransition = {navHostController.navigate("Summary")},
        toPlotsTransition = {navHostController.navigate("SprintsPlots")},
        numberOption = numberOfSprintsToShowcase, typeOption = typeOfSprintsToShowcase,
        changeShowcase =  fun(number: String?,type: String?){
            //3 cases are needed :
            // 1. when no parameter is passed (maybe not '_')
            // 2. when only number is passed
            // 3. when only type is passed
            if ((number==null) and (type ==null)){
                sprintsListViewModel.showcaseNSprints()
            }
            else if((number==null)){
                if (type != null) {
                    sprintsListViewModel.showcaseNSprints(typeOfSprints = type)
                }
            }
            else if((type==null)){
                sprintsListViewModel.showcaseNSprints(numberOfSprints = number)
            }

        },
        sprints = sprintsToShowcase,
        deleteSprint = fun(sprint: Sprint){
            sprintsListViewModel.deleteSprint(sprint)
        }
    )

}

@Composable
fun SprintsLayout(
    toSummaryTransition : ()-> Unit={},
    toPlotsTransition : ()->Unit={},
    numberOption: String,typeOption: String,
    changeShowcase:(String?, String?)-> Unit,
    sprints : List<Sprint>,deleteSprint: (Sprint) -> Unit

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
        SprintsFilter(numberOption = numberOption,changeShowcase = changeShowcase, typeOption = typeOption)
        SprintsList(sprints = sprints, deleteSprint = deleteSprint)
    }
}

@Preview(showBackground = true)
@Composable
fun SprintsFilter(numberOption: String="ALL",typeOption: String="ALL", changeShowcase: (String?, String?) -> Unit=::theDoNothingFunc){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 20.dp),
        verticalAlignment = Alignment.CenterVertically) {
        SprintsTypeFilter(typeOption = typeOption,changeShowcase = changeShowcase)
        SprintsNumberMenu(sprintNumberOption = numberOption,changeShowcase = changeShowcase)
    }
}
@Preview(showBackground = true)
@Composable
fun SprintsList(sprints:List<Sprint> =emptyList<Sprint>(),
                deleteSprint:(Sprint)-> Unit=::theDoNothingFunc){
    LazyColumn(
    ) {
        items(sprints.size){idx->
            SprintItem(sprints[idx],deleteSprint)
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
),deleteSprint:(Sprint)-> Unit){
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
                NormalDescription(sprint,deleteSprint)
            }
            SprintItemHeight.EXPANDED -> {
                ExpandedDescription(sprint,deleteSprint)
            }
        }


    }


}
@Composable
fun NormalDescription(sprint: Sprint,
                      deleteSprint:(Sprint)-> Unit){
    val descriptionSize = 12.sp
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
                Text(stringResource(R.string.DateOfSprint), fontSize = descriptionSize)
                Text(
                    DateFormat.getDateTimeInstance(DateFormat.SHORT,DateFormat.SHORT).format(sprint.dateOfSprint).toString(),
                    textAlign = TextAlign.Center,
                    fontSize = descriptionSize)
            }
            Spacer(modifier = Modifier.fillMaxWidth(0.05f))

            Column(Modifier.weight(0.3f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                Text(stringResource(R.string.distanceBetweenGates), fontSize = descriptionSize)
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
        Icon(

            painterResource(R.drawable.ic_delete),
            contentDescription = "Sprint type icon",
            modifier = Modifier.weight(0.15f)
                        .fillMaxHeight(0.4f)
                        .clickable(true, onClick = {deleteSprint(sprint)})
        )
    }

}
@Composable
fun ExpandedDescription(sprint: Sprint,
                        deleteSprint:(Sprint)-> Unit){
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
                .clickable(true, onClick = {
                    deleteSprint(sprint)

                })
        )
    }
}


@Composable
fun SprintsNumberMenu(
    sprintNumberOption: String ="All",
    changeShowcase: (String?, String?) -> Unit
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
                    onClick = { changeShowcase("10",null)
                                expanded=!expanded}
                )
                DropdownMenuItem(
                    text = { Text("20") },
                    onClick = { changeShowcase("20",null)
                                expanded=!expanded}
                )
                DropdownMenuItem(
                    text = { Text("50") },
                    onClick = { changeShowcase("50",null)
                                expanded=!expanded}
                )
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {changeShowcase("ALL",null) }
                )
            }
        }
    }
@Composable
fun SprintsTypeFilter(

    changeShowcase: (String?, String?) -> Unit,
    typeOption: String
){
    var allFilter: Boolean
    var topEndFilter: Boolean
    var accelerationFilter: Boolean
    val labelsSize = 10.sp
    when(typeOption){
        "TopEnd"->{
            allFilter=  true
            topEndFilter = false
            accelerationFilter= true
        }
        "Acceleration"->{
            allFilter=  true
            topEndFilter = true
            accelerationFilter= false
        }
        else -> {
            allFilter=  false
            topEndFilter = true
            accelerationFilter= true
        }

    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface (modifier = Modifier
            .fillMaxWidth(0.1f)
            .clickable(allFilter, onClick = { changeShowcase(null,"ALL") }),
            shape = RoundedCornerShape(7.dp),
            color = if (allFilter) {MaterialTheme.colorScheme.inversePrimary}
                    else{MaterialTheme.colorScheme.secondary}
        )
        {
            Text(
                "All",
                textAlign = TextAlign.Center,
                fontSize = labelsSize

            )
        }
        Spacer(Modifier.fillMaxWidth(0.066f))
        Surface (modifier = Modifier
            .fillMaxWidth(0.2f)
            .clickable(true, onClick = {  changeShowcase(null,"TopEnd")}),
            shape = RoundedCornerShape(7.dp),
            color = if (topEndFilter) {MaterialTheme.colorScheme.inversePrimary}
                    else{MaterialTheme.colorScheme.secondary}
        )
        {
            Text(
                "TopEnd",
                textAlign = TextAlign.Center,
                fontSize = labelsSize

            )
        }
        Spacer(Modifier.fillMaxWidth(0.066f))
        Surface (modifier = Modifier
            .fillMaxWidth(0.4f)
            .clickable(true, onClick = { changeShowcase(null,"Acceleration") }),
            shape = RoundedCornerShape(7.dp),
            color = if (accelerationFilter) {MaterialTheme.colorScheme.inversePrimary}
            else{MaterialTheme.colorScheme.secondary}
        )
        {
            Text(
                "Acceleration",
                textAlign = TextAlign.Center,
                fontSize = labelsSize

            )
        }
        Spacer(Modifier.fillMaxWidth(0.066f))
    }
}