package com.e.moontto.ui

import android.content.Context
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.views.chart.line.lineChart


@Composable
fun CountChart(
    context: Context,
    entry: List<FloatEntry>
) {
    val producer = ChartEntryModelProducer(entry)

    Chart(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        chart = lineChart(context),
        chartModelProducer = producer,
        startAxis = startAxis(),
        bottomAxis = bottomAxis(
//                tickPosition = HorizontalAxis.TickPosition.Center(0, 10)
        ),
    )
}