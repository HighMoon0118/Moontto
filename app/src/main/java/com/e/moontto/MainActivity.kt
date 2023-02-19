package com.e.moontto

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.google.gson.JsonParser
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
    }

    @Composable
    fun TitleLayout(
    ) {
        val lotto = mainViewModel.lottoNumbers.observeAsState(arrayListOf()).value

        LazyColumn {
            itemsIndexed(lotto) { round, list ->
                Column {
                    Row {
                        Text(text = "${round + 1}")
                        Text(text = "회 로또 당첨번호")
                    }
                    Row {
                        repeat(7) { idx ->
                            Text(text = "${lotto[round][idx]}")
                        }
                    }
                }
            }
        }
    }
}