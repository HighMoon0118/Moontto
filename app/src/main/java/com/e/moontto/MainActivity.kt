package com.e.moontto

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.endAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.scroll.rememberChartScrollSpec
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.horizontal.HorizontalAxis
import com.patrykandpatrick.vico.core.chart.scale.AutoScaleUp
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.Shape
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.formatter.ValueFormatter
import com.patrykandpatrick.vico.views.chart.line.lineChart
import kotlinx.coroutines.launch
import java.lang.StringBuilder


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()
    private var numbers: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainViewModel.updateNumbers()

        setContent {
            Column {
                MoonttoMain()
            }
        }
    }

    @Preview
    @Composable
    fun MoonttoMain(
    ) {
        TitleLayout()
        MoonttoTable()
        MoonttoGrid()
    }

    @Composable
    fun TitleLayout(
    ) {
        val lotto = mainViewModel.lottoMap.observeAsState(emptyMap()).value.run {
            toList().sortedWith(compareBy { it.first }).map { it.second }
        }
        val coroutineScope = rememberCoroutineScope()

        val listState = rememberLazyListState().apply {
            coroutineScope.launch {
                if (lotto.isNotEmpty()) scrollToItem(lotto.size - 1)
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            LazyRow(
                state = listState,
            ) {
                itemsIndexed(items = lotto) { round, list ->
                    NumbersOfRound(maxWidth, round, list)
                }
            }
        }

        val PERIOD = 30

        if (lotto.size >= PERIOD) {
            val chartData = lotto.subList(lotto.size - PERIOD, lotto.size).let { list ->
                val numList = Array(46) { 0 }
                for (numbers in list) for (number in numbers) numList[number]++

                List<ArrayList<Int>>( PERIOD + 1 ) { arrayListOf() }.also { countList ->
                    numList.forEachIndexed { num, count -> countList[count].add(num)}
                }
            }

            CountChart(
                chartData.mapIndexed { index, numList ->
                    FloatEntry(
                        x = index.toFloat(),
                        y = numList.size.toFloat()
                    )
                }
            )
        }
    }

    @Composable
    fun NumbersOfRound(
        roundWidth: Dp,
        round: Int,
        numbers: List<Int>
    ) {
        Column(
            modifier = Modifier
                .width(roundWidth)
                .height(80.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(text = "${round + 1}", fontWeight = FontWeight.Bold)
                Text(text = "회 로또 당첨번호", fontWeight = FontWeight.Bold)
            }
            Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                repeat(7) { idx ->
                    Text(text = "${numbers[idx]} / ", fontSize = 20.sp)
                }
            }
        }
    }

    @Composable
    fun MoonttoTable(

    ) {
        val 최근회차 = mainViewModel.lottoMap.value?.size ?: 1

        val moonttoList = mainViewModel.moonttoNumbers.observeAsState(arrayListOf()).value
            .sortedWith(
                compareBy<MainViewModel.Companion.MoonttoNumber> { it.count }
                .thenByDescending { 최근회차 - (it.lastRound ?: 1) }
            )

        TableRow(listOf(Pair("등수", 1f), Pair("번호", 1f), Pair("횟수", 1f), Pair("마지막 등장", 1.5f), Pair("확률", 1f)), false)

        if (moonttoList.size > 1) {
            LazyColumn(modifier = Modifier.fillMaxWidth().height(160.dp)) {

                itemsIndexed(items = moonttoList.subList(1, moonttoList.size)) { i, _ ->
                    TableRow(
                        listOf(
                            Pair("${i + 1}", 1f),
                            Pair("${moonttoList[i+1].number}", 1f),
                            Pair("${moonttoList[i+1].count}", 1f),
                            Pair("${moonttoList[i+1].lastRound}회 (${최근회차 - (moonttoList[i+1].lastRound ?: 1)})", 1.5f),
                            Pair("${moonttoList[i+1].probability}%", 1f)
                        ),
                        moonttoList[i+1].lastRound == 최근회차
                    )
                }
            }
        }
    }

    @Composable
    fun TableRow(
        value: List<Pair<String, Float>>,
        isHighlight: Boolean
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp),
//            horizontalArrangement = Arrangement.SpaceAround
        ) {
            repeat(value.size) { i ->
                Text(
                    modifier = Modifier.weight(value[i].second),
                    style = TextStyle(textAlign = TextAlign.Center),
                    text = value[i].first,
                    color = if (isHighlight) Color.Red else Color.Black,
                    fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }

    @Composable
    fun CountChart(
        entry: List<FloatEntry>
    ) {
        val producer = ChartEntryModelProducer(entry)

        Chart(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            chart = lineChart(baseContext),
            chartModelProducer = producer,
            startAxis = startAxis(),
            bottomAxis = bottomAxis(
//                tickPosition = HorizontalAxis.TickPosition.Center(0, 10)
            ),
        )
    }

    @Composable
    fun MoonttoGrid(

    ) {
        val lotto = mainViewModel.lottoMap.observeAsState(emptyMap()).value.run {
            toList().sortedWith(compareBy { - it.first }).map { it.second }
        }

        if (lotto.size > 1) {
            BoxWithConstraints(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
                val width = (maxWidth - 30.dp) / 45

                LazyColumn {
                    itemsIndexed(items = lotto) { i, list ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${lotto.size - i + 1}회",
                                modifier = Modifier.width(30.dp),
                                fontSize = 8.sp
                            )
                            repeat(45) { num ->
                                Text(
                                    text = if (list.contains(num + 1)) "${num + 1}" else "",
                                    fontSize = 4.sp,
                                    modifier = Modifier
                                        .size(width)
                                        .background(
                                            color = if (list.contains(num + 1)) Color.Green else Color.White
                                        )
                                        .border(border = BorderStroke((0.5).dp, Color.Black), shape = RectangleShape),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}