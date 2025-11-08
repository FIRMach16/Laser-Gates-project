package com.example.speedray.ui

import android.R.color.black
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.speedray.R
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.speedray.data.ProgressionViewModel
import com.example.speedray.data.SprintsListViewModel



@Composable
fun MyProgressionNavHost(
    transitionToLiveData: ()-> Unit,
    progressionViewModel: ProgressionViewModel,
    sprintsListViewModel: SprintsListViewModel
){
    val navigationController = rememberNavController()

    NavHost(navController = navigationController, startDestination = "Summary"){
        composable(route= "Summary"){
            progressionViewModel.onTopEndLoaded()
            ProgressionScreen(
                transitionToLiveData,
                progressionViewModel,
                navigationController
                )
        }

        composable(route= "SprintsList"){
            sprintsListViewModel.showcaseNSprints()
            SprintsScreen(
                transitionToLiveData,
                navigationController,
                sprintsListViewModel
            )
        }

//        composable(route= "SprintsPlots"){
//            ProgressionScreen(
//                transitionToLiveData,
//                progressionViewModel,
//                navigationController
//            )
//        }
    }
}
@Preview(showBackground = true)
@Composable
fun NavigationBar(
    isSprintsClickable : Boolean = true,
    isSummaryClickable : Boolean = true,
    isPlotsClickable:Boolean = false,
    toSprintsTransition : ()-> Unit = {},
    toSummaryTransition : ()->Unit = {},
    toPlotsTransition: () -> Unit= {}
){
    val borderWidth = 0.dp
    val textSize = 10.sp
    Row(modifier = Modifier.fillMaxWidth().padding(10.dp)) {
        Box(modifier = Modifier.clickable(isSprintsClickable, onClick = toSprintsTransition)
            .padding(10.dp)
            .weight(0.3f),
            contentAlignment = Alignment.Center){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val iconColor = when(isSprintsClickable){
                    true-> Color(0xFF000000)
                    false-> Color(0xFF1F8EEC)
                }
                Icon(
                    painter = painterResource(R.drawable.ic_sprints_list),
                    contentDescription = "lists",
                    tint = iconColor
                )
                Text("Sprints", fontSize = textSize, color = iconColor)
            }
        }
        Spacer(modifier = Modifier.weight(0.1f))
        Box(modifier = Modifier.clickable(isSummaryClickable, onClick = toSummaryTransition)
            .padding(10.dp)
            .weight(0.3f),
            contentAlignment = Alignment.Center){
            val iconColor = when(isSummaryClickable){
                true-> Color(0xFF000000)
                false-> Color(0xFF1F8EEC)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.ic_summary),
                contentDescription = "summary",
                tint = iconColor
            )
            Text("Summary", fontSize = textSize, color = iconColor)
            }
        }
        Spacer(modifier = Modifier.weight(0.1f))
        Box(modifier = Modifier.clickable(isPlotsClickable, onClick = toPlotsTransition)
            .padding(10.dp)
            .weight(0.3f),
            contentAlignment = Alignment.Center){
            val iconColor = when(isPlotsClickable){
                true-> Color(0xFF000000)
                false-> Color(0xFF1F8EEC)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(R.drawable.ic_plot),
                contentDescription = "plots",
                tint = iconColor
            )
            Text("Plots", fontSize = textSize, color = iconColor)
            }
        }
    }
}