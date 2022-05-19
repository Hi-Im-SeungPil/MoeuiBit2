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
//import android.graphics.Canvas
//import android.graphics.Color
//import android.graphics.Paint
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
//import com.github.mikephil.charting.data.*
//import com.github.mikephil.charting.highlight.Highlight
//import java.lang.Exception
//import java.text.DecimalFormat
//
//class MoEuiBitMarkerView(
//    var context: Context?,
//    layoutResource: Int,
//    var combinedChart: CombinedChart?
//) : MarkerView(
//    context, layoutResource) {
//    var tv_candleTime: TextView
//    var tv_startPrice: TextView
//    var tv_highPrice: TextView
//    var tv_lowPrice: TextView
//    var tv_endPrice: TextView
//    var tv_markerTransactionAmount: TextView
//    var tv_startPricePercent: TextView
//    var tv_highPricePercent: TextView
//    var tv_lowPricePercent: TextView
//    var tv_endPricePercent: TextView
//    var kst: String? = null
//    var fullyDate: Array<String>
//    var date: Array<String>?
//    var time: Array<String>?
//    var coinCandleDataDTO: CoinCandleDataDTO? = null
//    private val decimalFormat = DecimalFormat("###,###")
//
//    //마커뷰 세팅
//    override fun refreshContent(e: Entry, highlight: Highlight) {
//        try {
//            if (e is CandleEntry) {
//                val ce = e
//                coinCandleDataDTO = Fragment_chart.Companion.getCoinCandle(ce.x.toInt())
//                if (coinCandleDataDTO != null) {
//                    kst = coinCandleDataDTO!!.candleDateTimeKst
//                    fullyDate = kst!!.split("T").toTypedArray()
//                    date = fullyDate[0].split("-").toTypedArray()
//                    time = fullyDate[1].split(":").toTypedArray()
//                }
//                val openPrice = ce.open
//                val highPrice = ce.high
//                val lowPrice = ce.low
//                val closePrice = ce.close
//                val openPricePercent = 0f
//                val highPricePercent = (highPrice - openPrice) / openPrice * 100
//                val lowPricePercent = (lowPrice - openPrice) / openPrice * 100
//                val endPricePercent = (closePrice - openPrice) / openPrice * 100
//                if (endPricePercent > 0) {
//                    tv_endPrice.setTextColor(Color.parseColor("#B77300"))
//                    tv_endPricePercent.setTextColor(Color.parseColor("#B77300"))
//                } else if (endPricePercent < 0) {
//                    tv_endPrice.setTextColor(Color.parseColor("#0054FF"))
//                    tv_endPricePercent.setTextColor(Color.parseColor("#0054FF"))
//                } else {
//                    tv_endPrice.setTextColor(Color.parseColor("#000000"))
//                    tv_endPricePercent.setTextColor(Color.parseColor("#000000"))
//                }
//                if (lowPricePercent > 0) {
//                    tv_lowPrice.setTextColor(Color.parseColor("#B77300"))
//                    tv_lowPricePercent.setTextColor(Color.parseColor("#B77300"))
//                } else if (lowPricePercent < 0) {
//                    tv_lowPrice.setTextColor(Color.parseColor("#0054FF"))
//                    tv_lowPricePercent.setTextColor(Color.parseColor("#0054FF"))
//                } else {
//                    tv_lowPrice.setTextColor(Color.parseColor("#000000"))
//                    tv_lowPricePercent.setTextColor(Color.parseColor("#000000"))
//                }
//                if (highPricePercent > 0) {
//                    tv_highPrice.setTextColor(Color.parseColor("#B77300"))
//                    tv_highPricePercent.setTextColor(Color.parseColor("#B77300"))
//                } else if (highPricePercent < 0) {
//                    tv_highPrice.setTextColor(Color.parseColor("#0054FF"))
//                    tv_highPricePercent.setTextColor(Color.parseColor("#0054FF"))
//                } else {
//                    tv_highPrice.setTextColor(Color.parseColor("#000000"))
//                    tv_highPricePercent.setTextColor(Color.parseColor("#000000"))
//                }
//                tv_candleTime.text = date!![1] + "-" + date!![2] + " " + time!![0] + ":" + time!![1]
//                if (openPrice >= 100 || openPrice <= -100) {
//                    tv_startPrice.text = decimalFormat.format(Math.round(openPrice).toLong())
//                } else if (openPrice >= 1 && openPrice < 100) {
//                    tv_startPrice.text = String.format(String.format("%.2f", openPrice))
//                } else if (openPrice <= -1 && openPrice > -100) {
//                    tv_startPrice.text = String.format(String.format("%.2f", openPrice))
//                } else if (openPrice > 0 && openPrice < 1) {
//                    tv_startPrice.text = String.format(String.format("%.4f", openPrice))
//                } else if (openPrice < 0 && openPrice > -1) {
//                    tv_startPrice.text = String.format(String.format("%.4f", openPrice))
//                }
//                if (highPrice >= 100 || highPrice <= -100) {
//                    tv_highPrice.text = decimalFormat.format(Math.round(highPrice).toLong())
//                } else if (highPrice >= 1 && highPrice < 100) {
//                    tv_highPrice.text = String.format(String.format("%.2f", highPrice))
//                } else if (highPrice <= -1 && highPrice > -100) {
//                    tv_highPrice.text = String.format(String.format("%.2f", highPrice))
//                } else if (highPrice > 0 && highPrice < 1) {
//                    tv_highPrice.text = String.format(String.format("%.4f", highPrice))
//                } else if (highPrice < 0 && highPrice > -1) {
//                    tv_startPrice.text = String.format(String.format("%.4f", highPrice))
//                }
//                if (lowPrice >= 100 || lowPrice <= -100) {
//                    tv_lowPrice.text = decimalFormat.format(Math.round(lowPrice).toLong())
//                } else if (lowPrice >= 1 && lowPrice < 100) {
//                    tv_lowPrice.text = String.format(String.format("%.2f", lowPrice))
//                } else if (lowPrice <= -1 && lowPrice > -100) {
//                    tv_lowPrice.text = String.format(String.format("%.2f", lowPrice))
//                } else if (lowPrice > 0 && lowPrice < 1) {
//                    tv_lowPrice.text = String.format(String.format("%.4f", lowPrice))
//                } else if (lowPrice < 0 && lowPrice > -1) {
//                    tv_lowPrice.text = String.format(String.format("%.4f", lowPrice))
//                }
//                if (closePrice >= 100 || closePrice <= -100) {
//                    tv_endPrice.text = decimalFormat.format(Math.round(closePrice).toLong())
//                } else if (closePrice >= 1 && closePrice < 100) {
//                    tv_endPrice.text = String.format(String.format("%.2f", closePrice))
//                } else if (closePrice <= -1 && closePrice > -100) {
//                    tv_endPrice.text = String.format(String.format("%.2f", closePrice))
//                } else if (closePrice > 0 && closePrice < 1) {
//                    tv_endPrice.text = String.format(String.format("%.4f", closePrice))
//                } else if (closePrice < 0 && closePrice > -1) {
//                    tv_endPrice.text = String.format(String.format("%.4f", closePrice))
//                }
//                tv_highPricePercent.text = String.format("%.2f", highPricePercent) + "%"
//                tv_startPricePercent.text = String.format("%.2f", openPricePercent) + "%"
//                tv_lowPricePercent.text = String.format("%.2f", lowPricePercent) + "%"
//                tv_endPricePercent.text = String.format("%.2f", endPricePercent) + "%"
//                if (coinCandleDataDTO != null) {
//                    val transactionAmount = coinCandleDataDTO!!.candleTransactionAmount * 0.000001
//                    if (transactionAmount >= 1) tv_markerTransactionAmount.text =
//                        decimalFormat.format(
//                            Math.round(transactionAmount)) else if (transactionAmount < 1) {
//                        tv_markerTransactionAmount.text = String.format("%.2f", transactionAmount)
//                    }
//                }
//            }
//            super.refreshContent(e, highlight)
//        } catch (ee: Exception) {
//            ee.printStackTrace()
//            Toast.makeText(context, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
//            (context as Activity_coinDetails?)!!.finish()
//        }
//    }
//
//    //마커뷰 그림.
//    override fun draw(canvas: Canvas, posX: Float, posY: Float) {
//        val paint = Paint()
//        paint.color = Color.WHITE
//        paint.textSize = 35f
//        val paint2 = Paint()
//        paint2.color = Color.parseColor("#F361A6")
//        val paint3 = Paint()
//        paint3.color = Color.BLACK
//        paint3.textSize = 35f
//        val textSize = paint.textSize
//        val length = paint.measureText("08-20 13:18") + 10f
//        canvas.drawRect(posX - 10f,
//            canvas.height - textSize - 5f,
//            posX + length,
//            canvas.height + textSize + 5f,
//            paint2)
//        if (date != null && time != null) canvas.drawText(date!![1] + "-" + date!![2] + " " + time!![0] + ":" + time!![1],
//            posX,
//            canvas.height - 5f,
//            paint)
//        if (posX > canvas.width / 2.0) {
//            getOffsetForDrawingAtPoint(posX, posY)
//            super.draw(canvas)
//        } else {
//            super.draw(canvas, (canvas.width / 5 * 3).toFloat(), -posY as Int.toFloat())
//        }
//    }
//
//    init {
//        tv_candleTime = findViewById(R.id.tv_candleTime)
//        tv_startPrice = findViewById(R.id.tv_startPrice)
//        tv_highPrice = findViewById(R.id.tv_highPrice)
//        tv_lowPrice = findViewById(R.id.tv_lowPrice)
//        tv_endPrice = findViewById(R.id.tv_endPrice)
//        tv_markerTransactionAmount = findViewById(R.id.tv_markerTransactionAmount)
//        tv_startPricePercent = findViewById(R.id.tv_startPricePercent)
//        tv_highPricePercent = findViewById(R.id.tv_highPricePercent)
//        tv_lowPricePercent = findViewById(R.id.tv_lowPricePercent)
//        tv_endPricePercent = findViewById(R.id.tv_endPricePercent)
//    }
//}