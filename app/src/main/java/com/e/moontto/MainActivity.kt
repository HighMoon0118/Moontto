package com.e.moontto

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.e.moontto.ui.BottomNavi


class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            mainViewModel.updateNumbers()

            Column {
                MoonttoMain()
            }
        }
    }

    @Preview
    @Composable
    fun MoonttoMain(
    ) {
        Log.d("ㅇㅇㅇ MoonttoMain", "MoonttoMain")
        BottomNavi(baseContext, mainViewModel)
    }

    @Composable
    fun MoonttoGrid(

    ) {
        val viewModel: MainViewModel = viewModel()
        val lotto = viewModel.lottoMap.observeAsState(emptyMap()).value.run {
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