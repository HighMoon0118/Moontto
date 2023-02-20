package com.e.moontto

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.StringBuilder
import kotlin.coroutines.coroutineContext

class MainViewModel(val context: Application): AndroidViewModel(context) {

    val lottoNumbers: MutableLiveData<ArrayList<List<Int>>> by lazy {
        getAllNumbers()
    }
    private val utils = Utils(context)
    private val requestQueue = Volley.newRequestQueue(context)
    private val scope = CoroutineScope(Dispatchers.Main)

    private fun getAllNumbers(): MutableLiveData<ArrayList<List<Int>>> {
        Log.d("MainViewModel", "getAllNumbers")
        val tmpList = arrayListOf<List<Int>>()
        var round = 1
        while (true) {
            val numbers = utils.getNumbersOf(round)
            if (numbers.isEmpty()) break
            else {
                tmpList.add(numbers)
                round ++
            }
        }
        return MutableLiveData<ArrayList<List<Int>>>().apply {
            value = tmpList
        }
    }

    fun updateNumbers() {
        Log.d("MainViewModel", "updateNumbers")
        scope.launch {
            requestLotto(1)
        }
    }

    fun requestLotto(round: Int) {
        Log.d("MainViewModel", "requestLotto")
        if (lottoNumbers.value != null && lottoNumbers.value!!.size > round && lottoNumbers.value!![round - 1].isNotEmpty()) {
            scope.launch { requestLotto(round + 1) }
        } else if (utils.getNumbersOf(round).isNotEmpty()) {
            lottoNumbers.value?.add(round - 1, utils.getNumbersOf(round))
            scope.launch { requestLotto(round + 1) }
        } else {
            val url = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo=$round"

            val request = object : StringRequest(
                url,
                Response.Listener { response ->
                    if (response == null || response.contains("fail")) return@Listener
                    utils.setNumbersOf(round, response)
                    lottoNumbers.value?.add(round - 1, utils.getNumbersOf(round))
                    scope.launch { requestLotto(round + 1) }
                },
                Response.ErrorListener {
                    Log.d("error", "$it")
                }
            ) {
                override fun getParams(): MutableMap<String, String>? {
                    return super.getParams()
                }
            }
            request.setShouldCache(false)
            requestQueue.add(request)
        }
    }
}