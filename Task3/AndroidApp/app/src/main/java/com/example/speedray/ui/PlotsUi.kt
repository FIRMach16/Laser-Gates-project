package com.example.speedray.ui


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.speedray.R
import com.example.speedray.data.PlotsViewModel
import java.util.Locale
import kotlin.math.pow
import kotlin.random.Random

enum class GlobalPlotChoice{
    TOP_END,ACCELERATION
}
val backgroundColor = Color(0xFF020000)
val textColor = Color(0xFFFFFFFF)
val deactivatedTextColor =Color(0xFF484545)
val barColor = Color(0xFF525151)

val indicatorsColor = Color(0xFF25C99B)
val plotColor =  Color(0xFF25C99B)

val selectedChoiceBarColor = Color(0xFF545254)
val canvasColor =  Color(0xFF282626)
@Composable
fun PlotsScreen(navHostController: NavHostController,plotsViewModel: PlotsViewModel){

    val avgSpeeds by plotsViewModel.listOfAverageSpeed.collectAsState()
    val times by plotsViewModel.listOfTimes.collectAsState()
    val distances by plotsViewModel.listOfShowcasedDistances.collectAsState()
    PlotsLayout(

        toSummaryTransition = { navHostController.navigate("Summary") },
        toSprintsTransition = { navHostController.navigate("SprintsList") },
        avgSpeeds = avgSpeeds, times = times, distances = distances,
        changePlottedData = {plotChoice,weighted -> plotsViewModel.onPlotsLoaded(plotChoice,weighted)},
        changeShowcasedData = {distanceChosen-> plotsViewModel.changeShowcasedData(distanceChosen)}
    )

}
@Preview
@Composable
fun PlotsLayout(
    toSprintsTransition : ()-> Unit={},
    toSummaryTransition:()-> Unit={},
    avgSpeeds: List<Float> =emptyList(),
    times:List<Float> =emptyList(),
    distances: List<Int> =emptyList(),
    changePlottedData: (GlobalPlotChoice, Boolean)-> Unit= {a,b ->}, //this is a substitute for the do nothing
    // function in SprintUi (will change it when refactoring) TODO: Refactor SprintUi (the doNothingFunc Should go)
    changeShowcasedData: (Int) ->Unit = {a ->}
){
    Column(modifier = Modifier.background(backgroundColor).fillMaxSize(), verticalArrangement = Arrangement.Top) {
        NavigationBar(
            isSprintsClickable = true,
            isSummaryClickable = true,
            isPlotsClickable = false,
            toPlotsTransition = {},
            toSprintsTransition = toSprintsTransition,
            toSummaryTransition = toSummaryTransition,
            defaultColor = textColor
        )
        Plots(times
            , avgSpeeds,
            distances,
            changePlottedData,
            changeShowcasedData)
    }
}

@Composable
fun Plots(
          times: List<Float> = emptyList(),
          avgSpeeds: List<Float> = emptyList(),
          distances: List<Int> =emptyList(),
          changePlottedData: (GlobalPlotChoice, Boolean) -> Unit ={a,b->},
          changeShowcasedData: (Int) -> Unit ={a->}
){


    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        //TopEnd , Acceleration and weighted selector
        GlobalFilter(changePlottedData)
        //Time Plot
        Text("Times (s)",
            fontSize = 30.sp,
            color = textColor,
            textAlign = TextAlign.Center)
        DistanceChooser(distances,changeShowcasedData)
        ChartPlot( valuesToDraw = times)
        Spacer(Modifier.height(30.dp))
        //avgSpeed
        Text("Average speeds (KM/h)",
            fontSize = 30.sp,
            color = textColor,
            textAlign = TextAlign.Center)
        ChartPlot( valuesToDraw = avgSpeeds)

    }
}

@Composable
fun GlobalFilter(changePlottedData: (GlobalPlotChoice, Boolean) -> Unit = {a,b-> }){
    var checked by remember { mutableStateOf(false) }
    val selectorChoice = remember { mutableStateOf(GlobalPlotChoice.TOP_END) }
    val topEndSelector = when(selectorChoice.value){
        GlobalPlotChoice.TOP_END ->false
        GlobalPlotChoice.ACCELERATION -> true
    }
    val accelerationSelector = when(selectorChoice.value){
        GlobalPlotChoice.TOP_END -> true
        GlobalPlotChoice.ACCELERATION -> false
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically

    ) {

        SelectorContent(
            id = GlobalPlotChoice.TOP_END, Modifier.weight(0.3f),
            topEndSelector, selectorChoice,
            changePlottedData = changePlottedData, weighted = checked
        )
        SelectorContent(
            id = GlobalPlotChoice.ACCELERATION, Modifier.weight(0.3f),
            accelerationSelector, selectorChoice,
            changePlottedData = changePlottedData, weighted = checked
        )

        Row(modifier = Modifier.weight(0.3f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center){
            Text("Weighted", color = textColor)
            Checkbox(
                checked = checked,
                onCheckedChange = { checked = !checked
                                    changePlottedData(selectorChoice.value, checked)
                                    }
            )
        }
    }
}
@Composable
fun SelectorContent(id: GlobalPlotChoice, //to modify the bar bellow the text
                                            //the composable needs to know what it contains
                    modifier: Modifier,
                    selector: Boolean,
                    selectorChoice: MutableState<GlobalPlotChoice>,
                    changePlottedData: (GlobalPlotChoice, Boolean) -> Unit = {a,b->},
                    weighted: Boolean = false
                    ){
    val text = when(id){
        GlobalPlotChoice.ACCELERATION -> "Acceleration"
        GlobalPlotChoice.TOP_END -> "Top End"
    }
    Column (modifier = modifier
        .clickable(selector, onClick ={
            selectorChoice.value= when(selectorChoice.value){
                GlobalPlotChoice.TOP_END ->GlobalPlotChoice.ACCELERATION
                GlobalPlotChoice.ACCELERATION -> GlobalPlotChoice.TOP_END
            }
            changePlottedData(selectorChoice.value,weighted)
        } )  ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
        Text(text, textAlign = TextAlign.Center, color =textColor)
        Spacer(Modifier.height(1.dp))
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                when (id) {
                    GlobalPlotChoice.TOP_END -> when (selectorChoice.value) {
                        GlobalPlotChoice.TOP_END -> selectedChoiceBarColor
                        GlobalPlotChoice.ACCELERATION -> backgroundColor
                    }

                    GlobalPlotChoice.ACCELERATION -> when (selectorChoice.value) {
                        GlobalPlotChoice.ACCELERATION -> selectedChoiceBarColor
                        GlobalPlotChoice.TOP_END -> backgroundColor
                    }
                }

            ))
    }
}
@Composable
fun ChartPlot(valuesToDraw: List<Float>){
    val textMeasurer = rememberTextMeasurer()
    Box(
        modifier = Modifier
            .background(backgroundColor)

    ){
        Canvas(
            modifier = Modifier
                .padding(8.dp)
                .aspectRatio(16 / 9f)
                .fillMaxSize()
                .background(canvasColor)


        ) {

            val barWidthPx = 1.dp.toPx()
            drawRect(barColor, style = Stroke(barWidthPx))

            // vertical lines
            val verticalLines = 5
            val verticalLinesSpacing = size.width/(verticalLines+1)
            repeat(verticalLines){i->
                val startX = verticalLinesSpacing *(i+1)
                drawLine(
                    barColor,
                    start = Offset(startX,0f),
                    end = Offset(startX,size.height),
                    strokeWidth = barWidthPx

                )
            }
            //horizontal lines

            val horizontalLines = 3
            val horizontalLinesSpacing = size.height/(horizontalLines+1)

            repeat(horizontalLines){i->
                val startY = horizontalLinesSpacing*(i+1)
                drawLine(
                    barColor,
                    start = Offset(0f,startY),
                    end = Offset(size.width,startY),
                    strokeWidth = barWidthPx
                )
            }
            if (!valuesToDraw.isEmpty()) {
                //plot
                if (valuesToDraw.size == 1) {
                    drawText(
                        textMeasurer = textMeasurer,
                        text = String.format(Locale.ENGLISH, "%.2f", valuesToDraw[0]),
                        topLeft = Offset(size.width*0.4f, size.height * 0.4f),
                        style =
                            TextStyle(
                                color = indicatorsColor,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold
                            ),
                    )

                } else {
                    val path = generatePath(
                        data = valuesToDraw, size
                    )
                    drawPath(path, color = plotColor, style = Stroke(1.5f.dp.toPx()))
                    //adding coloring
                    val gradientPath = Path()
                    gradientPath.addPath(path)
                    gradientPath.lineTo(size.width, size.height)
                    gradientPath.lineTo(0f, size.height)
                    gradientPath.close()
                    val gradientBrush = Brush.verticalGradient(
                        listOf(
                            indicatorsColor.copy(0.5f),
                            Color.Transparent
                        )
                    )
                    drawPath(gradientPath, gradientBrush, style = Fill)


                    //value indicators

                    //min => best time or worst speed
                    drawLine(
                        barColor,
                        start = Offset(0f, 0.9f * size.height),
                        end = Offset(size.width, 0.9f * size.height),
                        strokeWidth = barWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 5f)
                    )

                    //max => worst time or best avg speed
                    drawLine(
                        barColor,
                        start = Offset(0f, 0.1f * size.height),
                        end = Offset(size.width, 0.1f * size.height),
                        strokeWidth = barWidthPx,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 5f)
                    )

                    drawText(
                        textMeasurer = textMeasurer,
                        text = String.format(Locale.ENGLISH, "%.2f", valuesToDraw.max()),
                        topLeft = Offset(0f, size.height * 0.03f),

                        style =
                            TextStyle(
                                color = indicatorsColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = "${valuesToDraw.size} Sprints",
                        topLeft = Offset(size.width*0.8f, size.height * 0.03f),

                        style =
                            TextStyle(
                                color = indicatorsColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                    )

                    drawText(
                        textMeasurer = textMeasurer,
                        text = String.format(Locale.ENGLISH, "%.2f", valuesToDraw.min()),
                        topLeft = Offset(0f, size.height * 0.9f),

                        style =
                            TextStyle(
                                color = indicatorsColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            ),
                    )
                }
            }
        }
    }
}


fun generatePath(data:List<Float>,canvasSize: Size): Path{
    val path = Path()

    // for x  :idx = 0 -> O (px)
    //        :idx = data.size -> canvasSize.width

    // for y  :y=min(data) -> canvasSize.height
    //        :y=max(data) -> 0 (px)

    val nbOfElements = data.size
    val maxOfArray = data.max()
    val minOfArray = data.min()

    var previousPointCordX : Float
    var previousPointCordY : Float
    previousPointCordY = 0f
    previousPointCordX=0f

    data.forEachIndexed{i,value ->
        val x = (i.toFloat()/(nbOfElements-1).toFloat())*canvasSize.width
        val y = ((maxOfArray - value) /(maxOfArray-minOfArray).toFloat())*canvasSize.height*0.8f+canvasSize.height*0.1f

        // to apply curves we can use the quadraticTo method
        // which uses a quadratic bezier curve => 1 control points is needed

        if(i==0){
            path.moveTo(x,y) // puts the first point
            previousPointCordX = x
            previousPointCordY = y
        }
        else{

            val ctrlPointCoords = calculateControlPoint(
                x,y,previousPointCordX,previousPointCordY,canvasSize
            )



            path.quadraticTo(ctrlPointCoords.first
                ,ctrlPointCoords.second
                ,x,y)
            previousPointCordX = x
            previousPointCordY = y



        }

    }
     
    return path
}


fun calculateControlPoint(
    currentPointX: Float,
    currentPointY:Float,
    previousPointX: Float,
    previousPointY: Float,
    size: Size //to not exceed limits

) : Pair<Float, Float>{



    val midPointX = (currentPointX + previousPointX)/2
    val midPointY = (currentPointY +previousPointY)/2

    if (currentPointY ==previousPointY){
        return Pair(midPointX,midPointY)
    }

    // any line has an equation of the form y = ax +b


    val slopeOfPerpendicularLine = -((currentPointX - previousPointX)/(currentPointY-previousPointY))// ->a
    val constantOfPerpendicularLine = midPointY-slopeOfPerpendicularLine*midPointX // -> b
    val distanceToLine = 2*size.height/100f

    // to find the point of distance "distanceToLine" to the midpoint
    // that also belongs to the perpendicular line we need to solve a quadratic equation

    val b = -(((2*constantOfPerpendicularLine)/(slopeOfPerpendicularLine.pow(2))+
            ((2*midPointX)/slopeOfPerpendicularLine) +
            (2*midPointY))/(1+(1/slopeOfPerpendicularLine.pow(2))))
    val c = (((constantOfPerpendicularLine.pow(2))/(slopeOfPerpendicularLine.pow(2)))+
            ((2*midPointX*constantOfPerpendicularLine)/slopeOfPerpendicularLine)+
            midPointX.pow(2)+midPointY.pow(2)-distanceToLine.pow(2))/(1+(1/slopeOfPerpendicularLine.pow(2)))

    val delta = b.pow(2)-4*c


    val y1 = (-b+delta.pow(0.5f))/2 //y1 > y2
    val y2 = (-b-delta.pow(0.5f))/2



    // if you are reading this, just trust me bro or do the math yourself
    // Note : if you find a more elegant solution tell me and be nice about it
    // (think about my feelings)

    var x: Float

    val y: Float = if (previousPointY>currentPointY){
        // positive slope
        y1
    }
    else{
        y2

    }
    x = (y- constantOfPerpendicularLine)/(slopeOfPerpendicularLine)

    return if((x<0)||(x>size.width)||(y<0f)||(y>size.height)){
        Pair(midPointX,midPointX)
    }
    else{
        Pair(x,y)
    }


}

@Composable
fun DistanceChooser(distances: List<Int>,
                    changeShowcasedData:(Int)-> Unit ={a->}){


    var expanded by remember { mutableStateOf(false) }
    var selectorText by remember { mutableStateOf("All distances") }

    val activeNotActiveColor = if(distances.isEmpty()){
        deactivatedTextColor
    }
    else{
        textColor
    }
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(1.0f)
    ) {
        IconButton(onClick = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(selectorText,
                    color =activeNotActiveColor)
                Spacer(modifier = Modifier.width(10.dp))
                Icon(
                    painterResource(R.drawable.ic_bottom_arrow),
                    contentDescription = "More options", tint = activeNotActiveColor
                )
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            distances.forEach { distance->
                DropdownMenuItem(
                    text = { Text(distance.toString()) },
                    onClick = { changeShowcasedData(distance)
                            selectorText=distance.toString()
                        expanded=!expanded}
                )
            }



        }
    }

}
