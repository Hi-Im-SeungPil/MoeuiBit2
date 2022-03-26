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
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.Activity_coinDetails
//import com.github.mikephil.charting.data.LineDataSet
//import com.github.mikephil.charting.components.MarkerView
//import androidx.recyclerview.widget.RecyclerView
//import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoinArcade
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvTransactionInfo
//import org.jeonfeel.moeuibit2.Database.TransactionInfo
//import org.jeonfeel.moeuibit2.Fragment.coinOrder.Fragment_coinOrder.GetUpBitCoinArcade
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.annotation.SuppressLint
//import android.text.TextWatcher
//import android.view.View.OnTouchListener
//import android.text.InputType
//import android.content.DialogInterface
//import android.text.Editable
//import android.text.TextUtils
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
//import android.content.Context
//import android.net.Uri
//import android.view.View
//import android.widget.*
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
//import androidx.fragment.app.Fragment
//
//class Fragment_coinSite : Fragment {
//    var linear_ddangle: LinearLayout? = null
//    var linear_coinPan: LinearLayout? = null
//    var linear_moneyNet: LinearLayout? = null
//    var linear_cobak: LinearLayout? = null
//    var linear_blockChanHub: LinearLayout? = null
//    var linear_dcInside: LinearLayout? = null
//    var linear_bitMan: LinearLayout? = null
//    var linear_FMkorea: LinearLayout? = null
//    var linear_coinMarketCap: LinearLayout? = null
//    var linear_coinGeko: LinearLayout? = null
//    var linear_kimpga: LinearLayout? = null
//    var linear_cryPrice: LinearLayout? = null
//    var linear_coDal: LinearLayout? = null
//    var linear_coinNess: LinearLayout? = null
//    var linear_coinMarketCal: LinearLayout? = null
//    var linear_xangle: LinearLayout? = null
//    var linear_binance: LinearLayout? = null
//    var linear_gopax: LinearLayout? = null
//    var linear_bybit: LinearLayout? = null
//    var linear_upBit: LinearLayout? = null
//    var linear_bithumb: LinearLayout? = null
//    var linear_coinOne: LinearLayout? = null
//    var linear_musk: LinearLayout? = null
//    var linears_group1: LinearLayout? = null
//    var linears_group2: LinearLayout? = null
//    var linears_group3: LinearLayout? = null
//    var linears_group4: LinearLayout? = null
//    var linears_group5: LinearLayout? = null
//    var linears_group6: LinearLayout? = null
//    var linears_group7: LinearLayout? = null
//    var linears_group8: LinearLayout? = null
//    var btn_hide1: Button? = null
//    var btn_hide2: Button? = null
//    var btn_hide3: Button? = null
//    var btn_hide4: Button? = null
//    var btn_hide5: Button? = null
//    var context: Context? = null
//
//    constructor() {}
//    constructor(context: Context?) {
//        this.context = context
//        // Required empty public constructor
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
//        val rootView: View = inflater.inflate(R.layout.fragment_coin_site, container, false)
//        FindViewById(rootView)
//        setIv_exchanges()
//        setLinear()
//        setBtns()
//        return rootView
//    }
//
//    private fun FindViewById(rootView: View) {
//        linear_upBit = rootView.findViewById(R.id.linear_upBit)
//        linear_bithumb = rootView.findViewById(R.id.linear_bithumb)
//        linear_coinOne = rootView.findViewById(R.id.linear_coinOne)
//        linear_binance = rootView.findViewById(R.id.linear_binance)
//        linear_gopax = rootView.findViewById(R.id.linear_gopax)
//        linear_bybit = rootView.findViewById(R.id.linear_bybit)
//        linear_ddangle = rootView.findViewById(R.id.linear_ddangle)
//        linear_coinPan = rootView.findViewById(R.id.linear_coinPan)
//        linear_moneyNet = rootView.findViewById(R.id.linear_moneyNet)
//        linear_cobak = rootView.findViewById(R.id.linear_cobak)
//        linear_blockChanHub = rootView.findViewById(R.id.linear_blockChanHub)
//        linear_dcInside = rootView.findViewById(R.id.linear_dcInside)
//        linear_bitMan = rootView.findViewById(R.id.linear_bitMan)
//        linear_FMkorea = rootView.findViewById(R.id.linear_FMkorea)
//        linear_coinMarketCap = rootView.findViewById(R.id.linear_coinMarketCap)
//        linear_coinGeko = rootView.findViewById(R.id.linear_coinGeko)
//        linear_xangle = rootView.findViewById(R.id.linear_xangle)
//        linear_kimpga = rootView.findViewById(R.id.linear_kimpga)
//        linear_cryPrice = rootView.findViewById(R.id.linear_cryPrice)
//        linear_coDal = rootView.findViewById(R.id.linear_coDal)
//        linear_coinNess = rootView.findViewById(R.id.linear_coinNess)
//        linear_coinMarketCal = rootView.findViewById(R.id.linear_coinMarketCal)
//        linear_musk = rootView.findViewById(R.id.linear_musk)
//        linears_group1 = rootView.findViewById(R.id.linears_group1)
//        linears_group2 = rootView.findViewById(R.id.linears_group2)
//        linears_group3 = rootView.findViewById(R.id.linears_group3)
//        linears_group4 = rootView.findViewById(R.id.linears_group4)
//        linears_group5 = rootView.findViewById(R.id.linears_group5)
//        linears_group6 = rootView.findViewById(R.id.linears_group6)
//        linears_group7 = rootView.findViewById(R.id.linears_group7)
//        linears_group8 = rootView.findViewById(R.id.linears_group8)
//        btn_hide1 = rootView.findViewById(R.id.btn_hide1)
//        btn_hide2 = rootView.findViewById(R.id.btn_hide2)
//        btn_hide3 = rootView.findViewById(R.id.btn_hide3)
//        btn_hide4 = rootView.findViewById(R.id.btn_hide4)
//        btn_hide5 = rootView.findViewById(R.id.btn_hide5)
//    }
//
//    private fun setIv_exchanges() {
//        linear_upBit!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.dunamu.exchange", "https://www.upbit.com")
//            }
//        })
//        linear_bithumb!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.btckorea.bithumb", "https://www.bithumb.com/")
//            }
//        })
//        linear_coinOne!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("coinone.co.kr.official", "https://coinone.co.kr/")
//            }
//        })
//        linear_binance!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.binance.dev", "https://www.binance.com/en/")
//            }
//        })
//        linear_gopax!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("kr.co.gopax", "https://www.gopax.co.kr/")
//            }
//        })
//        linear_bybit!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.bybit.app", "https://www.bybit.com/en-US/")
//            }
//        })
//        linear_ddangle!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.ddengle.app", "https://www.ddengle.com/")
//            }
//        })
//        linear_coinPan!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.coinpan.coinpan", "https://coinpan.com/")
//            }
//        })
//        linear_moneyNet!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("mnet7.mobile", "https://www.moneynet.co.kr/")
//            }
//        })
//        linear_cobak!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.cobak.android", "https://cobak.co.kr/")
//            }
//        })
//        linear_blockChanHub!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("kr.blockchainhub.app", "https://blockchainhub.kr/")
//            }
//        })
//        linear_dcInside!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.dcinside.app", "https://gall.dcinside.com/list.php?id=bitcoins_new1")
//            }
//        })
//        linear_FMkorea!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.fmkorea.m.fmk", "https://www.fmkorea.com/coin")
//            }
//        })
//        linear_coinMarketCap!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.coinmarketcap.android", "https://coinmarketcap.com/ko/")
//            }
//        })
//        linear_coinGeko!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.coingecko.coingeckoapp", "https://www.coingecko.com/ko")
//            }
//        })
//        linear_xangle!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.crossangle.xangle", "https://xangle.io/")
//            }
//        })
//        linear_coinNess!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.coinness", "https://kr.coinness.com/")
//            }
//        })
//        linear_coinMarketCal!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                isInstall("com.coincal", "https://coinmarketcal.com/en/")
//            }
//        })
//    }
//
//    private fun isInstall(pakageName: String, uri: String) {
//        var intent: Intent? = context!!.getPackageManager().getLaunchIntentForPackage(pakageName)
//        if (intent == null) {
//            //미설치
//            intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//            startActivity(intent)
//        } else {
//            //설치
//            startActivity(intent)
//        }
//    }
//
//    private fun setLinear() {
//        val linearLayouts: Array<LinearLayout?> =
//            arrayOf(linear_bitMan, linear_kimpga, linear_cryPrice, linear_coDal, linear_musk)
//        val setLinears: SetLinears = SetLinears()
//        for (i in linearLayouts.indices) {
//            linearLayouts.get(i)!!.setOnClickListener(setLinears)
//        }
//    }
//
//    private fun setBtns() {
//        btn_hide1!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                if (linears_group1!!.getVisibility() == View.VISIBLE) {
//                    btn_hide1!!.setText("▼")
//                    linears_group1!!.setVisibility(View.GONE)
//                    linears_group8!!.setVisibility(View.GONE)
//                } else {
//                    btn_hide1!!.setText("▲")
//                    linears_group1!!.setVisibility(View.VISIBLE)
//                    linears_group8!!.setVisibility(View.VISIBLE)
//                }
//            }
//        })
//        btn_hide2!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                if (linears_group2!!.getVisibility() == View.VISIBLE) {
//                    btn_hide2!!.setText("▼")
//                    linears_group2!!.setVisibility(View.GONE)
//                    linears_group3!!.setVisibility(View.GONE)
//                    linears_group4!!.setVisibility(View.GONE)
//                } else {
//                    btn_hide2!!.setText("▲")
//                    linears_group2!!.setVisibility(View.VISIBLE)
//                    linears_group3!!.setVisibility(View.VISIBLE)
//                    linears_group4!!.setVisibility(View.VISIBLE)
//                }
//            }
//        })
//        btn_hide3!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                if (linears_group5!!.getVisibility() == View.VISIBLE) {
//                    btn_hide3!!.setText("▼")
//                    linears_group5!!.setVisibility(View.GONE)
//                } else {
//                    btn_hide3!!.setText("▲")
//                    linears_group5!!.setVisibility(View.VISIBLE)
//                }
//            }
//        })
//        btn_hide4!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                if (linears_group6!!.getVisibility() == View.VISIBLE) {
//                    btn_hide4!!.setText("▼")
//                    linears_group6!!.setVisibility(View.GONE)
//                } else {
//                    btn_hide4!!.setText("▲")
//                    linears_group6!!.setVisibility(View.VISIBLE)
//                }
//            }
//        })
//        btn_hide5!!.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                if (linears_group7!!.getVisibility() == View.VISIBLE) {
//                    btn_hide5!!.setText("▼")
//                    linears_group7!!.setVisibility(View.GONE)
//                } else {
//                    btn_hide5!!.setText("▲")
//                    linears_group7!!.setVisibility(View.VISIBLE)
//                }
//            }
//        })
//    }
//
//    internal inner class SetLinears constructor() : View.OnClickListener {
//        public override fun onClick(view: View) {
//            var uri: String? = ""
//            when (view.getId()) {
//                R.id.linear_bitMan -> uri = "https://cafe.naver.com/nexontv"
//                R.id.linear_musk -> uri = "https://twitter.com/elonmusk"
//                R.id.linear_kimpga -> uri = "https://kimpga.com/"
//                R.id.linear_cryPrice -> uri = "https://scolkg.com/"
//                R.id.linear_coDal -> uri = "https://www.coindalin.com/"
//            }
//            val intent: Intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
//            startActivity(intent)
//        }
//    }
//}