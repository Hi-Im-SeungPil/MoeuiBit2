//package org.jeonfeel.moeuibit2.Fragment
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
//import com.github.mikephil.charting.data.CandleEntry
//import com.github.mikephil.charting.data.BarEntry
//import com.github.mikephil.charting.data.CandleData
//import com.github.mikephil.charting.data.CandleDataSet
//import com.github.mikephil.charting.data.BarData
//import com.github.mikephil.charting.data.BarDataSet
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.CombinedData
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
//import com.github.mikephil.charting.data.LineDataSet
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
//import android.net.Uri
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
//
//class Fragment_coinInfo : Fragment {
//    private var linear_coinInfo: LinearLayout? = null
//    private var linear_homepage: LinearLayout? = null
//    private var linear_twitter: LinearLayout? = null
//    private var linear_amount: LinearLayout? = null
//    private var linear_block: LinearLayout? = null
//    private var linear_sorry: LinearLayout? = null
//    private var mDatabase: DatabaseReference? = null
//    private var market: String? = null
//    private var homepage: String? = null
//    private var amount: String? = null
//    private var twitter: String? = null
//    private var block: String? = null
//    private var info: String? = null
//    var customLodingDialog: CustomLodingDialog? = null
//    var networkStatus: Int = 0
//    private var context: Context? = null
//
//    constructor() {}
//    constructor(market: String?) {
//        this.market = market
//    }
//
//    public override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    public override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        val rootView: View = inflater.inflate(R.layout.fragment_coin_info, container, false)
//        customLodingDialog = CustomLodingDialog((getActivity())!!)
//        customLodingDialog!!.show()
//        context = getActivity()
//        networkStatus = CheckNetwork.CheckNetwork(context)
//        FindViewById(rootView)
//        if (networkStatus != 0) {
//            mDatabase = FirebaseDatabase.getInstance().getReference()
//            coinData
//        } else {
//            linear_coinInfo!!.setVisibility(View.GONE)
//            linear_homepage!!.setVisibility(View.GONE)
//            linear_twitter!!.setVisibility(View.GONE)
//            linear_amount!!.setVisibility(View.GONE)
//            linear_block!!.setVisibility(View.GONE)
//            linear_sorry!!.setVisibility(View.GONE)
//            Toast.makeText(getActivity(), "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show()
//            customLodingDialog!!.dismiss()
//        }
//        return rootView
//    }
//
//    private fun FindViewById(rootView: View) {
//        linear_coinInfo = rootView.findViewById(R.id.linear_coinInfo)
//        linear_homepage = rootView.findViewById(R.id.linear_homepage)
//        linear_twitter = rootView.findViewById(R.id.linear_twitter)
//        linear_amount = rootView.findViewById(R.id.linear_amount)
//        linear_block = rootView.findViewById(R.id.linear_block)
//        linear_sorry = rootView.findViewById(R.id.linear_sorry)
//    }
//
//    private fun coinInfoIsNull() {
//        linear_coinInfo!!.setVisibility(View.GONE)
//        linear_homepage!!.setVisibility(View.GONE)
//        linear_twitter!!.setVisibility(View.GONE)
//        linear_amount!!.setVisibility(View.GONE)
//        linear_block!!.setVisibility(View.GONE)
//        linear_sorry!!.setVisibility(View.VISIBLE)
//    }
//
//    private val coinData: Unit
//        private get() {
//            mDatabase!!.child("coinInfo").child((market)!!)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    public override fun onDataChange(snapshot: DataSnapshot) {
//                        homepage = snapshot.child("homepage").getValue(String::class.java)
//                        amount = snapshot.child("amount").getValue(String::class.java)
//                        twitter = snapshot.child("twitter").getValue(String::class.java)
//                        block = snapshot.child("block").getValue(String::class.java)
//                        info = snapshot.child("content").getValue(String::class.java)
//                        val linearLayouts: Array<LinearLayout?> = arrayOf(linear_amount,
//                            linear_block,
//                            linear_coinInfo,
//                            linear_homepage,
//                            linear_twitter)
//                        val url: Array<String?> = arrayOf(amount, block, info, homepage, twitter)
//                        for (i in 0..4) {
//                            val a: Int = i
//                            linearLayouts.get(i)!!
//                                .setOnClickListener(object : View.OnClickListener {
//                                    public override fun onClick(view: View) {
//                                        val intent: Intent =
//                                            Intent(Intent.ACTION_VIEW, Uri.parse(url.get(a)))
//                                        startActivity(intent)
//                                    }
//                                })
//                        }
//                        if (homepage == null) {
//                            coinInfoIsNull()
//                        }
//                        customLodingDialog!!.dismiss()
//                    }
//
//                    public override fun onCancelled(error: DatabaseError) {}
//                })
//        }
//}