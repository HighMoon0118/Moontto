package com.e.moontto.ui

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.e.moontto.MainViewModel
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlinx.coroutines.launch


@Composable
fun TitleLayout(
    context: Context,
    mainViewModel: MainViewModel
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

    Column {
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
        Log.d("ㅇㅇㅇ", "lotto size = ${lotto.size}")

        if (lotto.size >= PERIOD) {
            val chartData = lotto.subList(lotto.size - PERIOD, lotto.size).let { list ->
                val numList = Array(46) { 0 }
                for (numbers in list) for (number in numbers) numList[number]++

                List<ArrayList<Int>>(PERIOD + 1) { arrayListOf() }.also { countList ->
                    numList.forEachIndexed { num, count -> countList[count].add(num) }
                }
            }

            CountChart(
                context,
                chartData.mapIndexed { index, numList ->
                    FloatEntry(
                        x = index.toFloat(),
                        y = numList.size.toFloat()
                    )
                }
            )
        }
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
    mainViewModel: MainViewModel
) {
    val 최근회차 = mainViewModel.lottoMap.observeAsState(emptyMap()).value?.size ?: 1
    val moonttoList = mainViewModel.moonttoNumbers.observeAsState(arrayListOf()).value
        .sortedWith(
            compareBy<MainViewModel.Companion.MoonttoNumber> { it.count }
                .thenByDescending { 최근회차 - (it.lastRound ?: 1) }
        )

    Column {
        TableRow(listOf(Pair("등수", 1f), Pair("번호", 1f), Pair("횟수", 1f), Pair("마지막 등장", 1.5f), Pair("확률", 1f)),
            isHighlight = false,
            isTitleOfColumn = true
        )

        Log.d("ㅇㅇㅇ", "moonttoList size = ${moonttoList.size}")
        if (moonttoList.size > 1) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {

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
}

@Composable
fun TableRow(
    value: List<Pair<String, Float>>,
    isHighlight: Boolean,
    isTitleOfColumn: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(if (isTitleOfColumn) 40.dp else 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(value.size) { i ->
            Text(
                modifier = Modifier.weight(value[i].second).background(color = if (isHighlight) Color.Red else Color.White),
                text = value[i].first,
                textAlign = TextAlign.Center,
                color = if (isHighlight) Color.White else Color.Black,
                fontWeight = if (isTitleOfColumn || isHighlight) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}