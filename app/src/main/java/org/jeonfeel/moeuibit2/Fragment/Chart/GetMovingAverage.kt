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
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO.myCoinsSymbol
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO.setCurrentPrice
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
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO
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
//import android.graphics.Color
//import org.jeonfeel.moeuibit2.view.activity.MainActivity
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
//import java.util.ArrayList
//
//class GetMovingAverage(private val candleEntries: ArrayList<CandleEntry>?) {
//    private var count = 0
//    var sumLine1 = 0f
//        private set
//    var sumLine2 = 0f
//        private set
//    var sumLine3 = 0f
//        private set
//    private var line1Entries = ArrayList<Entry>()
//    private var line2Entries = ArrayList<Entry>()
//    private var line3Entries = ArrayList<Entry>()
//    fun createLineData(): LineData {
//        val lineData = LineData()
//        val Line1 = 5
//        val Line2 = 20
//        val Line3 = 60
//        line1Entries = ArrayList()
//        line2Entries = ArrayList()
//        line3Entries = ArrayList()
//        sumLine1 = 0.0f
//        sumLine2 = 0.0f
//        sumLine3 = 0.0f
//        for (i in candleEntries!!.indices) {
//            count++
//            sumLine1 += candleEntries[i].close
//            sumLine2 += candleEntries[i].close
//            sumLine3 += candleEntries[i].close
//            if (count >= Line1) {
//                line1Entries.add(Entry((count - 1).toFloat(), sumLine1 / Line1))
//                sumLine1 -= candleEntries[count - 1 - (Line1 - 1)].close
//            }
//            if (count >= Line2) {
//                line2Entries.add(Entry((count - 1).toFloat(), sumLine2 / Line2))
//                sumLine2 -= candleEntries[count - 1 - (Line2 - 1)].close
//            }
//            if (count >= Line3) {
//                line3Entries.add(Entry((count - 1).toFloat(), sumLine3 / Line3))
//                sumLine3 -= candleEntries[count - 1 - (Line3 - 1)].close
//            }
//        }
//        val lineDataSet1 = LineDataSet(line1Entries, "")
//        lineDataSet1.setDrawCircles(false)
//        lineDataSet1.color = Color.GREEN
//        lineDataSet1.lineWidth = 1f
//        lineDataSet1.axisDependency = YAxis.AxisDependency.RIGHT
//        lineDataSet1.isHighlightEnabled = false
//        lineDataSet1.valueTextSize = 0f
//        val lineDataSet2 = LineDataSet(line2Entries, "")
//        lineDataSet2.setDrawCircles(false)
//        lineDataSet2.color = Color.parseColor("#00D8FF")
//        lineDataSet2.lineWidth = 1f
//        lineDataSet2.axisDependency = YAxis.AxisDependency.RIGHT
//        lineDataSet2.isHighlightEnabled = false
//        lineDataSet2.valueTextSize = 0f
//        val lineDataSet3 = LineDataSet(line3Entries, "")
//        lineDataSet3.setDrawCircles(false)
//        lineDataSet3.color = Color.RED
//        lineDataSet3.lineWidth = 1f
//        lineDataSet3.axisDependency = YAxis.AxisDependency.RIGHT
//        lineDataSet3.isHighlightEnabled = false
//        lineDataSet3.valueTextSize = 0f
//        lineData.addDataSet(lineDataSet1)
//        lineData.addDataSet(lineDataSet2)
//        lineData.addDataSet(lineDataSet3)
//        return lineData
//    }
//}