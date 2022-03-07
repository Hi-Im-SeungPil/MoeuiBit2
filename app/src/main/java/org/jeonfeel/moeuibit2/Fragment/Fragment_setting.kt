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
//import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart.GetRecentCoinChart
//import org.jeonfeel.moeuibit2.Fragment.Chart.GetMovingAverage
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.ViewGroup
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
//import android.content.Intent
//import org.jeonfeel.moeuibit2.Activitys.Activity_portfolio
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Context
//import android.net.Uri
//import android.view.View
//import android.widget.*
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
//import org.jeonfeel.moeuibit2.*
//
//class Fragment_setting constructor() : Fragment() {
//    var btn_sendError: Button? = null
//    var btn_sendSuggestions: Button? = null
//    var btn_appReset: Button? = null
//    var btn_logout: Button? = null
//    var btn_writeReview: Button? = null
//    var btn_needUpdate: Button? = null
//    var tv_versionCheck: TextView? = null
//    var context: Context? = null
//    var db: MoEuiBitDatabase? = null
//    public override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//    }
//
//    public override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        db = getInstance((context)!!)
//        val versionName: String = BuildConfig.VERSION_NAME
//        val rootView: View = inflater.inflate(R.layout.fragment_setting, container, false)
//        context = getActivity()
//        btn_appReset = rootView.findViewById(R.id.btn_appReset)
//        btn_appReset.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
//                    .setTitle("앱 초기화")
//                    .setMessage("다시 되돌릴 수 없습니다. 그래도 초기화 하시겠습니까?")
//                    .setPositiveButton("확인", object : DialogInterface.OnClickListener {
//                        public override fun onClick(dialogInterface: DialogInterface, i: Int) {
//                            db!!.userDAO()!!.deleteAll()
//                            db!!.transactionInfoDAO()!!.deleteAll()
//                            db!!.myCoinDAO()!!.deleteAll()
//                            db!!.favoriteDAO()!!.deleteAll()
//                            Toast.makeText(context, "초기화 되었습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    }).setNegativeButton("취소", null)
//                val alertDialog: AlertDialog = builder.create()
//                alertDialog.show()
//            }
//        })
//        btn_sendSuggestions = rootView.findViewById(R.id.btn_sendSuggestions)
//        btn_sendSuggestions.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                val intent: Intent = Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLSffNcVEhGf_wEsQoEmXLPZAu4u5akcibI5va-MPMp5VANwiNA/viewform?usp=sf_link"))
//                startActivity(intent)
//            }
//        })
//        btn_sendError = rootView.findViewById(R.id.btn_sendError)
//        btn_sendError.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                val intent: Intent = Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://docs.google.com/forms/d/e/1FAIpQLScHWxBRoZk4WvhDWDUwlNXjSmbcmOLy2by6SE4lq2VlaJUUTw/viewform?usp=sf_link"))
//                startActivity(intent)
//            }
//        })
//        btn_logout = rootView.findViewById(R.id.btn_logout)
//        btn_logout.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                FirebaseAuth.getInstance().signOut()
//                Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
//                val intent: Intent = Intent(context, Activity_Login::class.java)
//                startActivity(intent)
//                (context as Activity?)!!.finish()
//            }
//        })
//        btn_writeReview = rootView.findViewById(R.id.btn_writeReview)
//        btn_writeReview.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                val intent: Intent = Intent(Intent.ACTION_VIEW,
//                    Uri.parse("https://play.google.com/store/apps/details?id=org.jeonfeel.moeuibit2"))
//                startActivity(intent)
//            }
//        })
//        return rootView
//    }
//}