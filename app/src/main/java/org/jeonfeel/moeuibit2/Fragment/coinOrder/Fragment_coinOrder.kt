//package org.jeonfeel.moeuibit2.Fragment.coinOrder
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
//import org.jeonfeel.moeuibit2.Fragment.Chart.org.jeonfeel.moeuibit2.helper.GetMovingAverage
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
//import android.app.AlertDialog
//import android.content.Context
//import android.view.View
//import android.view.inputmethod.InputMethodManager
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
//import java.io.*
//import java.lang.Exception
//import java.lang.NullPointerException
//import java.net.HttpURLConnection
//import java.net.MalformedURLException
//import java.net.URL
//import java.text.DecimalFormat
//import java.util.ArrayList
//
///**
// * A simple [Fragment] subclass.
// * create an instance of this fragment.
// */
//class Fragment_coinOrder : Fragment {
//    private var include_coin_parent: LinearLayout? = null
//    private var linear_coinSell: LinearLayout? = null
//    private var linear_coinOrder: LinearLayout? = null
//    private var rv_coinArcade: RecyclerView? = null
//    private var rv_transactionInfo: RecyclerView? = null
//    private var market: String? = null
//    private var koreanName: String? = null
//    private var symbol: String? = null
//    private var coinArcadeDTOS: ArrayList<CoinArcadeDTO>? = null
//    private var adapter_rvCoinArcade: Adapter_rvCoinArcade? = null
//    private var adapter_rvTransactionInfo: Adapter_rvTransactionInfo? = null
//    private var transactionInfos: List<TransactionInfo>? = null
//    private var openingPrice: Double? = null
//    private var index = 0 //ask coinArcade set을 위해!!
//    private var index2 = 0 //bid coinArcade set을 위해!!
//    private var getUpBitCoinArcade: GetUpBitCoinArcade? = null
//    private var tv_orderableAmount: TextView? = null
//    private var tv_sellAbleCoinQuantity: TextView? = null
//    private var tv_sellAbleAmount: TextView? = null
//    private var tv_sellAbleCoinSymbol: TextView? = null
//    private var tv_transactionInfoIsNull: TextView? = null
//    private var tv_orderCoinTotalAmount: TextView? = null
//    private var tv_sellCoinTotalAmount: TextView? = null
//    private var tv_orderCoinPrice: TextView? = null
//    private var tv_sellCoinPrice: TextView? = null
//    private var btn_purchaseByDesignatingTotalAmount: Button? = null
//    private var btn_coinOrder: Button? = null
//    private var btn_coinSell: Button? = null
//    private var btn_coinSellReset: Button? = null
//    private var btn_coinOrderReset: Button? = null
//    private var db: MoEuiBitDatabase? = null
//    private var leftMoney: Long = 0
//    private var et_orderCoinQuantity: EditText? = null
//    private val decimalFormat = DecimalFormat("###,###")
//    private var spinner_orderCoinQuantity: Spinner? = null
//    private var spinner_sellCoinQuantity: Spinner? = null
//    private var et_sellCoinQuantity: EditText? = null
//    private var commaResult: String? = null
//    private var context: Context? = null
//    private var customLodingDialog: CustomLodingDialog? = null
//
//    constructor() {}
//    constructor(market: String?, koreanName: String?, symbol: String?) {
//        // Required empty public constructor
//        this.market = market
//        this.koreanName = koreanName
//        this.symbol = symbol
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
//        val rootView = inflater.inflate(R.layout.fragment_coin_order, container, false)
//        // Inflate the layout for this fragment
//        context = activity
//        customLodingDialog = CustomLodingDialog(context!!)
//        customLodingDialog!!.show()
//        FindViewById(rootView)
//        setRv_coinArcade()
//        openingPriceFromApi
//        coinArcadeInfo
//        initFragment_coinOrder()
//        setBtn_coinSellReset()
//        setBtn_coinOrderReset()
//        setTabLayout(rootView)
//        return rootView
//    }
//
//    private fun setRv_coinArcade() {
//        val linearLayoutManager = LinearLayoutManager(context)
//        rv_coinArcade!!.layoutManager = linearLayoutManager
//    }
//
//    private fun setRv_transactionInfo() {
//        val linearLayoutManager = LinearLayoutManager(context)
//        linearLayoutManager.reverseLayout = true
//        linearLayoutManager.stackFromEnd = true
//        rv_transactionInfo!!.layoutManager = linearLayoutManager
//        transactionInfos = ArrayList()
//        adapter_rvTransactionInfo = Adapter_rvTransactionInfo(transactionInfos, context!!)
//        rv_transactionInfo!!.adapter = adapter_rvTransactionInfo
//    }
//
//    @SuppressLint("ClickableViewAccessibility")
//    private fun initFragment_coinOrder() {
//        setBtn_coinOrder()
//        setBtn_coinSell()
//        setSpinner_orderCoinQuantity()
//        setSpinner_sellCoinQuantity()
//        setRv_transactionInfo()
//        linear_coinSell!!.visibility = View.GONE
//        rv_transactionInfo!!.visibility = View.GONE
//        tv_sellAbleCoinSymbol!!.text = symbol
//        db = getInstance(context!!)
//
//        // 주문가능 설정 ---------------
//        val user = db!!.userDAO()!!.all
//        leftMoney = user?.krw ?: 0
//        tv_orderableAmount!!.text = decimalFormat.format(leftMoney)
//
//        // 주문가능 설정 ---------------
//
//        // 판매가능 코인 설정 ---------------
//        val myCoin = db!!.myCoinDAO()!!.isInsert(market)
//        if (myCoin != null) {
//            tv_sellAbleCoinQuantity!!.text = String.format("%.8f", myCoin.quantity)
//        } else {
//            tv_sellAbleCoinQuantity!!.text = "0"
//        }
//
//        // 판매가능 코인 설정 ---------------
//        val tw1 = tw1()
//        et_orderCoinQuantity!!.addTextChangedListener(tw1)
//        et_sellCoinQuantity!!.addTextChangedListener(tw1)
//
//// 레이아웃 터치하면 키보드 내림.
//        include_coin_parent!!.setOnTouchListener { view, motionEvent ->
//            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            if (linear_coinOrder!!.visibility == View.VISIBLE) {
//                if (tv_orderCoinPrice!!.isFocused) {
//                    tv_orderCoinPrice!!.clearFocus()
//                    imm.hideSoftInputFromWindow(tv_orderCoinPrice!!.windowToken, 0)
//                } else if (et_orderCoinQuantity!!.isFocused) {
//                    et_orderCoinQuantity!!.clearFocus()
//                    imm.hideSoftInputFromWindow(et_orderCoinQuantity!!.windowToken, 0)
//                }
//            } else if (linear_coinSell!!.visibility == View.VISIBLE) {
//                if (tv_sellCoinPrice!!.isFocused) {
//                    tv_sellCoinPrice!!.clearFocus()
//                    imm.hideSoftInputFromWindow(tv_sellCoinPrice!!.windowToken, 0)
//                } else if (et_sellCoinQuantity!!.isFocused) {
//                    et_sellCoinQuantity!!.clearFocus()
//                    imm.hideSoftInputFromWindow(et_sellCoinQuantity!!.windowToken, 0)
//                }
//            }
//            false
//        }
//        btn_purchaseByDesignatingTotalAmount!!.setOnClickListener(View.OnClickListener {
//            if (tv_orderCoinPrice!!.isFocused) {
//                tv_orderCoinPrice!!.clearFocus()
//            } else if (et_orderCoinQuantity!!.isFocused) {
//                et_orderCoinQuantity!!.clearFocus()
//            }
//            val editText = EditText(context)
//            editText.inputType = InputType.TYPE_CLASS_NUMBER
//            val user = db!!.userDAO()!!.all
//            var myKrw: Long = 0
//            if (user != null) {
//                myKrw = user.krw
//            }
//            val textWatcher = tw2(editText)
//            editText.addTextChangedListener(textWatcher)
//            val builder = AlertDialog.Builder(context)
//            builder.setTitle("매수할 총액을 입력하세요")
//            builder.setMessage("\n주문가능\n\n" + decimalFormat.format(myKrw) + " KRW")
//            builder.setView(editText)
//            builder.setPositiveButton("매수", DialogInterface.OnClickListener { dialogInterface, i ->
//                try {
//                    var currentPrice = (context as Activity_coinDetails?)!!.globalCurrentPrice
//                    if (currentPrice == null) {
//                        currentPrice =
//                            tv_orderCoinPrice!!.text.toString().replace(",", "").toDouble()
//                    }
//                    if (editText.length() == 0) {
//                        Toast.makeText(context, "빈곳없이 매수요청 해주세요!", Toast.LENGTH_SHORT).show()
//                        return@OnClickListener
//                    }
//                    val orderAmount = editText.text.toString().replace(",", "").toDouble()
//                    val orderQuantity = String.format("%.8f", orderAmount / currentPrice)
//                    val orderQuantityResult = orderQuantity.toDouble()
//                    var myCoin: MyCoin? = null
//                    myCoin = db!!.myCoinDAO()!!.isInsert(market)
//                    if (Math.round(orderAmount) > leftMoney) {
//                        Toast.makeText(context, "보유 KRW가 부족합니다.", Toast.LENGTH_SHORT).show()
//                        return@OnClickListener
//                    }
//                    if (myCoin == null && orderAmount <= leftMoney) {
//                        if (Math.round(orderAmount) < 5000) {
//                            Toast.makeText(context, "5000KRW 이상만 주문 가능합니다.", Toast.LENGTH_SHORT)
//                                .show()
//                            return@OnClickListener
//                        }
//                        val myCoin1 = MyCoin((market)!!,
//                            currentPrice,
//                            (koreanName)!!,
//                            (symbol)!!,
//                            orderQuantityResult)
//                        db!!.myCoinDAO()!!.insert(myCoin1)
//                        db!!.userDAO()!!.updateMinusMoney(Math.round(orderAmount))
//                        val user = db!!.userDAO()!!.all
//                        tv_orderableAmount!!.text = decimalFormat.format(user!!.krw)
//                        myCoin = db!!.myCoinDAO()!!.isInsert(market)
//                        tv_sellAbleCoinQuantity!!.text = String.format("%.8f", myCoin!!.quantity)
//                        et_orderCoinQuantity!!.setText("")
//                        tv_orderCoinTotalAmount!!.text = ""
//                        db!!.transactionInfoDAO()!!.insert(market,
//                            currentPrice,
//                            orderQuantityResult,
//                            Math.round(orderAmount),
//                            "bid",
//                            System.currentTimeMillis())
//                        Toast.makeText(context, "매수 되었습니다.", Toast.LENGTH_SHORT).show()
//                    } else if (myCoin != null && orderAmount <= leftMoney) {
//                        if (Math.round(orderAmount) < 5000) {
//                            Toast.makeText(context, "5000KRW 이상만 주문 가능합니다.", Toast.LENGTH_SHORT)
//                                .show()
//                            return@OnClickListener
//                        }
//                        val myCoinQuantity = myCoin.quantity
//                        val myCoinPrice = myCoin.purchasePrice
//                        var averageOrderPrice = averageOrderPriceCalculate(myCoinQuantity,
//                            myCoinPrice,
//                            orderQuantityResult,
//                            currentPrice)
//                        if (averageOrderPrice >= 100) {
//                            val purchasePrice = Math.round(averageOrderPrice)
//                                .toInt()
//                            db!!.myCoinDAO()!!.updatePurchasePriceInt(market, purchasePrice)
//                        } else {
//                            averageOrderPrice =
//                                java.lang.Double.valueOf(String.format("%.2f", averageOrderPrice))
//                            db!!.myCoinDAO()!!.updatePurchasePrice(market, averageOrderPrice)
//                        }
//                        val quantityResult = java.lang.Double.valueOf(String.format("%.8f",
//                            myCoinQuantity + orderQuantityResult))
//                        db!!.myCoinDAO()!!.updateQuantity(market, quantityResult)
//                        db!!.userDAO()!!.updateMinusMoney(Math.round(orderAmount))
//                        val user = db!!.userDAO()!!.all
//                        tv_orderableAmount!!.text = decimalFormat.format(user!!.krw)
//                        myCoin = db!!.myCoinDAO()!!.isInsert(market)
//                        et_orderCoinQuantity!!.setText("")
//                        tv_orderCoinTotalAmount!!.text = ""
//                        db!!.transactionInfoDAO()!!.insert(market,
//                            currentPrice,
//                            orderQuantityResult,
//                            Math.round(orderAmount),
//                            "bid",
//                            System.currentTimeMillis())
//                        Toast.makeText(context, "매수 되었습니다.", Toast.LENGTH_SHORT).show()
//                    }
//                } catch (n: NullPointerException) {
//                    Toast.makeText(context, "오류가 발생했습니다 \n다시 시도해 주세요", Toast.LENGTH_SHORT).show()
//                }
//            }).setNegativeButton("취소", null)
//            val alertDialog = builder.create()
//            alertDialog.show()
//            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
//            editText.postDelayed({
//                editText.requestFocus()
//                imm.showSoftInput(editText, 0)
//            }, 100)
//        })
//
////        et_sellCoinTotalAmount.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                if(et_sellCoinPrice.isFocused()) {
////                    et_sellCoinPrice.clearFocus();
////                }else if(et_sellCoinQuantity.isFocused()) {
////                    et_sellCoinQuantity.clearFocus();
////                }
////                EditText editText = new EditText(getActivity());
////                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
////
////                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
////                builder.setTitle("총액을 입력하세요");
////                builder.setView(editText);
////                builder.setPositiveButton("입력", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        if (!et_sellCoinPrice.getText().toString().equals("0") && et_sellCoinPrice.length() != 0 && editText.length() != 0){
////                            long amount = Long.parseLong(editText.getText().toString());
////                            Double orderPrice = Double.valueOf(et_sellCoinPrice.getText().toString().replace(",",""));
////                            Double quantity =  amount / orderPrice;
////                            et_sellCoinQuantity.setText(String.format("%.8f",quantity));
////                        }
////                    }
////                }).setNegativeButton("취소", null);
////
////                AlertDialog alertDialog = builder.create();
////                alertDialog.show();
////
////                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
////                editText.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        editText.requestFocus();
////                        imm.showSoftInput(editText,0);
////                    }
////                },100);
////            }
////        });
//    }
//
//    private fun tw1(): TextWatcher {
//        return object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                if ((et_orderCoinQuantity!!.length() != 0) && (tv_orderCoinPrice!!.length() != 0) && (linear_coinOrder!!.visibility == View.VISIBLE)) {
//                    val quantity =
//                        java.lang.Double.valueOf(et_orderCoinQuantity!!.text.toString())
//                    val price =
//                        java.lang.Double.valueOf(tv_orderCoinPrice!!.text.toString()
//                            .replace(",", ""))
//                    tv_orderCoinTotalAmount!!.text =
//                        decimalFormat.format(Math.round(quantity * price))
//                } else if ((et_sellCoinQuantity!!.length() != 0) && (tv_sellCoinPrice!!.length() != 0) && (linear_coinSell!!.visibility == View.VISIBLE)) {
//                    val quantity =
//                        java.lang.Double.valueOf(et_sellCoinQuantity!!.text.toString())
//                    val price = java.lang.Double.valueOf(tv_sellCoinPrice!!.text.toString()
//                        .replace(",", ""))
//                    tv_sellCoinTotalAmount!!.text =
//                        decimalFormat.format(Math.round(quantity * price))
//                } else if ((et_orderCoinQuantity!!.length() == 0) && (tv_orderCoinPrice!!.length() != 0) && (linear_coinOrder!!.visibility == View.VISIBLE)) {
//                    tv_orderCoinTotalAmount!!.text = "0"
//                } else if ((et_sellCoinQuantity!!.length() == 0) && (tv_sellCoinPrice!!.length() != 0) && (linear_coinSell!!.visibility == View.VISIBLE)) {
//                    tv_sellCoinTotalAmount!!.text = "0"
//                }
//            }
//
//            override fun afterTextChanged(editable: Editable) {}
//        }
//    }
//
//    private fun tw2(editText: EditText): TextWatcher {
//        return object : TextWatcher {
//            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
//            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
//                if ((!TextUtils.isEmpty(charSequence.toString()) && charSequence.toString() != commaResult
//                            && (java.lang.Double.valueOf(charSequence.toString()
//                        .replace(",".toRegex(), "")) >= 1000))
//                ) {
//                    commaResult =
//                        decimalFormat.format(charSequence.toString().replace(",".toRegex(), "")
//                            .toDouble())
//                    editText.setText(commaResult)
//                    editText.setSelection(commaResult.length)
//                }
//            }
//
//            override fun afterTextChanged(editable: Editable) {}
//        }
//    }
//
//    private fun FindViewById(rootView: View) {
//        rv_coinArcade = rootView.findViewById(R.id.rv_coinArcade)
//        tv_orderableAmount = rootView.findViewById(R.id.tv_orderableAmount)
//        btn_coinOrder = rootView.findViewById(R.id.btn_coinOrder)
//        tv_orderCoinPrice = rootView.findViewById(R.id.tv_orderCoinPrice)
//        et_orderCoinQuantity = rootView.findViewById(R.id.et_orderCoinQuantity)
//        tv_orderCoinTotalAmount = rootView.findViewById(R.id.tv_orderCoinTotalAmount)
//        include_coin_parent = rootView.findViewById(R.id.include_coin_parent)
//        spinner_orderCoinQuantity = rootView.findViewById(R.id.spinner_orderCoinQuantity)
//        linear_coinSell = rootView.findViewById(R.id.linear_coinSell)
//        linear_coinOrder = rootView.findViewById(R.id.linear_coinOrder)
//        tv_sellAbleCoinQuantity = rootView.findViewById(R.id.tv_sellAbleCoinQuantity)
//        tv_sellAbleAmount = rootView.findViewById(R.id.tv_sellAbleAmount)
//        et_sellCoinQuantity = rootView.findViewById(R.id.et_sellCoinQuantity)
//        tv_sellCoinPrice = rootView.findViewById(R.id.tv_sellCoinPrice)
//        tv_sellCoinTotalAmount = rootView.findViewById(R.id.tv_sellCoinTotalAmount)
//        btn_coinSell = rootView.findViewById(R.id.btn_coinSell)
//        btn_coinSellReset = rootView.findViewById(R.id.btn_coinSellReset)
//        btn_coinOrderReset = rootView.findViewById(R.id.btn_coinOrderReset)
//        tv_sellAbleCoinSymbol = rootView.findViewById(R.id.tv_sellAbleCoinSymbol)
//        spinner_sellCoinQuantity = rootView.findViewById(R.id.spinner_sellCoinQuantity)
//        rv_transactionInfo = rootView.findViewById(R.id.rv_transactionInfo)
//        tv_transactionInfoIsNull = rootView.findViewById(R.id.tv_transactionInfoIsNull)
//        btn_purchaseByDesignatingTotalAmount =
//            rootView.findViewById(R.id.btn_purchaseByDesignatingTotalAmount)
//    }
//
//    //매수 , 매도 잔량 중간.
//    private val coinArcadeInfo: Unit
//        //초기 코인 가격 설정
//        private get() {
//            val coinArcadeUrl = "https://api.upbit.com/v1/orderbook?markets=$market"
//            coinArcadeDTOS = ArrayList()
//            val getUpBitCoins = GetUpBitCoins()
//            try {
//                var jsonArray: JSONArray? = JSONArray()
//                jsonArray = getUpBitCoins.execute(coinArcadeUrl).get()
//                val jsonObject = jsonArray!![0] as JSONObject
//                if (jsonArray != null) {
//                    val jsonUnits = jsonObject["orderbook_units"] as JSONArray
//                    var jsonObjUnits = JSONObject()
//                    for (i in jsonUnits.length() - 1 downTo 0) {
//                        jsonObjUnits = jsonUnits[i] as JSONObject
//                        val arcadePrice = jsonObjUnits.getDouble("ask_price")
//                        val coinArcadeSize = jsonObjUnits.getDouble("ask_size")
//                        coinArcadeDTOS!!.add(CoinArcadeDTO(arcadePrice, coinArcadeSize, "ask"))
//                    }
//                    for (i in 0 until jsonUnits.length()) {
//                        jsonObjUnits = jsonUnits[i] as JSONObject
//                        val arcadePrice = jsonObjUnits.getDouble("bid_price")
//                        val coinArcadeSize = jsonObjUnits.getDouble("bid_size")
//                        coinArcadeDTOS!!.add(CoinArcadeDTO(arcadePrice, coinArcadeSize, "bid"))
//                    }
//                    //매수 , 매도 잔량 중간.
//                    val initPrice = coinArcadeDTOS!![14].coinArcadePrice
//
//                    //초기 코인 가격 설정
//                    if (initPrice >= 100) {
//                        tv_orderCoinPrice!!.text = decimalFormat.format(Math.round(initPrice)) + ""
//                        tv_sellCoinPrice!!.text = decimalFormat.format(Math.round(initPrice)) + ""
//                    } else if (initPrice >= 1 && initPrice < 100) {
//                        tv_orderCoinPrice!!.text = String.format("%.2f", initPrice)
//                        tv_sellCoinPrice!!.text = String.format("%.2f", initPrice)
//                    } else if (initPrice < 1) {
//                        tv_orderCoinPrice!!.text = String.format("%.4f", initPrice)
//                        tv_sellCoinPrice!!.text = String.format("%.4f", initPrice)
//                    }
//                    adapter_rvCoinArcade!!.setItem(coinArcadeDTOS!!)
//                    adapter_rvCoinArcade!!.notifyDataSetChanged()
//                    rv_coinArcade!!.scrollToPosition(9)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    private val openingPriceFromApi: Unit
//        private get() {
//            val coinUrl = "https://api.upbit.com/v1/ticker?markets=$market"
//            val getUpBitCoins = GetUpBitCoins()
//            try {
//                var jsonArray: JSONArray? = JSONArray()
//                jsonArray = getUpBitCoins.execute(coinUrl).get()
//                if (jsonArray != null) {
//                    var jsonObject = JSONObject()
//                    jsonObject = jsonArray[0] as JSONObject
//                    openingPrice = jsonObject.getDouble("prev_closing_price")
//                    adapter_rvCoinArcade = Adapter_rvCoinArcade(coinArcadeDTOS!!,
//                        context!!,
//                        openingPrice!!,
//                        customLodingDialog)
//                    rv_coinArcade!!.adapter = adapter_rvCoinArcade
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//
//    override fun onStart() {
//        super.onStart()
//        getUpBitCoinArcade = GetUpBitCoinArcade()
//        getUpBitCoinArcade!!.start()
//    }
//
//    override fun onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
//        super.onPause()
//        if (getUpBitCoinArcade != null) {
//            getUpBitCoinArcade!!.stopThread()
//        }
//    }
//
//    private fun setBtn_coinOrder() {
//        btn_coinOrder!!.setOnClickListener(View.OnClickListener {
//            try {
//                var currentPrice = (context as Activity_coinDetails?)!!.globalCurrentPrice
//                if (currentPrice == null) {
//                    currentPrice = tv_orderCoinPrice!!.text.toString().replace(",", "").toDouble()
//                }
//                if ((et_orderCoinQuantity!!.length() == 0) || (tv_orderCoinPrice!!.length() == 0) || (tv_orderCoinTotalAmount!!.length() == 0)) {
//                    Toast.makeText(context, "빈곳없이 매수요청 해주세요!", Toast.LENGTH_SHORT).show()
//                    return@OnClickListener
//                }
//                val orderQuantity = java.lang.Double.valueOf(
//                    et_orderCoinQuantity!!.text.toString())
//                val orderAmount = Math.round(currentPrice * orderQuantity)
//                var myCoin: MyCoin? = null
//                myCoin = db!!.myCoinDAO()!!.isInsert(market)
//                if (Math.round(orderAmount.toFloat()) > leftMoney) {
//                    Toast.makeText(context, "보유 KRW가 부족합니다.", Toast.LENGTH_SHORT).show()
//                    return@OnClickListener
//                }
//                if (myCoin == null && orderAmount <= leftMoney) {
//                    if (Math.round(orderAmount.toFloat()) < 5000) {
//                        Toast.makeText(context, "5000KRW 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show()
//                        return@OnClickListener
//                    }
//                    val myCoin1 =
//                        MyCoin((market)!!, currentPrice, (koreanName)!!, (symbol)!!, orderQuantity)
//                    db!!.myCoinDAO()!!.insert(myCoin1)
//                    db!!.userDAO()!!
//                        .updateMinusMoney(Math.round(orderAmount.toFloat()).toLong())
//                    val user = db!!.userDAO()!!.all
//                    tv_orderableAmount!!.text = decimalFormat.format(user!!.krw)
//                    myCoin = db!!.myCoinDAO()!!.isInsert(market)
//                    tv_sellAbleCoinQuantity!!.text = String.format("%.8f", myCoin!!.quantity)
//                    et_orderCoinQuantity!!.setText("0")
//                    tv_orderCoinTotalAmount!!.text = "0"
//                    db!!.transactionInfoDAO()!!.insert(market,
//                        currentPrice,
//                        orderQuantity,
//                        Math.round(orderAmount.toFloat()).toLong(),
//                        "bid",
//                        System.currentTimeMillis())
//                    Toast.makeText(context, "매수 되었습니다.", Toast.LENGTH_SHORT).show()
//                } else if (myCoin != null && orderAmount <= leftMoney) {
//                    if (Math.round(orderAmount.toFloat()) < 5000) {
//                        Toast.makeText(context, "5000KRW 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show()
//                        return@OnClickListener
//                    }
//                    val myCoinQuantity = myCoin.quantity
//                    val myCoinPrice = myCoin.purchasePrice
//                    var averageOrderPrice = averageOrderPriceCalculate(myCoinQuantity,
//                        myCoinPrice,
//                        orderQuantity,
//                        currentPrice)
//                    if (averageOrderPrice >= 100) {
//                        val purchasePrice = Math.round(averageOrderPrice).toInt()
//                        db!!.myCoinDAO()!!.updatePurchasePriceInt(market, purchasePrice)
//                    } else if (averageOrderPrice >= 1 && averageOrderPrice < 100) {
//                        averageOrderPrice =
//                            java.lang.Double.valueOf(String.format("%.2f", averageOrderPrice))
//                        db!!.myCoinDAO()!!.updatePurchasePrice(market, averageOrderPrice)
//                    } else if (averageOrderPrice > 0 && averageOrderPrice < 1) {
//                        averageOrderPrice =
//                            java.lang.Double.valueOf(String.format("%.4f", averageOrderPrice))
//                        db!!.myCoinDAO()!!.updatePurchasePrice(market, averageOrderPrice)
//                    }
//                    val quantityResult = java.lang.Double.valueOf(String.format("%.8f",
//                        myCoinQuantity + orderQuantity))
//                    db!!.myCoinDAO()!!.updateQuantity(market, quantityResult)
//                    db!!.userDAO()!!
//                        .updateMinusMoney(Math.round(orderAmount.toFloat()).toLong())
//                    val user = db!!.userDAO()!!.all
//                    tv_orderableAmount!!.text = decimalFormat.format(user!!.krw)
//                    myCoin = db!!.myCoinDAO()!!.isInsert(market)
//                    tv_sellAbleCoinQuantity!!.text = String.format("%.8f", myCoin!!.quantity)
//                    et_orderCoinQuantity!!.setText("0")
//                    tv_orderCoinTotalAmount!!.text = "0"
//                    db!!.transactionInfoDAO()!!.insert(market,
//                        currentPrice,
//                        orderQuantity,
//                        Math.round(orderAmount.toFloat()).toLong(),
//                        "bid",
//                        System.currentTimeMillis())
//                    Toast.makeText(context, "매수 되었습니다.", Toast.LENGTH_SHORT).show()
//                }
//            } catch (n: NullPointerException) {
//                Toast.makeText(context, "오류가 발생했습니다. \n다시 시도해 주세요.", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun setBtn_coinSell() {
//        btn_coinSell!!.setOnClickListener(View.OnClickListener {
//            try {
//                val sellAbleCoinQuantity = java.lang.Double.valueOf(
//                    tv_sellAbleCoinQuantity!!.text.toString())
//                var currentPrice = (context as Activity_coinDetails?)!!.globalCurrentPrice
//                if (currentPrice == null) {
//                    currentPrice = tv_orderCoinPrice!!.text.toString().replace(",", "").toDouble()
//                }
//                if ((et_sellCoinQuantity!!.length() == 0) || (tv_sellCoinPrice!!.length() == 0) || (tv_sellCoinPrice!!.length() == 0)) {
//                    Toast.makeText(context, "빈곳없이 매도요청 해주세요!", Toast.LENGTH_SHORT).show()
//                    return@OnClickListener
//                }
//                val sellCoinQuantity = java.lang.Double.valueOf(
//                    et_sellCoinQuantity!!.text.toString())
//                val sellCoinPrice = java.lang.Double.valueOf(tv_sellCoinPrice!!.text.toString()
//                    .replace(",".toRegex(), ""))
//                if (sellAbleCoinQuantity < sellCoinQuantity) {
//                    Toast.makeText(context, "판매가능한 수량이 부족합니다.", Toast.LENGTH_SHORT).show()
//                    return@OnClickListener
//                }
//                if (Math.round(sellCoinQuantity * currentPrice) < 5000) {
//                    Toast.makeText(context, "5000KRW 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show()
//                    return@OnClickListener
//                }
//                if (sellCoinQuantity <= sellAbleCoinQuantity && Math.round(sellCoinQuantity * sellCoinPrice) >= 5000) {
//                    val initQuantity = db!!.myCoinDAO()!!.isInsert(market)!!.quantity
//                    val quantityResult = java.lang.Double.valueOf(String.format("%.8f",
//                        initQuantity - sellCoinQuantity))
//                    db!!.myCoinDAO()!!.updateQuantity(market, quantityResult)
//                    db!!.userDAO()!!.updatePlusMoney(Math.round(currentPrice * sellCoinQuantity))
//                    val quantity = db!!.myCoinDAO()!!.isInsert(market)!!.quantity
//                    if (quantity == 0.00000000) {
//                        db!!.myCoinDAO()!!.delete(market)
//                        tv_sellAbleCoinQuantity!!.text = "0"
//                    } else {
//                        tv_sellAbleCoinQuantity!!.text = String.format("%.8f", quantity)
//                    }
//                    val user = db!!.userDAO()!!.all
//                    tv_orderableAmount!!.text = decimalFormat.format(user!!.krw)
//                    et_sellCoinQuantity!!.setText("0")
//                    tv_sellCoinTotalAmount!!.text = "0"
//                    Toast.makeText(context, "매도 되었습니다.", Toast.LENGTH_SHORT).show()
//                    db!!.transactionInfoDAO()!!.insert(market,
//                        currentPrice,
//                        sellCoinQuantity,
//                        Math.round(currentPrice * sellAbleCoinQuantity),
//                        "ask",
//                        System.currentTimeMillis())
//                }
//            } catch (n: NullPointerException) {
//                Toast.makeText(context, "오류가 발생했습니다. \n다시 시도해 주세요", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun setSpinner_sellCoinQuantity() {
//        val items = arrayOf<String?>("%선택", "최대", "50%", "25%", "10%")
//        val adapter: ArrayAdapter<*> =
//            ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_dropdown_item, items)
//        spinner_sellCoinQuantity!!.adapter = adapter
//        spinner_sellCoinQuantity!!.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    adapterView: AdapterView<*>?,
//                    view: View,
//                    position: Int,
//                    l: Long
//                ) {
//                    var sellAbleCoinQuantity = 0.0
//                    when (spinner_sellCoinQuantity!!.selectedItemPosition) {
//                        0 -> sellAbleCoinQuantity = 0.0
//                        1 -> sellAbleCoinQuantity =
//                            java.lang.Double.valueOf(tv_sellAbleCoinQuantity!!.text.toString())
//                        2 -> sellAbleCoinQuantity =
//                            java.lang.Double.valueOf(tv_sellAbleCoinQuantity!!.text.toString()) / 2
//                        3 -> sellAbleCoinQuantity =
//                            java.lang.Double.valueOf(tv_sellAbleCoinQuantity!!.text.toString()) / 4
//                        4 -> sellAbleCoinQuantity =
//                            java.lang.Double.valueOf(tv_sellAbleCoinQuantity!!.text.toString()) / 10
//                    }
//                    if (sellAbleCoinQuantity != 0.0) et_sellCoinQuantity!!.setText(String.format("%.8f",
//                        sellAbleCoinQuantity)) else et_sellCoinQuantity!!.setText(
//                        String.format("0"))
//                }
//
//                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
//            }
//    }
//
//    private fun setSpinner_orderCoinQuantity() {
//        val items = arrayOf<String?>("%선택", "최대", "50%", "25%", "10%")
//        val adapter: ArrayAdapter<*> =
//            ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_dropdown_item, items)
//        spinner_orderCoinQuantity!!.adapter = adapter
//        spinner_orderCoinQuantity!!.onItemSelectedListener =
//            object : AdapterView.OnItemSelectedListener {
//                override fun onItemSelected(
//                    adapterView: AdapterView<*>?,
//                    view: View,
//                    position: Int,
//                    l: Long
//                ) {
//                    var orderablePrice: Long = 0
//                    when (spinner_orderCoinQuantity!!.selectedItemPosition) {
//                        0 -> orderablePrice = 0
//                        1 -> orderablePrice =
//                            tv_orderableAmount!!.text.toString().replace(",", "").toInt().toLong()
//                        2 -> orderablePrice =
//                            Math.round((tv_orderableAmount!!.text.toString().replace(",", "")
//                                .toInt() / 2).toFloat()).toLong()
//                        3 -> orderablePrice =
//                            Math.round((tv_orderableAmount!!.text.toString().replace(",", "")
//                                .toInt() / 4).toFloat()).toLong()
//                        4 -> orderablePrice =
//                            Math.round((tv_orderableAmount!!.text.toString().replace(",", "")
//                                .toInt() / 10).toFloat()).toLong()
//                    }
//                    if (orderablePrice != 0L) {
//                        val price = java.lang.Double.valueOf(tv_orderCoinPrice!!.text.toString()
//                            .replace(",", ""))
//                        et_orderCoinQuantity!!.setText(String.format("%.8f",
//                            orderablePrice / price))
//                    } else et_orderCoinQuantity!!.setText("0")
//                }
//
//                override fun onNothingSelected(adapterView: AdapterView<*>?) {}
//            }
//    }
//
//    private fun setTabLayout(rootView: View) {
//        val tab_orderSellTransaction: TabLayout =
//            rootView.findViewById(R.id.tab_orderSellTransaction)
//        tab_orderSellTransaction.addOnTabSelectedListener(object : OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                val position = tab.position
//                if (position == 0) {
//                    linear_coinOrder!!.visibility = View.VISIBLE
//                    linear_coinSell!!.visibility = View.GONE
//                    rv_transactionInfo!!.visibility = View.GONE
//                    tv_transactionInfoIsNull!!.visibility = View.GONE
//                } else if (position == 1) {
//                    linear_coinOrder!!.visibility = View.GONE
//                    linear_coinSell!!.visibility = View.VISIBLE
//                    rv_transactionInfo!!.visibility = View.GONE
//                    tv_transactionInfoIsNull!!.visibility = View.GONE
//                } else if (position == 2) {
//                    linear_coinOrder!!.visibility = View.GONE
//                    linear_coinSell!!.visibility = View.GONE
//                    setRv_transactionInfoData()
//                }
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {}
//            override fun onTabReselected(tab: TabLayout.Tab) {}
//        })
//    }
//
//    private fun setBtn_coinSellReset() {
//        btn_coinSellReset!!.setOnClickListener {
//            et_sellCoinQuantity!!.setText("0")
//            tv_sellCoinTotalAmount!!.text = "0"
//        }
//    }
//
//    private fun setBtn_coinOrderReset() {
//        btn_coinOrderReset!!.setOnClickListener {
//            et_orderCoinQuantity!!.setText("0")
//            tv_orderCoinTotalAmount!!.text = "0"
//        }
//    }
//
//    private fun setRv_transactionInfoData() {
//        val transactionInfos = db!!.transactionInfoDAO()!!
//            .select(market)
//        adapter_rvTransactionInfo!!.setItem(transactionInfos)
//        adapter_rvTransactionInfo!!.notifyDataSetChanged()
//        if (adapter_rvTransactionInfo!!.itemCount == 0) {
//            rv_transactionInfo!!.visibility = View.GONE
//            tv_transactionInfoIsNull!!.visibility = View.VISIBLE
//        } else {
//            rv_transactionInfo!!.visibility = View.VISIBLE
//            tv_transactionInfoIsNull!!.visibility = View.GONE
//        }
//    }
//
//    private fun averageOrderPriceCalculate(
//        myCoinQuantity: Double,
//        myCoinPrice: Double,
//        newCoinQuantity: Double,
//        newCoinPrice: Double
//    ): Double {
//        return (((myCoinQuantity * myCoinPrice) + (newCoinQuantity * newCoinPrice))
//                / (myCoinQuantity + newCoinQuantity))
//    }
//
//    internal inner class GetUpBitCoinArcade : Thread() {
//        private var isRunning = true
//        var url: URL? = null
//        override fun run() {
//            super.run()
//            try {
//                url = URL("https://api.upbit.com/v1/orderbook?markets=$market")
//            } catch (e: MalformedURLException) {
//                e.printStackTrace()
//            }
//            while (isRunning) {
//                try {
//                    val conn = url!!.openConnection() as HttpURLConnection
//                    val inputStream: InputStream = BufferedInputStream(conn.inputStream)
//                    val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
//                    val builder = StringBuffer()
//                    var inputString: String? = null
//                    while ((bufferedReader.readLine().also { inputString = it }) != null) {
//                        builder.append(inputString)
//                    }
//                    val s = builder.toString()
//                    val jsonCoinInfo: JSONArray = JSONArray(s)
//                    conn.disconnect()
//                    bufferedReader.close()
//                    inputStream.close()
//                    try {
//                        val jsonObject = jsonCoinInfo[0] as JSONObject
//                        if (jsonCoinInfo != null) {
//                            val jsonUnits = jsonObject["orderbook_units"] as JSONArray
//                            var jsonObjUnits = JSONObject()
//                            for (i in jsonUnits.length() - 1 downTo 0) {
//                                jsonObjUnits = jsonUnits[i] as JSONObject
//                                val arcadePrice = jsonObjUnits.getDouble("ask_price")
//                                val coinArcadeSize = jsonObjUnits.getDouble("ask_size")
//                                coinArcadeDTOS!![index++] =
//                                    CoinArcadeDTO(arcadePrice, coinArcadeSize, "ask")
//                                index2++
//                            }
//                            for (i in 0 until jsonUnits.length()) {
//                                jsonObjUnits = jsonUnits[i] as JSONObject
//                                val arcadePrice = jsonObjUnits.getDouble("bid_price")
//                                val coinArcadeSize = jsonObjUnits.getDouble("bid_size")
//                                coinArcadeDTOS!![index2++] =
//                                    CoinArcadeDTO(arcadePrice, coinArcadeSize, "bid")
//                            }
//                            index = 0
//                            index2 = 0
//                            if (context != null) {
//                                val currentPrice: Double?
//                                try {
//                                    currentPrice =
//                                        (context as Activity_coinDetails).globalCurrentPrice
//                                } catch (e: Exception) {
//                                    e.printStackTrace()
//                                    try {
//                                        sleep(1000)
//                                    } catch (ee: InterruptedException) {
//                                        ee.printStackTrace()
//                                    }
//                                    continue
//                                }
//                                (context as Activity_coinDetails).runOnUiThread(Runnable {
//                                    if (adapter_rvCoinArcade == null) {
//                                        openingPriceFromApi
//                                        coinArcadeInfo
//                                    }
//                                    if (currentPrice != null) {
//                                        tv_sellAbleAmount!!.text =
//                                            "= " + decimalFormat.format(Math.round(currentPrice * java.lang.Double.valueOf(
//                                                tv_sellAbleCoinQuantity!!.text.toString())))
//                                        if (adapter_rvCoinArcade != null) adapter_rvCoinArcade!!.notifyDataSetChanged()
//                                        if (currentPrice >= 100) {
//                                            tv_orderCoinPrice!!.text =
//                                                decimalFormat.format(currentPrice)
//                                            tv_sellCoinPrice!!.text =
//                                                decimalFormat.format(currentPrice)
//                                        } else if (currentPrice >= 1 && currentPrice < 100) {
//                                            tv_orderCoinPrice!!.text =
//                                                String.format("%.2f", currentPrice)
//                                            tv_sellCoinPrice!!.text =
//                                                String.format("%.2f", currentPrice)
//                                        } else if (currentPrice < 1) {
//                                            tv_orderCoinPrice!!.text =
//                                                String.format("%.4f", currentPrice)
//                                            tv_sellCoinPrice!!.text =
//                                                String.format("%.4f", currentPrice)
//                                        }
//                                        if ((linear_coinOrder!!.visibility == View.VISIBLE) && (tv_orderCoinTotalAmount!!.length() != 0
//                                                    ) && tv_orderCoinTotalAmount != "0" && (et_orderCoinQuantity!!.length() != 0) && et_orderCoinQuantity != "0"
//                                        ) {
//                                            val orderQuantity =
//                                                et_orderCoinQuantity!!.text.toString().toDouble()
//                                            if (orderQuantity * currentPrice >= 100) tv_orderCoinTotalAmount!!.text =
//                                                decimalFormat.format(
//                                                    Math.round(orderQuantity * currentPrice)) else tv_orderCoinTotalAmount!!.text =
//                                                String.format("%.2f", orderQuantity * currentPrice)
//                                        } else if ((linear_coinSell!!.visibility == View.VISIBLE) && (tv_sellCoinTotalAmount!!.length() != 0) && tv_sellCoinTotalAmount != "0") {
//                                            val sellQuantity: Double
//                                            if (et_sellCoinQuantity!!.length() != 0) {
//                                                sellQuantity =
//                                                    et_sellCoinQuantity!!.text.toString().toDouble()
//                                            } else {
//                                                sellQuantity = 0.0
//                                            }
//                                            if (sellQuantity * currentPrice >= 100) {
//                                                tv_sellCoinTotalAmount!!.text =
//                                                    decimalFormat.format(Math.round(sellQuantity * currentPrice))
//                                            } else {
//                                                tv_sellCoinTotalAmount!!.text =
//                                                    String.format("%.2f",
//                                                        sellQuantity * currentPrice)
//                                            }
//                                        }
//                                    }
//                                })
//                            }
//                        }
//                    } catch (e: JSONException) {
//                        e.printStackTrace()
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
//}