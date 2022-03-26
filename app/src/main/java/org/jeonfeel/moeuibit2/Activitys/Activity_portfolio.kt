//package org.jeonfeel.moeuibit2.Activitys
//
//import androidx.lifecycle.ViewModelProvider.get
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import android.os.Bundle
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import org.jeonfeel.moeuibit2.R
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.firebase.auth.FirebaseUser
//import androidx.activity.result.ActivityResultLauncher
//import android.content.Intent
//import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
//import androidx.activity.result.ActivityResultCallback
//import android.app.Activity
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.common.api.ApiException
//import com.google.firebase.auth.AuthCredential
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.auth.AuthResult
//import android.widget.Toast
//import org.jeonfeel.moeuibit2.view.activity.main.MainActivity
//import androidx.core.app.ActivityCompat
//import android.content.pm.PackageManager
//import org.jeonfeel.moeuibit2.Activitys.Activity_Login
//import com.google.gson.annotations.SerializedName
//import androidx.fragment.app.FragmentActivity
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.coinDetailsViewModel
//import org.jeonfeel.moeuibit2.getUpBitAPI.Retrofit_UpBit
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.SelectedCoinModel
//import androidx.lifecycle.ViewModelProvider
//import org.jeonfeel.moeuibit2.CheckNetwork
//import org.jeonfeel.moeuibit2.Fragment.coinOrder.Fragment_coinOrder
//import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart
//import org.jeonfeel.moeuibit2.Fragment.Fragment_coinInfo
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
//import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
//import com.google.android.gms.ads.initialization.InitializationStatus
//import android.app.Application
//import android.content.res.Resources
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.MutableLiveData
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase
//import com.bumptech.glide.Glide
//import org.jeonfeel.moeuibit2.Database.Favorite
//import android.os.Looper
//import com.github.mikephil.charting.charts.PieChart
//import android.widget.LinearLayout
//import org.jeonfeel.moeuibit2.Database.MyCoin
//import android.content.res.TypedArray
//import android.graphics.Color
//import android.view.View
//import android.widget.Button
//import com.github.mikephil.charting.data.PieEntry
//import com.github.mikephil.charting.data.PieDataSet
//import com.github.mikephil.charting.data.PieData
//import android.widget.TextView
//import androidx.appcompat.app.ActionBar
//import org.jeonfeel.moeuibit2.Database.User
//import java.util.ArrayList
//
//class Activity_portfolio constructor() : AppCompatActivity() {
//    private var pieChart: PieChart? = null
//    private var linear_pieChartContent: LinearLayout? = null
//    private var btn_portfolioBackSpace: Button? = null
//    private var db: MoEuiBitDatabase? = null
//    private var myCoinList: List<MyCoin>? = null
//    private var coinTotalPrice: ArrayList<Long>? = null
//    private var coinSymbol: ArrayList<String>? = null
//    private var coinKoreanName: ArrayList<String>? = null
//    private var colors: IntArray
//    var coinAmount: Long = 0
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_portfolio)
//        val actionBar: ActionBar? = getSupportActionBar()
//        actionBar!!.hide()
//        FindViewById()
//        db = MoEuiBitDatabase.getInstance(this@Activity_portfolio)
//        myCoins
//        setPieChart()
//        insertTextView()
//        btn_portfolioBackSpace!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                finish()
//            }
//        })
//    }
//
//    private fun FindViewById() {
//        pieChart = findViewById(R.id.pieChart)
//        linear_pieChartContent = findViewById(R.id.linear_pieChartContent)
//        btn_portfolioBackSpace = findViewById(R.id.btn_portfolioBackSpace)
//    }
//
//    private val myCoins: Unit
//        private get() {
//            coinTotalPrice = ArrayList()
//            coinSymbol = ArrayList()
//            coinKoreanName = ArrayList()
//            myCoinList = db!!.myCoinDAO().all
//            val user: User? = db!!.userDAO().all
//            if (user != null) {
//                if (user.krw != 0L) {
//                    val krw: Long = user.krw
//                    coinTotalPrice!!.add(krw)
//                    coinSymbol!!.add("KRW")
//                    coinKoreanName!!.add("원화")
//                    coinAmount += krw
//                }
//            }
//            for (i in myCoinList.indices) {
//                val coinPrice: Long = Math.round(myCoinList.get(i).quantity * myCoinList.get(i)
//                    .purchasePrice)
//                coinTotalPrice!!.add(coinPrice)
//                coinSymbol!!.add(myCoinList.get(i).symbol)
//                coinKoreanName!!.add(myCoinList.get(i).koreanCoinName)
//                coinAmount += coinPrice
//            }
//        }
//
//    private fun setPieChart() {
//        var position: Int = 0
//        val pieChart_color: Resources = getResources()
//        val color_Array: TypedArray = pieChart_color.obtainTypedArray(R.array.pieChart_color)
//        colors = IntArray(coinTotalPrice!!.size)
//        for (i in coinTotalPrice!!.indices) {
//            colors.get(i) = color_Array.getColor(position, 0)
//            position++
//            if (position >= 42) {
//                position = 0
//            }
//        }
//        val data: ArrayList<PieEntry> = ArrayList()
//        for (i in coinTotalPrice!!.indices) {
//            data.add(PieEntry(coinTotalPrice!!.get(i).toFloat()))
//        }
//        val pieDataSet: PieDataSet = PieDataSet(data, "")
//        pieDataSet.setColors(*colors)
//        pieDataSet.setValueTextColor(Color.parseColor("#FFFFFFFF"))
//        val pieData: PieData = PieData(pieDataSet)
//        pieChart!!.setDrawEntryLabels(true)
//        pieChart!!.setUsePercentValues(true)
//        pieChart!!.setHighlightPerTapEnabled(false)
//        pieData.setValueTextSize(12f)
//        pieChart!!.setCenterText("보유 현황 %")
//        pieChart!!.setCenterTextSize(15f)
//        pieChart!!.getLegend().setEnabled(false)
//        pieChart!!.setData(pieData)
//        pieChart!!.invalidate()
//    }
//
//    private fun insertTextView() {
//        for (i in coinTotalPrice!!.indices) {
//            val coinPercent: String =
//                String.format("%.1f", (coinTotalPrice!!.get(i) / coinAmount.toFloat()) * 100)
//            val textView: TextView = TextView(this@Activity_portfolio)
//            textView.setText("★ " + coinKoreanName!!.get(i) + " (" + coinSymbol!!.get(i) + ") " + coinPercent + "%")
//            textView.setTextColor(colors.get(i))
//            textView.setTextSize(17f)
//            linear_pieChartContent!!.addView(textView)
//        }
//    }
//}