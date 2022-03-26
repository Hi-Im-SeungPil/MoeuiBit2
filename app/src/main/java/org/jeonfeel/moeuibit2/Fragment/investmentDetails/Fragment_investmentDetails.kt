//package org.jeonfeel.moeuibit2.Fragment.investmentDetails
//
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase.Companion.getInstance
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvMyCoins.setCurrentPrices
//import org.json.JSONArray
//import org.jeonfeel.moeuibit2.CustomLodingDialog
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.ViewGroup
//import org.jeonfeel.moeuibit2.R
//import org.json.JSONObject
//import androidx.recyclerview.widget.RecyclerView
//import androidx.recyclerview.widget.LinearLayoutManager
//import android.content.DialogInterface
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO
//import org.jeonfeel.moeuibit2.Adapters.Adapter_rvMyCoins
//import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
//import com.google.android.gms.ads.initialization.InitializationStatus
//import org.jeonfeel.moeuibit2.CheckNetwork
//import android.content.Intent
//import org.jeonfeel.moeuibit2.Activitys.Activity_portfolio
//import android.app.Activity
//import android.app.AlertDialog
//import android.content.Context
//import android.graphics.Color
//import android.os.Handler
//import android.util.Log
//import android.view.View
//import android.widget.*
//import org.jeonfeel.moeuibit2.view.activity.main.MainActivity
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
//import com.google.android.gms.ads.rewarded.RewardItem
//import androidx.fragment.app.Fragment
//import com.google.android.gms.ads.*
//import org.jeonfeel.moeuibit2.Database.*
//import java.io.BufferedInputStream
//import java.io.BufferedReader
//import java.io.InputStream
//import java.io.InputStreamReader
//import java.lang.Exception
//import java.lang.StringBuilder
//import java.net.HttpURLConnection
//import java.net.URL
//import java.text.DecimalFormat
//import java.util.ArrayList
//import java.util.HashMap
//
//class Fragment_investmentDetails : Fragment {
//    val TAG: String = "investmentDetails"
//    private var tv_myKoreanWon: TextView? = null
//    private var tv_myTotalProperty: TextView? = null
//    private var tv_totalBuyOut: TextView? = null
//    private var tv_totalEvaluation: TextView? = null
//    private var tv_evaluationGainLoss: TextView? = null
//    private var tv_yield: TextView? = null
//    private var db: MoEuiBitDatabase? = null
//    private val decimalFormat: DecimalFormat = DecimalFormat("###,###")
//    private var myCoinsDTOS: ArrayList<MyCoinsDTO?>? = null
//    private var rv_myCoins: RecyclerView? = null
//    private var markets: String = ""
//    private var myCoins: List<MyCoin?>? = null
//    private var totalBuyOut: Long = 0
//    private var myKoreanWon: Long = 0
//    private var getMyCoins: GetMyCoins? = null
//    private var currentPrices: ArrayList<Double>? = null
//    private var adapter_rvMyCoins: Adapter_rvMyCoins? = null
//    private var context: Context? = null
//    private var customLodingDialog: CustomLodingDialog? = null
//    private var rewardedInterstitialAd: RewardedInterstitialAd? = null
//    private var earnKrw: EarnKrw? = null
//    private var checkSecond: Int = 0
//    private var marketList: ArrayList<String>? = null
//    private var hashMap: HashMap<String?, Int?>? = null
//
//    constructor() {}
//    constructor(customLodingDialog: CustomLodingDialog?) {
//        this.customLodingDialog = customLodingDialog
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
//        val rootView: View =
//            inflater.inflate(R.layout.fragment_investment_details, container, false)
//        context = getActivity()
//        earnKrw = EarnKrw()
//        FindViewById(rootView)
//        db = getInstance((context)!!)
//        if (rewardedInterstitialAd == null) {
//            MobileAds.initialize(context, object : OnInitializationCompleteListener {
//                public override fun onInitializationComplete(initializationStatus: InitializationStatus) {
//                    loadAd()
//                }
//            })
//        }
//        setRv_myCoins()
//        init()
//        setCoinOrder(rootView)
//        setBtn_earningKrw(rootView)
//        setBtn_portfolio(rootView)
//        return rootView
//    }
//
//    //아이디찾기
//    private fun FindViewById(rootView: View) {
//        tv_myKoreanWon = rootView.findViewById(R.id.tv_myKoreanWon)
//        tv_myTotalProperty = rootView.findViewById(R.id.tv_myTotalProperty)
//        tv_totalBuyOut = rootView.findViewById(R.id.tv_totalBuyOut)
//        tv_totalEvaluation = rootView.findViewById(R.id.tv_totalEvaluation)
//        tv_evaluationGainLoss = rootView.findViewById(R.id.tv_evaluationGainLoss)
//        tv_yield = rootView.findViewById(R.id.tv_yield)
//        rv_myCoins = rootView.findViewById(R.id.rv_myCoins)
//    }
//
//    private fun setCoinOrder(rootView: View) {
//        val btn_investmentDetailOrderByName: Button =
//            rootView.findViewById(R.id.btn_investmentDetailOrderByName)
//        val btn_investmentDetailOrderByYield: Button =
//            rootView.findViewById(R.id.btn_investmentDetailOrderByYield)
//        val coinOrder: CoinOrder = CoinOrder(btn_investmentDetailOrderByName,
//            btn_investmentDetailOrderByYield,
//            myCoinsDTOS,
//            currentPrices)
//        hashMap = coinOrder.getHashMap()
//    }
//
//    private fun setRv_myCoins() {
//        val linearLayoutManager: LinearLayoutManager =
//            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        rv_myCoins!!.setLayoutManager(linearLayoutManager)
//    }
//
//    //초기설정
//    private fun init() {
//        var stringBuilder: StringBuilder? = null
//        myCoinsDTOS = null
//        currentPrices = null
//        marketList = null
//        totalBuyOut = 0
//        stringBuilder = StringBuilder()
//        myCoinsDTOS = ArrayList()
//        currentPrices = ArrayList()
//        marketList = ArrayList()
//
//        //보유 krw 설정
//        val user: User? = db!!.userDAO()!!.all
//        if (user != null) {
//            myKoreanWon = user.krw
//            tv_myTotalProperty!!.setText(decimalFormat.format(myKoreanWon))
//        } else {
//            myKoreanWon = 0
//        }
//
//        // 총 매수 설정
//        myCoins = db!!.myCoinDAO()!!.all
//
//        //보유 코인 정보 get
//        if (myCoins!!.size != 0) {
//            for (i in myCoins!!.indices) {
//                val purchasePrice: Double = myCoins!!.get(i)!!.purchasePrice
//                val quantity: Double = myCoins!!.get(i)!!.quantity
//                val market: String = myCoins!!.get(i)!!.market
//                stringBuilder.append(market).append(",")
//                // 객체 만들어서
//                val myCoinsDTO: MyCoinsDTO = MyCoinsDTO(myCoins!!.get(i)!!.koreanCoinName,
//                    myCoins!!.get(i)!!.symbol,
//                    quantity,
//                    purchasePrice,
//                    0.0)
//                myCoinsDTOS!!.add(myCoinsDTO)
//                marketList!!.add(market)
//                totalBuyOut += Math.round(purchasePrice * quantity)
//                currentPrices!!.add(0.0)
//            }
//            //markets 설정
//            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","))
//            markets = stringBuilder.toString()
//            //리사이클러뷰
//        }
//        tv_totalBuyOut!!.setText(decimalFormat.format(totalBuyOut))
//        tv_myKoreanWon!!.setText(decimalFormat.format(myKoreanWon))
//        adapter_rvMyCoins = Adapter_rvMyCoins(myCoinsDTOS, (context)!!)
//        rv_myCoins!!.setAdapter(adapter_rvMyCoins)
//        adapter_rvMyCoins!!.setCurrentPrices(currentPrices)
//        adapter_rvMyCoins!!.notifyDataSetChanged()
//    }
//
//    public override fun onStart() {
//        super.onStart()
//        val networkStatus: Int = CheckNetwork.CheckNetwork(context)
//        if (networkStatus == 0) {
//            Toast.makeText(context, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show()
//            if (customLodingDialog != null && customLodingDialog!!.isShowing()) customLodingDialog!!.dismiss()
//        }
//        if (myCoins!!.size != 0) {
//            if (customLodingDialog != null && customLodingDialog!!.isShowing()) customLodingDialog!!.dismiss()
//            getMyCoins = GetMyCoins()
//            getMyCoins!!.start()
//        } else {
//            if (customLodingDialog != null && customLodingDialog!!.isShowing()) customLodingDialog!!.dismiss()
//        }
//    }
//
//    public override fun onPause() {
//        super.onPause()
//        if (getMyCoins != null) {
//            getMyCoins!!.stopThread()
//            getMyCoins = null
//        }
//    }
//
//    private fun setBtn_portfolio(rootView: View) {
//        val btn_portfolio: Button = rootView.findViewById(R.id.btn_portfolio)
//        btn_portfolio.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                val intent: Intent = Intent(context, Activity_portfolio::class.java)
//                startActivity(intent)
//            }
//        })
//    }
//
//    // krw 충전
//    private fun setBtn_earningKrw(rootView: View) {
//        val btn_earningKrw: Button = rootView.findViewById(R.id.btn_earningKrw)
//        btn_earningKrw.setOnClickListener(object : View.OnClickListener {
//            public override fun onClick(view: View) {
//                if (CheckNetwork.CheckNetwork(context) == 0) {
//                    Toast.makeText(context, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show()
//                    return
//                }
//                if (checkSecond == 1) {
//                    Toast.makeText(context, "충전 후 5초뒤에 충전 가능합니다.", Toast.LENGTH_SHORT).show()
//                    return
//                }
//                val builder: AlertDialog.Builder = AlertDialog.Builder(context)
//                builder.setTitle("KRW 충전")
//                    .setMessage("짧은 광고를 시청하시면 5,000,000 KRW가 보상으로 지급됩니다.")
//                    .setPositiveButton("충전", object : DialogInterface.OnClickListener {
//                        public override fun onClick(dialogInterface: DialogInterface, i: Int) {
//                            if (rewardedInterstitialAd != null) {
//                                rewardedInterstitialAd!!.show(context as Activity?, earnKrw)
//                                MobileAds.initialize(context,
//                                    object : OnInitializationCompleteListener {
//                                        public override fun onInitializationComplete(
//                                            initializationStatus: InitializationStatus
//                                        ) {
//                                            loadAd()
//                                        }
//                                    })
//                            } else {
//                                Toast.makeText(context, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }).setNegativeButton("취소", object : DialogInterface.OnClickListener {
//                        public override fun onClick(dialogInterface: DialogInterface, i: Int) {}
//                    })
//                val alertDialog: AlertDialog = builder.create()
//                alertDialog.show()
//            }
//        })
//    }
//
//    //1초마다 업비트에서 코인 가격 받아오기
//    inner class GetMyCoins constructor() : Thread() {
//        private var isRunning: Boolean = true
//        public override fun run() {
//            super.run()
//            while (isRunning) {
//                try {
//                    var totalEvaluation: Double = 0.0
//                    val url: URL = URL("https://api.upbit.com/v1/ticker?markets=" + markets)
//                    val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
//                    val inputStream: InputStream = BufferedInputStream(conn.getInputStream())
//                    val bufferedReader: BufferedReader =
//                        BufferedReader(InputStreamReader(inputStream, "UTF-8"))
//                    val builder: StringBuffer = StringBuffer()
//                    Log.d("Qqqq", markets)
//                    var inputString: String? = null
//                    while ((bufferedReader.readLine().also({ inputString = it })) != null) {
//                        builder.append(inputString)
//                    }
//                    val s: String = builder.toString()
//                    val jsonCoinInfo: JSONArray? = JSONArray(s)
//                    conn.disconnect()
//                    bufferedReader.close()
//                    inputStream.close()
//                    if (jsonCoinInfo != null && myCoins!!.size == jsonCoinInfo.length()) {
//                        var jsonObject: JSONObject
//                        for (i in 0 until jsonCoinInfo.length()) {
//                            jsonObject = jsonCoinInfo.get(i) as JSONObject
//                            val currentPrice: Double = jsonObject.getDouble("trade_price")
//                            val quantity: Double = myCoins!!.get(i)!!.quantity
//                            val market: String = jsonObject.getString("market")
//                            totalEvaluation += currentPrice * quantity
//                            currentPrices!!.set((hashMap!!.get(market))!!, currentPrice)
//                            //                                currentPrices.set(i, currentPrice);
//                        }
//                        val longTotalEvaluation: Long = Math.round(totalEvaluation)
//                        val yield: Double =
//                            (longTotalEvaluation - totalBuyOut) / java.lang.Double.valueOf(
//                                totalBuyOut.toDouble()) * 100 //퍼센트 계산
//                        val evaluationGainLoss: Long = (longTotalEvaluation - totalBuyOut)
//                        val myTotalProperty: Long = (myKoreanWon + longTotalEvaluation)
//                        val yieldResult: String = String.format("%.2f", yield)
//                        Log.d("totalEvaluation", totalEvaluation.toString() + "")
//                        Log.d("totalBuyOut", totalBuyOut.toString() + "")
//                        Log.d("evaluationGainLoss", evaluationGainLoss.toString() + "")
//                        if (context != null) {
//                            (context as MainActivity).runOnUiThread(object : Runnable {
//                                public override fun run() {
//                                    if (longTotalEvaluation - totalBuyOut > 0) {
//                                        tv_yield!!.setTextColor(Color.parseColor("#B77300"))
//                                        tv_evaluationGainLoss!!.setTextColor(Color.parseColor("#B77300"))
//                                    } else if (longTotalEvaluation - totalBuyOut < 0) {
//                                        tv_yield!!.setTextColor(Color.parseColor("#0054FF"))
//                                        tv_evaluationGainLoss!!.setTextColor(Color.parseColor("#0054FF"))
//                                    } else {
//                                        tv_yield!!.setTextColor(Color.parseColor("#000000"))
//                                        tv_evaluationGainLoss!!.setTextColor(Color.parseColor("#000000"))
//                                    }
//                                    tv_totalEvaluation!!.setText(decimalFormat.format(
//                                        longTotalEvaluation))
//                                    tv_myTotalProperty!!.setText(decimalFormat.format(
//                                        myTotalProperty))
//                                    tv_evaluationGainLoss!!.setText(decimalFormat.format(
//                                        evaluationGainLoss))
//                                    tv_yield!!.setText(yieldResult + "%")
//                                    adapter_rvMyCoins!!.notifyDataSetChanged()
//                                }
//                            })
//                        }
//                    }
//                } catch (e: Exception) {
//
//                    //코인 매수한게 사라지면 파이어베이스에서 사라진 코인 마켓을 불러와서 내장 db에서 삭제 하고
//                    //markets 목록을 초기화 해서 다시 실행되게 한다
//                    if (myCoins!!.size != 0) {
//                        Log.d(TAG, "catch실행")
//                        val mDatabase: DatabaseReference =
//                            FirebaseDatabase.getInstance().getReference()
//                        mDatabase.child("removeCoin")
//                            .addListenerForSingleValueEvent(object : ValueEventListener {
//                                public override fun onDataChange(snapshot: DataSnapshot) {
//                                    val market: String? = snapshot.getValue(
//                                        String::class.java)
//                                    val marketArray: Array<String> =
//                                        market!!.split(",").toTypedArray()
//                                    run({
//                                        var i: Int = 0
//                                        while (i < marketArray.size) {
//                                            db!!.myCoinDAO()!!.delete(marketArray.get(i))
//                                            val marketIndex: Int =
//                                                marketList!!.indexOf(marketArray.get(i))
//                                            Log.d("marketIndex", marketIndex.toString() + "")
//                                            if (marketIndex != -1) {
//                                                myCoinsDTOS!!.removeAt(marketIndex)
//                                                marketList!!.removeAt(marketIndex)
//                                            }
//                                            i++
//                                        }
//                                    })
//                                    markets = ""
//                                    val stringBuilder: StringBuilder = StringBuilder()
//                                    var i: Int = 0
//                                    while (i < marketList!!.size) {
//                                        stringBuilder.append(marketList!!.get(i)).append(",")
//                                        i++
//                                    }
//                                    stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","))
//                                    markets = stringBuilder.toString()
//                                    Log.d("markets", markets)
//                                    myCoins = db!!.myCoinDAO()!!.all
//                                    if (context != null) {
//                                        (context as MainActivity).runOnUiThread(object : Runnable {
//                                            public override fun run() {
//                                                adapter_rvMyCoins!!.notifyDataSetChanged()
//                                            }
//                                        })
//                                    }
//                                }
//
//                                public override fun onCancelled(error: DatabaseError) {}
//                            })
//                    }
//                    e.printStackTrace()
//                }
//                try {
//                    sleep(1000)
//                } catch (e: Exception) {
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
//    fun loadAd() {
//        // Use the test ad unit ID to load an ad.
//        RewardedInterstitialAd.load(context, "ca-app-pub-8481465476603755/3905762551",
//            AdRequest.Builder().build(), object : RewardedInterstitialAdLoadCallback() {
//                public override fun onAdLoaded(ad: RewardedInterstitialAd) {
//                    rewardedInterstitialAd = ad
//                    Log.e(TAG, "onAdLoaded")
//                }
//
//                public override fun onAdFailedToLoad(loadAdError: LoadAdError) {
//                    Log.e(TAG, "onAdFailedToLoad")
//                }
//            })
//    }
//
//    inner class EarnKrw constructor() : OnUserEarnedRewardListener {
//        public override fun onUserEarnedReward(rewardItem: RewardItem) {
//            var user: User? = db!!.userDAO()!!.all
//            if (user == null) {
//                db!!.userDAO()!!.insert()
//            } else {
//                db!!.userDAO()!!.updatePlusMoney(5000000)
//            }
//            val user1: User? = db!!.userDAO()!!.all
//            myKoreanWon = user1!!.krw
//            tv_myKoreanWon!!.setText(decimalFormat.format(myKoreanWon))
//            val myTotalProperty: Long =
//                tv_myTotalProperty!!.getText().toString().replace(",".toRegex(), "").toLong()
//            tv_myTotalProperty!!.setText(decimalFormat.format(myTotalProperty + 5000000))
//            checkSecond = 1
//            val handler: Handler = Handler()
//            handler.postDelayed(object : Runnable {
//                public override fun run() {
//                    //지연시키길 원하는 밀리초 뒤에 동작
//                    checkSecond = 0
//                }
//            }, 5000)
//            user = null
//        }
//    }
//}