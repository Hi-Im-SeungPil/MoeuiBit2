//package org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails
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
//import android.app.Fragment
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
//import android.view.View
//import com.github.mikephil.charting.data.PieEntry
//import com.github.mikephil.charting.data.PieDataSet
//import com.github.mikephil.charting.data.PieData
//import android.widget.TextView
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.Observer
//import com.google.android.gms.ads.AdRequest
//import org.jeonfeel.moeuibit2.databinding.ActivityCoinDetailsBinding
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import java.util.*
//
//class Activity_coinDetails() : FragmentActivity(), View.OnClickListener {
//    private val TAG = "Activity_coinInfo"
//    private var binding: ActivityCoinDetailsBinding? = null
//    private var coinDetailsViewModel: coinDetailsViewModel? = null
//    private var market: String? = null
//    private var retrofit: Retrofit_UpBit? = null
//    private var call: Call<List<SelectedCoinModel>>? = null
//    private var koreanName: String? = null
//    private var symbol: String? = null
//    private var timer: Timer? = null
//    private var TT: TimerTask? = null
//    var globalCurrentPrice: Double? = null
//        private set
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityCoinDetailsBinding.inflate(
//            layoutInflater)
//        setContentView(binding!!.root)
//        coinDetailsViewModel = ViewModelProvider(this).get<coinDetailsViewModel>(
//            coinDetailsViewModel::class.java)
//        init()
//        initAds()
//        coinDetailsViewModel!!.selectedCoinModelLiveData.observe(this,
//            Observer { globalCurrentPrice = coinDetailsViewModel!!.updateCoinDetails(binding!!) })
//        binding!!.btnBookMark.setOnClickListener(this)
//        binding!!.btnCoinInfoBackSpace.setOnClickListener(this)
//        setTabLayout()
//    }
//
//    private fun init() {
//        retrofit = Retrofit_UpBit()
//        val intent = intent
//        koreanName = intent.getStringExtra("koreanName")
//        symbol = intent.getStringExtra("symbol")
//        call = retrofit!!.getSelectedCoinCall("KRW-$symbol")
//        if (CheckNetwork.CheckNetwork(this) == 0) {
//            Toast.makeText(this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show()
//            finish()
//        } else {
//            call.enqueue(object : Callback<List<SelectedCoinModel?>> {
//                override fun onResponse(
//                    call: Call<List<SelectedCoinModel?>>,
//                    response: Response<List<SelectedCoinModel?>>
//                ) {
//                    if (response.isSuccessful) {
//                        val selectedCoinModels = (response.body())!!
//                        coinDetailsViewModel!!.selectedCoinModelLiveData.setValue(selectedCoinModels[0])
//                        globalCurrentPrice =
//                            coinDetailsViewModel!!.initCoinDetails(binding, koreanName, symbol)
//                    } else {
//                        Toast.makeText(this@Activity_coinDetails,
//                            "네트워크 상태를 확인해 주세요.",
//                            Toast.LENGTH_SHORT).show()
//                        finish()
//                    }
//                }
//
//                override fun onFailure(call: Call<List<SelectedCoinModel?>>, t: Throwable) {
//                    t.printStackTrace()
//                    Toast.makeText(this@Activity_coinDetails,
//                        "네트워크 상태를 확인해 주세요.",
//                        Toast.LENGTH_SHORT).show()
//                    finish()
//                }
//            })
//        }
//    }
//
//    private fun setTabLayout() {
//        val intent = intent
//        market = intent.getStringExtra("market")
//        val fragment_coinOrder = Fragment_coinOrder(market, koreanName, symbol)
//        val fragment_chart = Fragment_chart(market)
//        val fragment_coinInfo = Fragment_coinInfo(market)
//        val tab_coinInfo: TabLayout = findViewById(R.id.tab_coinInfo)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.coinInfo_fragment_container, fragment_coinOrder).commit()
//        tab_coinInfo.addOnTabSelectedListener(object : OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                val position = tab.position
//                var selected: Fragment? = null
//                val networkStatus = CheckNetwork.(this@Activity_coinDetails)
//                if (position == 0) {
//                    if (networkStatus != 0) {
//                        selected = fragment_coinOrder
//                    } else {
//                        Toast.makeText(this@Activity_coinDetails,
//                            "네트워크 상태를 확인해 주세요.",
//                            Toast.LENGTH_SHORT).show()
//                        return
//                    }
//                } else if (position == 1) {
//                    if (networkStatus != 0) {
//                        selected = fragment_chart
//                    } else {
//                        Toast.makeText(this@Activity_coinDetails,
//                            "네트워크 상태를 확인해 주세요.",
//                            Toast.LENGTH_SHORT).show()
//                        return
//                    }
//                } else if (position == 2) {
//                    selected = fragment_coinInfo
//                }
//                supportFragmentManager.beginTransaction()
//                    .replace(R.id.coinInfo_fragment_container, (selected)!!).commit()
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
//    }
//
//    override fun onStart() {
//        super.onStart()
//        if (timer == null && TT == null) {
//            timer = Timer()
//            val networkStatus = CheckNetwork.CheckNetwork(this@Activity_coinDetails)
//            if (networkStatus != 0) {
//                TT = object : TimerTask() {
//                    override fun run() {
//                        call!!.clone().enqueue(object : Callback<List<SelectedCoinModel?>> {
//                            override fun onResponse(
//                                call: Call<List<SelectedCoinModel?>>,
//                                response: Response<List<SelectedCoinModel?>>
//                            ) {
//                                if (response.isSuccessful) {
//                                    val selectedCoinModels = (response.body())!!
//                                    coinDetailsViewModel!!.selectedCoinModelLiveData.setValue(
//                                        selectedCoinModels[0])
//                                } else {
//                                    Toast.makeText(this@Activity_coinDetails,
//                                        "네트워크 상태를 확인해 주세요.",
//                                        Toast.LENGTH_SHORT).show()
//                                }
//                            }
//
//                            override fun onFailure(
//                                call: Call<List<SelectedCoinModel?>>,
//                                t: Throwable
//                            ) {
//                                t.printStackTrace()
//                                Toast.makeText(this@Activity_coinDetails,
//                                    "네트워크 상태를 확인해 주세요.",
//                                    Toast.LENGTH_SHORT).show()
//                            }
//                        })
//                    }
//                }
//                timer!!.schedule(TT, 0, 1000)
//            } else {
//                Toast.makeText(this@Activity_coinDetails, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT)
//                    .show()
//                finish()
//            }
//        }
//    }
//
//    public override fun onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
//        super.onPause()
//        if (timer != null && TT != null) {
//            timer!!.cancel()
//            timer = null
//            TT = null
//        }
//    }
//
//    override fun onClick(view: View) {
//        if (view === binding!!.btnBookMark) {
//            coinDetailsViewModel!!.updateBookMark(binding, symbol)
//        } else if (view === binding!!.btnCoinInfoBackSpace) {
//            finish()
//        }
//    }
//
//    private fun initAds() {
//        MobileAds.initialize(this, object : OnInitializationCompleteListener {
//            override fun onInitializationComplete(initializationStatus: InitializationStatus) {}
//        })
//        val adRequest = AdRequest.Builder().build()
//        binding!!.adView2.loadAd(adRequest)
//    }
//}