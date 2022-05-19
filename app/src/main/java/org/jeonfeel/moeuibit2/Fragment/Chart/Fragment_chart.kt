//package org.jeonfeel.moeuibit2.Fragment.Chart
//
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase.Companion.getInstance
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase.myCoinDAO
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO.isInsert
//import org.jeonfeel.moeuibit2.Database.MyCoin.purchasePrice
//import org.jeonfeel.moeuibit2.DTOS.CoinCandleDataDTO.candleDateTimeKst
//import org.jeonfeel.moeuibit2.DTOS.CoinCandleDataDTO.candleTransactionAmount
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase.userDAO
//import org.jeonfeel.moeuibit2.Database.UserDAO.all
//import org.jeonfeel.moeuibit2.Database.User.krw
//import org.jeonfeel.moeuibit2.Database.MyCoin.quantity
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.Activity_coinDetails.globalCurrentPrice
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO.insert
//import org.jeonfeel.moeuibit2.Database.UserDAO.updateMinusMoney
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase.transactionInfoDAO
//import org.jeonfeel.moeuibit2.Database.TransactionInfoDAO.insert
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO.updatePurchasePriceInt
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO.updatePurchasePrice
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO.updateQuantity
//import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO.coinArcadePrice
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoinArcade.setItem
//import org.jeonfeel.moeuibit2.Database.UserDAO.updatePlusMoney
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO.delete
//import org.jeonfeel.moeuibit2.Database.TransactionInfoDAO.select
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvTransactionInfo.getItemCount
//import org.jeonfeel.moeuibit2.dtos.MyCoinsDTO.myCoinsSymbol
//import org.jeonfeel.moeuibit2.dtos.MyCoinsDTO.setCurrentPrice
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO.all
//import org.jeonfeel.moeuibit2.Database.MyCoin.market
//import org.jeonfeel.moeuibit2.Database.MyCoin.koreanCoinName
//import org.jeonfeel.moeuibit2.Database.MyCoin.symbol
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvMyCoins.setCurrentPrices
//import org.jeonfeel.moeuibit2.Database.UserDAO.insert
//import org.jeonfeel.moeuibit2.Database.UserDAO.deleteAll
//import org.jeonfeel.moeuibit2.Database.TransactionInfoDAO.deleteAll
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO.deleteAll
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase.favoriteDAO
//import org.jeonfeel.moeuibit2.Database.FavoriteDAO.deleteAll
//import org.jeonfeel.moeuibit2.Database.FavoriteDAO.all
//import org.jeonfeel.moeuibit2.Database.Favorite.market
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoin.setMarkets
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoin.setFavoriteStatus
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoin.getFilter
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoin.setItem
//import org.jeonfeel.moeuibit2.DTOS.CoinDTO.market
//import android.os.AsyncTask
//import org.json.JSONArray
//import org.json.JSONException
//import com.github.mikephil.charting.charts.CombinedChart
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase
//import com.github.mikephil.charting.components.LimitLine
//import org.jeonfeel.moeuibit2.CustomLodingDialog
//import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart.GetRecentCoinChart
//import android.widget.RadioGroup
//import android.widget.RadioButton
//import org.jeonfeel.moeuibit2.Fragment.Chart.GetMovingAverage
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import org.jeonfeel.moeuibit2.R
//import org.jeonfeel.moeuibit2.Fragment.Chart.MoEuiBitMarkerView
//import com.github.mikephil.charting.listener.OnChartGestureListener
//import android.view.MotionEvent
//import com.github.mikephil.charting.listener.ChartTouchListener.ChartGesture
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.components.YAxis
//import com.github.mikephil.charting.components.LegendEntry
//import com.github.mikephil.charting.components.Legend
//import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart
//import org.jeonfeel.moeuibit2.DTOS.CoinCandleDataDTO
//import org.jeonfeel.moeuibit2.Fragment.Chart.GetUpBitCoins
//import org.json.JSONObject
//import com.github.mikephil.charting.utils.EntryXComparator
//import org.jeonfeel.moeuibit2.Database.MyCoin
//import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart.myValueFormatter
//import android.widget.Toast
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.Activity_coinDetails
//import com.github.mikephil.charting.components.MarkerView
//import android.widget.TextView
//import android.widget.LinearLayout
//import androidx.recyclerview.widget.RecyclerView
//import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoinArcade
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvTransactionInfo
//import org.jeonfeel.moeuibit2.Database.TransactionInfo
//import org.jeonfeel.moeuibit2.Fragment.coinOrder.Fragment_coinOrder.GetUpBitCoinArcade
//import android.widget.EditText
//import android.widget.Spinner
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.annotation.SuppressLint
//import android.text.TextWatcher
//import android.view.View.OnTouchListener
//import android.text.InputType
//import android.content.DialogInterface
//import android.text.Editable
//import android.text.TextUtils
//import android.widget.ArrayAdapter
//import android.widget.AdapterView
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
//import org.jeonfeel.moeuibit2.dtos.MyCoinsDTO
//import androidx.annotation.RequiresApi
//import android.os.Build
//import org.jeonfeel.moeuibit2.Fragment.investmentDetails.Fragment_investmentDetails.GetMyCoins
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvMyCoins
//import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
//import org.jeonfeel.moeuibit2.Fragment.investmentDetails.Fragment_investmentDetails.EarnKrw
//import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
//import com.google.android.gms.ads.initialization.InitializationStatus
//import org.jeonfeel.moeuibit2.Fragment.investmentDetails.CoinOrder
//import org.jeonfeel.moeuibit2.CheckNetwork
//import android.content.Intent
//import org.jeonfeel.moeuibit2.Activitys.Activity_portfolio
//import android.app.Activity
//import android.content.Context
//import android.graphics.Color
//import android.graphics.Paint
//import android.util.Log
//import android.view.View
//import org.jeonfeel.moeuibit2.view.activity.main.MainActivity
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
//import com.google.android.gms.ads.LoadAdError
//import com.google.android.gms.ads.OnUserEarnedRewardListener
//import com.google.android.gms.ads.rewarded.RewardItem
//import com.google.firebase.auth.FirebaseAuth
//import org.jeonfeel.moeuibit2.Activitys.Activity_Login
//import org.jeonfeel.moeuibit2.Fragment.Fragment_coinSite.SetLinears
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoin
//import org.jeonfeel.moeuibit2.DTOS.CoinDTO
//import org.jeonfeel.moeuibit2.Database.Favorite
//import org.jeonfeel.moeuibit2.view.fragment.Fragment_Exchange.GetUpBitCoinsThread
//import com.google.android.gms.ads.AdView
//import android.widget.CompoundButton
//import androidx.fragment.app.Fragment
//import com.github.mikephil.charting.data.*
//import com.github.mikephil.charting.formatter.ValueFormatter
//import java.io.*
//import java.lang.Exception
//import java.lang.IndexOutOfBoundsException
//import java.net.HttpURLConnection
//import java.net.URL
//import java.text.DecimalFormat
//import java.util.*
//
//class Fragment_chart : Fragment {
//    private var combinedChart: CombinedChart? = null
//    private var market: String? = null
//    private var candleEntries: ArrayList<CandleEntry>? = null
//    private var barEntries: ArrayList<BarEntry>? = null
//    private var candlePosition = 0
//    private var candleData: CandleData? = null
//    private var candleDataSet: CandleDataSet? = null
//    private var barData: BarData? = null
//    private var barDataSet: BarDataSet? = null
//    private var lineData: LineData? = null
//    private var finalCombinedData: CombinedData? = null
//    private var minute = 1
//    private var period = "minutes"
//    private var db: MoEuiBitDatabase? = null
//    private var ll2: LimitLine? = null
//    private val decimalFormat = DecimalFormat("###,###")
//    private var customLodingDialog: CustomLodingDialog? = null
//    private var context: Context? = null
//    private var getRecentCoinChart: GetRecentCoinChart? = null
//    private var rg_chart: RadioGroup? = null
//    private var rg_minuteGroup: RadioGroup? = null
//    private var radio_minuteChart: RadioButton? = null
//    private var radio_oneMinute: RadioButton? = null
//    private var sumLine1 = 0f
//    private var sumLine2 = 0f
//    private var sumLine3 = 0f
//    private var getMovingAverage: GetMovingAverage? = null
//
//    // TODO: Rename and change types of parameters
//    constructor() {}
//    constructor(market: String?) {
//        this.market = market
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val rootView = inflater.inflate(R.layout.fragment_chart, container, false)
//        context = activity
//        customLodingDialog = CustomLodingDialog(context!!)
//        customLodingDialog!!.show()
//        FindViewById(rootView)
//        db = getInstance(getContext()!!)
//        initCombinedChart()
//        chartDataSet()
//        radio_minuteChart!!.isChecked = true
//        radio_oneMinute!!.isChecked = true
//        getCoinCandleData(minute, period)
//        setRg_chart()
//        setRg_minuteGroup()
//        return rootView
//    }
//
//    private fun FindViewById(rootView: View) {
//        combinedChart = rootView.findViewById(R.id.combinedChart)
//        rg_minuteGroup = rootView.findViewById(R.id.rg_minuteGroup)
//        rg_chart = rootView.findViewById(R.id.rg_chart)
//        radio_oneMinute = rootView.findViewById(R.id.radio_oneMinute)
//        radio_minuteChart = rootView.findViewById(R.id.radio_minuteChart)
//    }
//
//    // 차트 초기화
//    private fun initCombinedChart() {
//        val moEuiBitMarkerView =
//            MoEuiBitMarkerView(context, R.layout.candle_info_marker, combinedChart)
//        combinedChart!!.description.isEnabled = false
//        combinedChart!!.isScaleYEnabled = false
//        combinedChart!!.setPinchZoom(false)
//        combinedChart!!.setDrawGridBackground(false)
//        combinedChart!!.setDrawBorders(false)
//        combinedChart!!.isDoubleTapToZoomEnabled = false
//        combinedChart!!.marker = moEuiBitMarkerView
//        combinedChart!!.isDragDecelerationEnabled = false
//        combinedChart!!.isDragEnabled = true
//        combinedChart!!.isHighlightPerDragEnabled = true
//        combinedChart!!.isHighlightPerTapEnabled = false
//        combinedChart!!.fitScreen()
//        combinedChart!!.isAutoScaleMinMaxEnabled = true
//        combinedChart!!.setBackgroundColor(Color.parseColor("#F2212121"))
//        combinedChart!!.onChartGestureListener = object : OnChartGestureListener {
//            //터치 하면 하이라이트 생성
//            override fun onChartGestureStart(me: MotionEvent, lastPerformedGesture: ChartGesture) {
//                try {
//                    val highlight = combinedChart!!.getHighlightByTouchPoint(me.x, me.y)
//                    if (highlight != null) {
//                        combinedChart!!.highlightValue(highlight, true)
//                    }
//                } catch (e: IndexOutOfBoundsException) {
//                    Log.d("오류11", "오류오류11")
//                    e.printStackTrace()
//                }
//            }
//
//            override fun onChartGestureEnd(me: MotionEvent, lastPerformedGesture: ChartGesture) {}
//            override fun onChartLongPressed(me: MotionEvent) {}
//            override fun onChartDoubleTapped(me: MotionEvent) {}
//            override fun onChartSingleTapped(me: MotionEvent) {}
//            override fun onChartFling(
//                me1: MotionEvent,
//                me2: MotionEvent,
//                velocityX: Float,
//                velocityY: Float
//            ) {
//            }
//
//            override fun onChartScale(me: MotionEvent, scaleX: Float, scaleY: Float) {}
//            override fun onChartTranslate(me: MotionEvent, dX: Float, dY: Float) {}
//        }
//        val xAxis = combinedChart!!.xAxis
//        xAxis.textColor = Color.parseColor("#FFFFFFFF")
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//        xAxis.setDrawGridLines(false)
//        xAxis.setAvoidFirstLastClipping(true)
//        xAxis.setLabelCount(3, true)
//        xAxis.setDrawLabels(true)
//        xAxis.granularity = 3f
//        xAxis.isGranularityEnabled = true
//        xAxis.axisLineColor = Color.parseColor("#FFFFFFFF")
//        val leftAxis = combinedChart!!.axisLeft
//        leftAxis.setDrawGridLines(false)
//        leftAxis.setDrawLabels(false)
//        leftAxis.axisMinimum = 0f
//        val rightAxis = combinedChart!!.axisRight
//        rightAxis.setLabelCount(5, true)
//        rightAxis.textColor = Color.WHITE
//        rightAxis.setDrawAxisLine(true)
//        rightAxis.setDrawGridLines(false)
//        rightAxis.axisLineColor = Color.WHITE
//        rightAxis.minWidth = 50f
//        val legendEntry1 = LegendEntry()
//        legendEntry1.label = "단순 MA"
//        val legendEntry2 = LegendEntry()
//        legendEntry2.label = "5"
//        legendEntry2.formColor = Color.GREEN
//        val legendEntry3 = LegendEntry()
//        legendEntry3.label = "20"
//        legendEntry3.formColor = Color.parseColor("#00D8FF")
//        val legendEntry4 = LegendEntry()
//        legendEntry4.label = "60"
//        legendEntry4.formColor = Color.RED
//        val l = combinedChart!!.legend
//        l.setCustom(arrayOf(legendEntry1, legendEntry2, legendEntry3, legendEntry4))
//        l.textColor = Color.parseColor("#FFFFFFFF")
//        l.isWordWrapEnabled = true
//        l.verticalAlignment = Legend.LegendVerticalAlignment.TOP
//        l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
//        l.orientation = Legend.LegendOrientation.HORIZONTAL
//        l.setDrawInside(true)
//        combinedChart!!.zoom(4f, 0f, 0f, 0f)
//    }
//
//    private fun chartDataSet() {
//        combinedChart!!.clear()
//        if (candleEntries == null) {
//            candleEntries = ArrayList()
//            barEntries = ArrayList()
//        }
//        candleDataSet = CandleDataSet(candleEntries, "")

//        candleDataSet!!.axisDependency = YAxis.AxisDependency.RIGHT
//        candleDataSet!!.shadowColor = Color.parseColor("#CCEAEAEA")
//        candleDataSet!!.shadowWidth = 1f
//        candleDataSet!!.decreasingColor = Color.parseColor("#CC6496FF")
//        candleDataSet!!.decreasingPaintStyle = Paint.Style.FILL
//        candleDataSet!!.increasingColor = Color.parseColor("#CCFF6464")
//        candleDataSet!!.increasingPaintStyle = Paint.Style.FILL
//        candleDataSet!!.neutralColor = Color.parseColor("#CCEAEAEA")
//        candleDataSet!!.setDrawValues(false)
//
////---------------------------------------------------------------------------------------------------------
//        barDataSet = BarDataSet(barEntries, "")
//        barDataSet!!.axisDependency = YAxis.AxisDependency.LEFT
//        barDataSet!!.setDrawValues(false)
//        barDataSet!!.color = Color.parseColor("#33FAF4C0")
//    }
//
//    //코인 정보 200개 받아오는 메소드
//    private fun getCoinCandleData(minute: Int, period: String) {
//        sumLine1 = 0.0f
//        sumLine2 = 0.0f
//        sumLine3 = 0.0f
//        if (candleEntries!!.size != 0) {
//            candleEntries!!.clear()
//            barEntries!!.clear()
//        }
//        if (candleData != null) {
//            candleData!!.clearValues()
//        } else {
//            candleData = CandleData()
//        }
//        if (barData != null) {
//            barData!!.clearValues()
//        } else {
//            barData = BarData()
//        }
//        if (lineData != null) {
//            lineData!!.clearValues()
//        } else {
//            lineData = LineData()
//        }
//        if (finalCombinedData != null) {
//            finalCombinedData!!.clearValues()
//        } else {
//            finalCombinedData = CombinedData()
//        }
//        if (coinCandleDataDTOS != null) {
//            coinCandleDataDTOS!!.clear()
//        } else {
//            coinCandleDataDTOS = ArrayList()
//        }
//        candlePosition = 0
//        val valueInfo = ArrayList<CoinCandleDataDTO>()
//        val coinUrl: String
//        coinUrl = if (minute == 2) {
//            "https://api.upbit.com/v1/candles/$period?market=$market&count=200"
//        } else {
//            "https://api.upbit.com/v1/candles/$period/$minute?market=$market&count=200"
//        }
//        val getUpBitCoins = GetUpBitCoins()
//        try {
//            val jsonArray: JSONArray?
//            jsonArray = getUpBitCoins.execute(coinUrl).get()
//            if (jsonArray != null) {
//                var jsonObject: JSONObject
//                for (i in jsonArray.length() - 1 downTo 0) {
//                    jsonObject = jsonArray[i] as JSONObject
//                    val candleDateTimeKst = jsonObject.getString("candle_date_time_kst")
//                    val openingPrice = jsonObject.getDouble("opening_price")
//                    val highPrice = jsonObject.getDouble("high_price")
//                    val lowPrice = jsonObject.getDouble("low_price")
//                    val tradePrice = jsonObject.getDouble("trade_price")
//                    val candleTransactionAmount = jsonObject.getDouble("candle_acc_trade_price")
//                    var openingPrice2: Float
//                    openingPrice2 = if (openingPrice < 100 && openingPrice >= 1) {
//                        String.format("%.2f", openingPrice).toFloat()
//                    } else if (openingPrice > 0 && openingPrice < 1) {
//                        String.format("%.4f", openingPrice).toFloat()
//                    } else {
//                        (Math.round(openingPrice * 100).toFloat() * 0.01).toFloat()
//                    }
//                    val highPrice2 = String.format("%.4f", highPrice).toFloat()
//                    val lowPrice2 = String.format("%.4f", lowPrice).toFloat()
//                    val tradePrice2 = String.format("%.4f", tradePrice).toFloat()
//                    val candleTransactionAmount2 = candleTransactionAmount.toString().toFloat()
//                    candleDataSet!!.addEntry(CandleEntry(candlePosition.toFloat(),
//                        highPrice2,
//                        lowPrice2,
//                        openingPrice2,
//                        tradePrice2))
//                    barDataSet!!.addEntry(BarEntry(candlePosition, candleTransactionAmount2))
//                    coinCandleDataDTOS!!.add(CoinCandleDataDTO(candleDateTimeKst,
//                        candleTransactionAmount))
//                    valueInfo.add(CoinCandleDataDTO(candleDateTimeKst, candleTransactionAmount))
//                    candlePosition++
//                }
//                Collections.sort(candleEntries, EntryXComparator())
//                Collections.sort(barEntries, EntryXComparator())
//                barDataSet!!.notifyDataSetChanged()
//                candleDataSet!!.notifyDataSetChanged()
//                candleData!!.addDataSet(candleDataSet)
//                //----------------------------------------------------------
//                barData!!.addDataSet(barDataSet)
//                val maxValue = barData!!.getYMax(YAxis.AxisDependency.LEFT)
//                //----------------------------------------------------------
//                getMovingAverage = GetMovingAverage(candleEntries)
//                lineData = getMovingAverage!!.createLineData()
//                sumLine1 = getMovingAverage.getSumLine1()
//                sumLine2 = getMovingAverage.getSumLine2()
//                sumLine3 = getMovingAverage.getSumLine3()
//                //------------------------------------------------------------
//                finalCombinedData!!.setData(barData)
//                finalCombinedData!!.setData(candleData)
//                finalCombinedData!!.setData(lineData)
//                combinedChart!!.clear()
//                combinedChart!!.data = finalCombinedData
//                val entryCount = combinedChart!!.candleData.entryCount
//                combinedChart!!.xAxis.axisMinimum = -0.7f
//                combinedChart!!.xAxis.axisMaximum = entryCount + 3f
//                val myCoin = db!!.myCoinDAO()!!.isInsert(market)
//                if (myCoin != null) {
//                    val average = myCoin.purchasePrice.toString()
//                    val averageResult = average.toFloat()
//                    val averageResultText: String
//                    averageResultText = if (averageResult >= 100) {
//                        decimalFormat.format(Math.round(averageResult).toLong())
//                    } else if (averageResult >= 1 && averageResult < 100) {
//                        String.format("%.2f", averageResult)
//                    } else {
//                        String.format("%.4f", averageResult)
//                    }
//                    val ll1 = LimitLine(averageResult, "매수평균($averageResultText)")
//                    ll1.lineWidth = 0f
//                    ll1.lineColor = Color.parseColor("#FFFFFFFF")
//                    ll1.textColor = Color.parseColor("#FFFFFFFF")
//                    ll1.enableDashedLine(10f, 1f, 0f)
//                    ll1.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
//                    ll1.textSize = 10f
//                    combinedChart!!.axisRight.removeAllLimitLines()
//                    combinedChart!!.axisRight.addLimitLine(ll1)
//                }
//                val myValueFormatter = myValueFormatter(valueInfo, 0)
//                combinedChart!!.xAxis.valueFormatter = myValueFormatter
//                combinedChart!!.notifyDataSetChanged()
//                combinedChart!!.axisLeft.axisMaximum = maxValue * 2
//                combinedChart!!.barData.isHighlightEnabled = false
//                combinedChart!!.moveViewToX(entryCount.toFloat())
//                if (customLodingDialog!!.isShowing && customLodingDialog != null) customLodingDialog!!.dismiss()
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//
//    // 시작할 때 코인 정보 받아오기
//    override fun onStart() {
//        super.onStart()
//        if (getRecentCoinChart == null) {
//            getRecentCoinChart = GetRecentCoinChart(minute, period)
//            getRecentCoinChart!!.start()
//        }
//    }
//
//    override fun onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
//        super.onPause()
//        if (getRecentCoinChart != null) {
//            getRecentCoinChart!!.stopThread()
//            getRecentCoinChart = null
//        }
//    }
//
//    private fun setRg_chart() {
//        rg_chart!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
//            when (radioGroup.checkedRadioButtonId) {
//                R.id.radio_minuteChart -> if (minute != 1) {
//                    minute = 1
//                    period = "minutes"
//                    setBtn(minute, period)
//                    rg_minuteGroup!!.visibility = View.VISIBLE
//                    radio_oneMinute!!.isChecked = true
//                    break
//                } else {
//                    Toast.makeText(context, "현재 1분봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_dailyChart -> if (period != "days") {
//                    minute = 2
//                    period = "days"
//                    setBtn(minute, period)
//                    rg_minuteGroup!!.visibility = View.INVISIBLE
//                    break
//                } else {
//                    Toast.makeText(context, "현재 일봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_weeklyChart -> if (period != "weeks") {
//                    minute = 2
//                    period = "weeks"
//                    setBtn(minute, period)
//                    rg_minuteGroup!!.visibility = View.INVISIBLE
//                    break
//                } else {
//                    Toast.makeText(context, "현재 주봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_monthlyChart -> if (period != "months") {
//                    minute = 2
//                    period = "months"
//                    setBtn(minute, period)
//                    rg_minuteGroup!!.visibility = View.INVISIBLE
//                    break
//                } else {
//                    Toast.makeText(context, "현재 월봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//            }
//        })
//    }
//
//    fun setRg_minuteGroup() {
//        rg_minuteGroup!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { radioGroup, i ->
//            when (radioGroup.checkedRadioButtonId) {
//                R.id.radio_oneMinute -> if (minute != 1) {
//                    minute = 1
//                    period = "minutes"
//                    setBtn(minute, period)
//                    break
//                } else {
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_threeMinute -> if (minute != 3) {
//                    minute = 3
//                    period = "minutes"
//                    setBtn(minute, period)
//                    break
//                } else {
//                    Toast.makeText(context, "현재 3분봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_fiveMinute -> if (minute != 5) {
//                    minute = 5
//                    period = "minutes"
//                    setBtn(minute, period)
//                    break
//                } else {
//                    Toast.makeText(context, "현재 5분봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_tenMinute -> if (minute != 10) {
//                    minute = 10
//                    period = "minutes"
//                    setBtn(minute, period)
//                    break
//                } else {
//                    Toast.makeText(context, "현재 10분봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_fifteenMinute -> if (minute != 15) {
//                    minute = 15
//                    period = "minutes"
//                    setBtn(minute, period)
//                    break
//                } else {
//                    Toast.makeText(context, "현재 15분봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_thirtyMinute -> if (minute != 30) {
//                    minute = 30
//                    period = "minutes"
//                    setBtn(minute, period)
//                    break
//                } else {
//                    Toast.makeText(context, "현재 30분봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_hour -> if (minute != 60) {
//                    minute = 60
//                    period = "minutes"
//                    setBtn(minute, period)
//                    break
//                } else {
//                    Toast.makeText(context, "현재 60분봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//                R.id.radio_fourHour -> if (minute != 240) {
//                    minute = 240
//                    period = "minutes"
//                    setBtn(minute, period)
//                    break
//                } else {
//                    Toast.makeText(context, "현재 240분봉 입니다.", Toast.LENGTH_SHORT).show()
//                    return@OnCheckedChangeListener
//                }
//            }
//        })
//    }
//
//    private fun setBtn(minute: Int, period: String) {
//        if (getRecentCoinChart != null) {
//            getRecentCoinChart!!.stopThread()
//            getRecentCoinChart = null
//            getCoinCandleData(minute, period)
//            getRecentCoinChart = GetRecentCoinChart(minute, period)
//            getRecentCoinChart!!.start()
//        }
//    }
//
//    internal inner class GetRecentCoinChart(private val minute: Int, private val period: String) :
//        Thread() {
//        private var isRunning = true
//        override fun run() {
//            super.run()
//            while (isRunning) {
//                try {
//                    var coinUrl = ""
//                    if (minute != 0 && minute != 2) {
//                        coinUrl =
//                            "https://api.upbit.com/v1/candles/$period/$minute?market=$market&count=1"
//                    } else if (minute == 2) {
//                        coinUrl = "https://api.upbit.com/v1/candles/$period?market=$market&count=1"
//                    }
//                    val url = URL(coinUrl)
//                    val conn = url.openConnection() as HttpURLConnection
//                    val inputStream: InputStream = BufferedInputStream(conn.inputStream)
//                    val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
//                    val builder = StringBuffer()
//                    var inputString: String?
//                    while (bufferedReader.readLine().also { inputString = it } != null) {
//                        builder.append(inputString)
//                    }
//                    val s = builder.toString()
//                    var jsonRecentCoinInfo: JSONArray? = null
//                    jsonRecentCoinInfo = JSONArray(s)
//                    conn.disconnect()
//                    bufferedReader.close()
//                    inputStream.close()
//                    try {
//                        var candleDateTimeKst = ""
//                        if (jsonRecentCoinInfo != null) {
//                            val jsonObject = jsonRecentCoinInfo[0] as JSONObject
//                            candleDateTimeKst = jsonObject.getString("candle_date_time_kst")
//                            val openingPrice = jsonObject.getDouble("opening_price")
//                            val highPrice = jsonObject.getDouble("high_price")
//                            val lowPrice = jsonObject.getDouble("low_price")
//                            val tradePrice = jsonObject.getDouble("trade_price")
//                            val candleTransactionAmount =
//                                jsonObject.getDouble("candle_acc_trade_price")
//                            var openingPrice2: Float
//                            openingPrice2 = if (openingPrice < 100 && openingPrice >= 1) {
//                                String.format("%.2f", openingPrice).toFloat()
//                            } else if (openingPrice > 0 && openingPrice < 1) {
//                                String.format("%.4f", openingPrice).toFloat()
//                            } else {
//                                (Math.round(openingPrice * 100)
//                                    .toFloat() * 0.01).toFloat()
//                            }
//                            val highPrice2 = String.format("%.4f", highPrice).toFloat()
//                            val lowPrice2 = String.format("%.4f", lowPrice).toFloat()
//                            val tradePrice2 = String.format("%.4f", tradePrice).toFloat()
//                            val candleTransactionAmount2 =
//                                candleTransactionAmount.toString().toFloat()
//                            val candleSize = candleEntries!!.size
//                            if (coinCandleDataDTOS!!.size != 0 && candleEntries!!.size != 0 && coinCandleDataDTOS!![candleSize - 1].candleDateTimeKst == candleDateTimeKst) {
//                                candleEntries!![candlePosition - 1] =
//                                    CandleEntry((candlePosition - 1).toFloat(),
//                                        highPrice2,
//                                        lowPrice2,
//                                        openingPrice2,
//                                        tradePrice2)
//                                barEntries!![candlePosition - 1] =
//                                    BarEntry(candlePosition - 1, candleTransactionAmount2)
//                                lineData!!.removeEntry((candlePosition - 1).toFloat(), 0)
//                                lineData!!.addEntry(Entry((candlePosition - 1).toFloat(),
//                                    (sumLine1 + tradePrice2) / 5), 0)
//                                lineData!!.removeEntry((candlePosition - 1).toFloat(), 1)
//                                lineData!!.addEntry(Entry((candlePosition - 1).toFloat(),
//                                    (sumLine2 + tradePrice2) / 20), 1)
//                                lineData!!.removeEntry((candlePosition - 1).toFloat(), 2)
//                                lineData!!.addEntry(Entry((candlePosition - 1).toFloat(),
//                                    (sumLine3 + tradePrice2) / 60), 2)
//                                coinCandleDataDTOS!![candleSize - 1] =
//                                    CoinCandleDataDTO(candleDateTimeKst, candleTransactionAmount)
//                            } else if (coinCandleDataDTOS!!.size != 0 && candleEntries!!.size != 0 && coinCandleDataDTOS!![candleSize - 1].candleDateTimeKst != candleDateTimeKst) {
//                                candleEntries!!.add(CandleEntry(candlePosition.toFloat(),
//                                    highPrice2,
//                                    lowPrice2,
//                                    openingPrice2,
//                                    tradePrice2))
//                                barEntries!!.add(BarEntry(candlePosition, candleTransactionAmount2))
//                                sumLine1 += tradePrice2
//                                lineData!!.addEntry(Entry(
//                                    candlePosition.toFloat(), sumLine1 / 5), 0)
//                                sumLine1 -= candleEntries!![candlePosition - 5].close
//                                sumLine2 += tradePrice2
//                                lineData!!.addEntry(Entry(
//                                    candlePosition.toFloat(), sumLine2 / 20), 1)
//                                sumLine2 -= candleEntries!![candlePosition - 20].close
//                                sumLine3 += tradePrice2
//                                lineData!!.addEntry(Entry(
//                                    candlePosition.toFloat(), sumLine3 / 60), 2)
//                                sumLine3 -= candleEntries!![candlePosition - 60].close
//                                coinCandleDataDTOS!!.add(CoinCandleDataDTO(candleDateTimeKst,
//                                    candleTransactionAmount))
//                                candlePosition++
//                                combinedChart!!.xAxis.axisMaximum = combinedChart!!.xChartMax + 1f
//                            }
//                            combinedChart!!.axisRight.removeLimitLine(ll2)
//                            ll2 = if (tradePrice2 >= 100) {
//                                LimitLine(tradePrice2, decimalFormat.format(tradePrice2.toDouble()))
//                            } else if (tradePrice2 >= 1 && tradePrice2 < 100) {
//                                LimitLine(tradePrice2, String.format("%.2f", tradePrice2))
//                            } else {
//                                LimitLine(tradePrice2, String.format("%.4f", tradePrice2))
//                            }
//                            (context as Activity_coinDetails?)!!.runOnUiThread {
//                                ll2!!.lineWidth = 0f
//                                ll2!!.enableDashedLine(1f, 1f, 0f)
//                                ll2!!.lineColor = Color.parseColor("#F2212121")
//                                ll2!!.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
//                                ll2!!.textSize = 10f
//                                ll2!!.textColor = Color.parseColor("#FFFFFFFF")
//                                combinedChart!!.axisRight.addLimitLine(ll2)
//                                lineData!!.notifyDataChanged()
//                                combinedChart!!.notifyDataSetChanged()
//                                combinedChart!!.invalidate()
//                            }
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
//                    } catch (ex: NegativeArraySizeException) {
//                        ex.printStackTrace()
//                    }
//                } catch (e: UnsupportedEncodingException) {
//                    e.printStackTrace()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                } catch (e: JSONException) {
//                    e.printStackTrace()
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//                try {
//                    sleep(1000)
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
//        }
//
//        fun stopThread() {
//            isRunning = false
//        }
//    }
//
//    //차트 XAxis value formatter
//    inner class myValueFormatter(var mValue: ArrayList<CoinCandleDataDTO>, var entries: Int) :
//        ValueFormatter() {
//        var size: Int
//        override fun getFormattedValue(value: Float): String {
//            if (value as Int<0 || value.toInt() >= size) {
//                return ""
//            } else if (value as Int<size) {
//                val fullyDate = mValue[value.toInt()].candleDateTimeKst.split("T").toTypedArray()
//                val date = fullyDate[0].split("-").toTypedArray()
//                val time = fullyDate[1].split(":").toTypedArray()
//                return date[1] + "-" + date[2] + " " + time[0] + ":" + time[1]
//            }
//            return ""
//        }
//
//        init {
//            size = mValue.size
//        }
//    }
//
//    companion object {
//        private var coinCandleDataDTOS: ArrayList<CoinCandleDataDTO>? = null
//        fun getCoinCandle(position: Int): CoinCandleDataDTO {
//            return coinCandleDataDTOS!![position]
//        }
//    }
//}