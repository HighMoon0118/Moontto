package com.e.moontto

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.lang.StringBuilder


class Utils(context: Context) {

    var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("Moontto", Context.MODE_PRIVATE)
    }



    fun getNumbersOf(round: Int): List<Int> =
        sharedPreferences.getString("$round", null)?.run {
            Log.d("Utils", "getNumbersOf")
            split("/").map {
                var number = -1
                try {
                    number = it.toInt()
                } catch (e: Exception) {
                    println(e)
                }
                number
            }
        } ?: emptyList()
    fun setNumbersOf(round: Int, data: String) {
        Log.d("Utils", "setNumbersOf")
        val jsonObject = JsonParser.parseString(data) as JsonObject
        val sb = StringBuilder()

        try {
            sb.append(jsonObject.get("drwtNo1")).append("/")
            sb.append(jsonObject.get("drwtNo2")).append("/")
            sb.append(jsonObject.get("drwtNo3")).append("/")
            sb.append(jsonObject.get("drwtNo4")).append("/")
            sb.append(jsonObject.get("drwtNo5")).append("/")
            sb.append(jsonObject.get("drwtNo6")).append("/")
            sb.append(jsonObject.get("bnusNo"))
        } catch (error: Exception) {
            Log.d("numbersOf", "error")
        } finally {
            sharedPreferences.edit { it.putString("$round", sb.toString()) }
        }
    }

    private fun SharedPreferences.edit(edit: (SharedPreferences.Editor) -> Unit) {
        edit().apply {
            edit(this)
            this.apply()
        }
    }
}
