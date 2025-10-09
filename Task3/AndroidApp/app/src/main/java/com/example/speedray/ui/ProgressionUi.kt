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
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.speedray.R
import com.example.speedray.SprintPerfInfo
import java.util.Date

@Composable
fun NavButtonContent(scale: ContentScale,
                     image: Painter,
                     text: String,
                     transition:()-> Unit,
                     clickable: Boolean){

    Card{
        Column (modifier = Modifier.size(120.dp)
            .clickable(enabled = clickable,onClick = transition),
            horizontalAlignment = Alignment.CenterHorizontally,){

            Image(
                modifier = Modifier.fillMaxSize(0.8f),
                contentScale = scale,
                painter = image,
                contentDescription = text
            )
            Text(text=text)

        }}
}
@Composable
fun NavigationButtons(transition:()-> Unit,
                      liveDataActive: Boolean,
                      progressionActive: Boolean){


    Row(
        modifier = Modifier.fillMaxSize().padding(10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ){
        NavButtonContent(ContentScale.FillBounds,
            painterResource(R.drawable.ic_timer),
            "Live Data",
            transition,liveDataActive
        )
        Spacer(modifier = Modifier.width(50.dp))
        NavButtonContent(ContentScale.FillBounds,
            painterResource(R.drawable.ic_progression),
            "Progression",transition,progressionActive)

    }

}
@Preview(showBackground = true)
@Composable
fun ProgressionActivityLayout(transition: () -> Unit = {print(1)}){
    Column(modifier = Modifier.fillMaxSize()
        , verticalArrangement = Arrangement.Center){
        Column(modifier= Modifier.fillMaxHeight(0.8f),
            verticalArrangement = Arrangement.Center) {
            MessageCard(
                SprintPerfInfo("Best", 35.12f, Date(), 20, 2.05f)
            )
            MessageCard(
                SprintPerfInfo("Latest", 25.71f, Date(), 20, 2.80f)
            )
        }
        NavigationButtons(transition = transition, liveDataActive = true, progressionActive = false)
    }

}

//@Composable
//fun ProgressionNavButtons(){
//    val context = LocalContext.current
//    val transitionToLiveData = {
//        val intent = Intent(context, MainActivity::class.java)
//    }
//}

@Composable
fun MessageCard(perf: SprintPerfInfo){
    val detailsSpacing = 20.dp
    val descriptionFontSize =  18.sp
    val avgSpeedFontSize = 24.sp
    val detailsTitlesFontSize = 10.sp
    val detailsValuesFontSize = 12.sp

    Card(shape = MaterialTheme.shapes.medium, modifier =
        Modifier.padding(10.dp)
            .fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    perf.description, fontSize = descriptionFontSize,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(20.dp))
                Text(perf.avgSpeed.toString() + "Km/h", fontSize = avgSpeedFontSize)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.Center) {

                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Date", fontSize = detailsTitlesFontSize)
                    Text(
                        DateFormat.getDateInstance().format(perf.dayOfPerf),
                        fontSize = detailsValuesFontSize
                    )
                }
                Spacer(modifier = Modifier.width(detailsSpacing))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Distance", fontSize = detailsTitlesFontSize)
                    Text(perf.distance.toString() + "m", fontSize = detailsValuesFontSize)
                }
                Spacer(modifier = Modifier.width(detailsSpacing))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {

                    Text("Time", fontSize = detailsTitlesFontSize)
                    Text(perf.time.toString() + "s", fontSize = detailsValuesFontSize)

                }

            }

        }

    }

}



//@Preview("Results Model", showBackground = true)
//@Composable
//fun PreviewMessageCard(){
//    Column {
//        MessageCard(
//            SprintPerfInfo("Best", 35.12f, Date(), 20, 2.05f)
//        )
//        Spacer(modifier = Modifier.height(10.dp))
//        MessageCard(
//            SprintPerfInfo("Latest", 25.71f, Date(), 20, 2.80f)
//        )
//    }
//}

//@Preview("Buttons")
//@Composable
//fun PreviewButtons(){
//    NavigationButtons({print("Hello")}, liveDataActive = true, progressionActive = false)
//}