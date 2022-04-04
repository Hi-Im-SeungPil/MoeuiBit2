package org.jeonfeel.moeuibit2.ui.coindetail

import android.graphics.Color
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel

@Composable
fun ChartScreen(coinDetailViewModel: CoinDetailViewModel) {

    Column(modifier = Modifier.fillMaxSize()) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "일") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "주") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "월") }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "1분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "3분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "5분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "10분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "15분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "30분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "60분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "240분") }
        }

        AndroidView(factory = { context ->
            CombinedChart(context).apply {
                this.initCombinedChart()
                coinDetailViewModel.requestChartData("1", this@apply)
            }
        },
            modifier = Modifier.fillMaxSize()
        )
    }
}

fun CombinedChart.initCombinedChart() {
    this.description.isEnabled = false
    this.isScaleYEnabled = false
    this.isDoubleTapToZoomEnabled = false
    this.isDragDecelerationEnabled = false
    this.isDragEnabled = true
    this.isAutoScaleMinMaxEnabled = true
    this.setPinchZoom(false)
    this.setDrawGridBackground(false)
    this.setDrawBorders(false)
    this.fitScreen()
    val xAxis = this.xAxis
    xAxis.textColor = Color.parseColor("#000000")
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.setAvoidFirstLastClipping(true)
    xAxis.setLabelCount(3, true)
    xAxis.setDrawLabels(false)
    xAxis.setDrawAxisLine(false)
    xAxis.axisLineColor = Color.GRAY
    xAxis.granularity = 3f
    xAxis.isGranularityEnabled = true
    val leftAxis = this.axisLeft
    leftAxis.setDrawGridLines(false)
    leftAxis.setDrawLabels(false)
    leftAxis.setDrawAxisLine(false)
    leftAxis.axisMinimum = 0f
    val rightAxis = this.axisRight
    rightAxis.setLabelCount(5, true)
    rightAxis.textColor = Color.BLACK
    rightAxis.setDrawAxisLine(true)
    rightAxis.setDrawGridLines(false)
    rightAxis.axisLineColor = Color.GRAY
    rightAxis.minWidth = 50f
}


//mBarChart.setOnChartGestureListener(new OnChartGestureListener() {
//    @Override
//    public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//        mIsCanLoad = false;
//    }
//
//    @Override
//    public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//
//        int rightXIndex = mBarChart.getHighestVisibleXIndex(); //Get the index displayed on the far right of the x-axis in the visible area
//        int size = mBarChart.getBarData().getXVals().size();
//
//        if(lastPerformedGesture == ChartTouchListener.ChartGesture.DRAG){
//            mIsCanLoad = true;
//            if(rightXIndex == size-1 || rightXIndex == size){
//                mIsCanLoad = false;
//                // Operation to load more data
//
//            }
//        }
//    }
//
//    @Override
//    public void onChartLongPressed(MotionEvent me) {
//
//    }
//
//    @Override
//    public void onChartDoubleTapped(MotionEvent me) {
//
//    }
//
//    @Override
//    public void onChartSingleTapped(MotionEvent me) {
//
//    }
//
//    @Override
//    public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {
//
//    }
//
//    @Override
//    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
//        Log.i("My","onChartScale");
//    }
//
//    @Override
//    public void onChartTranslate(MotionEvent me, float dX, float dY) {
//        if(mIsCanLoad){
//            int rightXIndex = mBarChart.getHighestVisibleXIndex(); //Get the index displayed on the far right of the x-axis in the visible area
//            int size = mBarChart.getBarData().getXVals().size();
//            if(rightXIndex == size-1 || rightXIndex == size){
//                mIsCanLoad = false;
//                // Operation to load more data
//
//            }
//        }
//    }
//});