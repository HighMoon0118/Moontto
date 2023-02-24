package com.e.moontto

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    }

    @Composable
    fun NumbersOfRound(
        roundWidth: Dp,
        round: Int,
        numbers: List<Int>
    ) {
        Column(
            modifier = Modifier.width(roundWidth).height(80.dp)
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
            LazyColumn(modifier = Modifier.fillMaxWidth()) {

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
            modifier = Modifier.fillMaxWidth().height(20.dp),
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
}