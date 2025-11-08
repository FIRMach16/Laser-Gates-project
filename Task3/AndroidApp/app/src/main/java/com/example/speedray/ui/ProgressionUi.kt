package com.example.speedray.ui

import android.icu.text.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.speedray.R
import com.example.speedray.data.ProgressionViewModel
import com.example.speedray.data.SprintPerfInfo


@Composable
fun NavButtonContent(
    scale: ContentScale,
    image: Painter,
    text: String,
    transition: () -> Unit,
    clickable: Boolean
) {

    Card {
        Column(
            modifier = Modifier
                .size(120.dp)
                .clickable(enabled = clickable, onClick = transition),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Image(
                modifier = Modifier.fillMaxSize(0.8f),
                contentScale = scale,
                painter = image,
                contentDescription = text
            )
            Text(text = text)

        }
    }
}

@Composable
fun NavigationButtons(
    transition: () -> Unit,
    liveDataActive: Boolean,
    progressionActive: Boolean
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        NavButtonContent(
            ContentScale.FillBounds,
            painterResource(R.drawable.ic_timer),
            "Live Data",
            transition, liveDataActive
        )
        Spacer(modifier = Modifier.width(50.dp))
        NavButtonContent(
            ContentScale.FillBounds,
            painterResource(R.drawable.ic_progression),
            "Progression", transition, progressionActive
        )

    }

}
@Composable
fun ProgressionScreen(
    transitionToLiveData: () -> Unit,
    progressionViewModel: ProgressionViewModel = viewModel(),
    navHostController: NavHostController

){
    val best by progressionViewModel.bestPerf.collectAsState()
    val latest by progressionViewModel.latestPerf.collectAsState()

    val topEndClickable by progressionViewModel.topEndClickable.collectAsState()
    val accelerationClickable by progressionViewModel.accelerationClickable.collectAsState()



    ProgressionActivityLayout(

        transitionToLiveDataActivity = transitionToLiveData,
        loadAcceleration = { progressionViewModel.onAccelerationLoaded() },
        loadTopEnd = {progressionViewModel.onTopEndLoaded()},
        topEndClickable = topEndClickable,
        accelerationClickable = accelerationClickable,
        best = best,
        latest = latest,
        transitionToPlots =  {
            navHostController.navigate("SprintsPlots")
                             },
        transitionToList = {
            navHostController.navigate("SprintsList")
        }

    )
}

@Preview(showBackground = true)
@Composable
fun ProgressionActivityLayout(
    transitionToLiveDataActivity: () -> Unit = { },
    loadAcceleration: () -> Unit= {},
    loadTopEnd:()-> Unit = {} ,
    topEndClickable: Boolean =true,
    accelerationClickable: Boolean =true,
    best: SprintPerfInfo = SprintPerfInfo(),
    latest: SprintPerfInfo = SprintPerfInfo(),
    transitionToPlots : () -> Unit = {},
    transitionToList : ()-> Unit = {}
    ) {

    Column(
        modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Top
    ) {
        NavigationBar(isSprintsClickable = true,
            isSummaryClickable = false,
            isPlotsClickable = true,
            toSprintsTransition = transitionToList,
            toSummaryTransition = {},
            toPlotsTransition = transitionToPlots
            )

        SelectorChoice(loadAcceleration,loadTopEnd,topEndClickable, accelerationClickable)
        if (best == latest && best.dayOfPerf ==null){
            Column(
                modifier = Modifier

                    .fillMaxWidth().weight(0.7f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
            Text("No Data")}
        }
        else if(best.id
            ==latest.id) {

            Column(
                modifier = Modifier
                    .fillMaxHeight(0.6f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
            Text("Your latest performance is your PB ;) Keep Going!",
                modifier = Modifier.padding(30.dp),
                textAlign = TextAlign.Center)
            MessageCard(
                best

            )
            }

        }
        else {

            Column(
                modifier = Modifier.fillMaxHeight(0.6f),
                verticalArrangement = Arrangement.Center
            ) {
                MessageCard(
                    best
                )
                MessageCard(
                    latest

                )
            }
        }
        NavigationButtons(transition = transitionToLiveDataActivity, liveDataActive = true, progressionActive = false)
    }

}

@Composable
fun SelectorChoice(loadAcceleration:()-> Unit,
                   loadTopEnd:()->Unit,
                   topEndClickable: Boolean,
                   accelerationClickable: Boolean){
    val selectorFontSize = 15.sp
    Row(modifier = Modifier
        .fillMaxWidth(),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.Center
    ) {
        Surface (modifier = Modifier
            .fillMaxWidth(0.3f)
            .clickable(topEndClickable, onClick = loadTopEnd),
                shape = RoundedCornerShape(7.dp),
                color = MaterialTheme.colorScheme.inversePrimary
        )
        {
            Text(
                "Top End",
                fontSize = selectorFontSize,
                textAlign = TextAlign.Center

            )
        }
        Spacer(Modifier.fillMaxWidth(0.17f))

        Surface (modifier = Modifier
            .fillMaxWidth(0.53f)
            .clickable(accelerationClickable, onClick = loadAcceleration),
            shape = RoundedCornerShape(7.dp),
            color = MaterialTheme.colorScheme.inversePrimary
        ){
        Text("Acceleration",
            fontSize = selectorFontSize,
            textAlign = TextAlign.Center,

        )}
    }
}


@Composable
fun MessageCard(perf: SprintPerfInfo?) {
    val detailsSpacing = 20.dp
    val descriptionFontSize = 18.sp
    val avgSpeedFontSize = 24.sp
    val detailsTitlesFontSize = 10.sp
    val detailsValuesFontSize = 12.sp

    Card(
        shape = MaterialTheme.shapes.medium, modifier =
            Modifier
                .padding(10.dp)
                .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    perf?.description.toString(), fontSize = descriptionFontSize,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text("${"%.2f".format(perf?.avgSpeed)}Km/h", fontSize = avgSpeedFontSize)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.Center) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Date", fontSize = detailsTitlesFontSize)

                    Text(text =
                        if (perf?.dayOfPerf !=null)
                         DateFormat.getDateInstance().format(perf.dayOfPerf)
                        else
                            "__.__.__",
                        fontSize = detailsValuesFontSize
                    )
                }
                Spacer(modifier = Modifier.width(detailsSpacing))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Distance", fontSize = detailsTitlesFontSize)
                    Text(perf?.distance.toString() + "m", fontSize = detailsValuesFontSize)
                }
                Spacer(modifier = Modifier.width(detailsSpacing))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Time", fontSize = detailsTitlesFontSize)
                    Text("${"%.2f".format(perf?.time)}s", fontSize = detailsValuesFontSize)

                }

            }

        }

    }

}


