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
//
//class Fragment_coinOrder_CPMbtn constructor() { //    Button btn_currentPricePlus,btn_currentPriceMinus,btn_currentPrice;
//    //    EditText et_price;
//    //    int plusPercent, minusPercent;
//    //    DecimalFormat decimalFormat = new DecimalFormat("###,###");
//    //    Context context;
//    //
//    //    public Fragment_coinOrder_CPMbtn(Button btn_currentPricePlus, Button btn_currentPriceMinus, Button btn_currentPrice,EditText et_price,Context context) {
//    //        this.btn_currentPricePlus = btn_currentPricePlus;
//    //        this.btn_currentPriceMinus = btn_currentPriceMinus;
//    //        this.btn_currentPrice = btn_currentPrice;
//    //        this.et_price = et_price;
//    //        this.plusPercent = 5;
//    //        this.minusPercent = -5;
//    //        this.context = context;
//    //        this.btn_currentPricePlus.setText(plusPercent + "%");
//    //        this.btn_currentPriceMinus.setText(minusPercent + "%");
//    //    }
//    //
//    //    public void setBtn_currentPricePlus(){
//    //        btn_currentPricePlus.setOnClickListener(new View.OnClickListener() {
//    //            @Override
//    //            public void onClick(View view) {
//    //
//    //                Double currentPrice = Activity_coinInfo.globalCurrentPrice;
//    //
//    //                if (Activity_coinInfo.globalCurrentPrice != null) {
//    //
//    //                    Double price = plusPercent * 0.01 * currentPrice + currentPrice;
//    //
//    //                    if (plusPercent <= 100) {
//    //
//    //                        if (price >= 100) {
//    //                            et_price.setText(decimalFormat.format(round(price)));
//    //                        } else if(price >= 10 && price < 100){
//    //                            et_price.setText(String.format("%.1f", price));
//    //                        }else{
//    //                            et_price.setText(String.format("%.2f", price));
//    //                        }
//    //
//    //                        plusPercent += 5;
//    //                        minusPercent += 5;
//    //
//    //                        btn_currentPricePlus.setText(plusPercent + "%");
//    //                        btn_currentPriceMinus.setText(minusPercent + "%");
//    //                    } else {
//    //                        Toast.makeText(context, "100% 초과할 수 없습니다.", Toast.LENGTH_SHORT).show();
//    //                    }
//    //                }
//    //            }
//    //        });
//    //    }
//    //
//    //    public void setBtn_currentPriceMinus(){
//    //
//    //
//    //
//    //        btn_currentPriceMinus.setOnClickListener(new View.OnClickListener() {
//    //            @Override
//    //            public void onClick(View view) {
//    //
//    //                Double currentPrice = Activity_coinInfo.globalCurrentPrice;
//    //
//    //                if (Activity_coinInfo.globalCurrentPrice != null) {
//    //
//    //                    Double price = minusPercent * 0.01 * currentPrice + currentPrice;
//    //
//    //                    if (minusPercent >= -95) {
//    //                        if (price >= 100) {
//    //                            et_price.setText(decimalFormat.format(round(price)));
//    //                        } else if(price >= 10 && price < 100){
//    //                            et_price.setText(String.format("%.1f", price));
//    //                        }else {
//    //                            et_price.setText(String.format("%.2f", price));
//    //                        }
//    //
//    //                        minusPercent -= 5;
//    //                        plusPercent -= 5;
//    //
//    //                        btn_currentPricePlus.setText(plusPercent + "%");
//    //                        btn_currentPriceMinus.setText(minusPercent + "%");
//    //
//    //                    } else {
//    //                        Toast.makeText(context, "-95% 초과할 수 없습니다.", Toast.LENGTH_SHORT).show();
//    //                    }
//    //                }
//    //            }
//    //        });
//    //    }
//    //
//    //    public void setBtn_currentPrice(){
//    //
//    //
//    //
//    //        btn_currentPrice.setOnClickListener(new View.OnClickListener() {
//    //            @Override
//    //            public void onClick(View view) {
//    //
//    //                Double currentPrice = Activity_coinInfo.globalCurrentPrice;
//    //
//    //                if (Activity_coinInfo.globalCurrentPrice != null) {
//    //
//    //                    plusPercent = 5;
//    //                    minusPercent = -5;
//    //
//    //                    if (currentPrice >= 100) {
//    //                        et_price.setText(decimalFormat.format(currentPrice));
//    //                    } else {
//    //                        et_price.setText(String.format("%.2f", currentPrice));
//    //                    }
//    //
//    //                    btn_currentPricePlus.setText(plusPercent + "%");
//    //                    btn_currentPriceMinus.setText(minusPercent + "%");
//    //
//    //                }
//    //            }
//    //        });
//    //    }
//    //
//    //    public void reset(){
//    //
//    //        this.plusPercent = 5;
//    //        this.minusPercent = -5;
//    //
//    //        btn_currentPricePlus.setText(plusPercent + "%");
//    //        btn_currentPriceMinus.setText(minusPercent + "%");
//    //
//    //    }
//}