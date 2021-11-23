package org.jeonfeel.moeuibit2.Fragment.investmentDetails;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jeonfeel.moeuibit2.Activitys.Activity_portfolio;
import org.jeonfeel.moeuibit2.Adapters.Adapter_rvMyCoins;
import org.jeonfeel.moeuibit2.CheckNetwork;
import org.jeonfeel.moeuibit2.CustomLodingDialog;
import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO;
import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.Database.MyCoin;
import org.jeonfeel.moeuibit2.MainActivity;
import org.jeonfeel.moeuibit2.R;
import org.jeonfeel.moeuibit2.Database.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.round;

public class Fragment_investmentDetails extends Fragment {
    final String TAG ="investmentDetails";
    private TextView tv_myKoreanWon, tv_myTotalProperty, tv_totalBuyOut, tv_totalEvaluation, tv_evaluationGainLoss, tv_yield;
    private MoEuiBitDatabase db;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private ArrayList<MyCoinsDTO> myCoinsDTOS;
    private RecyclerView rv_myCoins;
    private String markets = "";
    private List<MyCoin> myCoins;
    private long totalBuyOut,myKoreanWon = 0;
    private GetMyCoins getMyCoins;
    private ArrayList<Double> currentPrices;
    private Adapter_rvMyCoins adapter_rvMyCoins;
    private Context context;
    private CustomLodingDialog customLodingDialog;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private EarnKrw earnKrw;
    private int checkSecond = 0;
    private ArrayList<String> marketList;
    private HashMap<String,Integer> hashMap;
    public Fragment_investmentDetails(){}

    public Fragment_investmentDetails(CustomLodingDialog customLodingDialog) {
        this.customLodingDialog = customLodingDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_investment_details, container, false);

        context = getActivity();
        earnKrw = new EarnKrw();
        FindViewById(rootView);
        db = MoEuiBitDatabase.getInstance(context);

        if(rewardedInterstitialAd == null) {
            MobileAds.initialize(context, new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    loadAd();
                }
            });
        }

        setRv_myCoins();
        init();
        setCoinOrder(rootView);
        setBtn_earningKrw(rootView);
        setBtn_portfolio(rootView);

        return rootView;
    }

    //아이디찾기
    private void FindViewById(View rootView) {
        tv_myKoreanWon = rootView.findViewById(R.id.tv_myKoreanWon);
        tv_myTotalProperty = rootView.findViewById(R.id.tv_myTotalProperty);
        tv_totalBuyOut = rootView.findViewById(R.id.tv_totalBuyOut);
        tv_totalEvaluation = rootView.findViewById(R.id.tv_totalEvaluation);
        tv_evaluationGainLoss = rootView.findViewById(R.id.tv_evaluationGainLoss);
        tv_yield = rootView.findViewById(R.id.tv_yield);
        rv_myCoins = rootView.findViewById(R.id.rv_myCoins);
    }

    private void setCoinOrder(View rootView){
        Button btn_investmentDetailOrderByName = rootView.findViewById(R.id.btn_investmentDetailOrderByName);
        Button btn_investmentDetailOrderByYield = rootView.findViewById(R.id.btn_investmentDetailOrderByYield);
        CoinOrder coinOrder = new CoinOrder(btn_investmentDetailOrderByName,btn_investmentDetailOrderByYield,myCoinsDTOS,currentPrices);
        hashMap = coinOrder.getHashMap();
    }

    private void setRv_myCoins(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false);
        rv_myCoins.setLayoutManager(linearLayoutManager);
    }

    //초기설정
    private void init() {

        StringBuilder stringBuilder = null;
        myCoinsDTOS = null;
        currentPrices = null;
        marketList = null;
        totalBuyOut = 0;

        stringBuilder = new StringBuilder();
        myCoinsDTOS = new ArrayList<>();
        currentPrices = new ArrayList<>();
        marketList = new ArrayList<>();

        //보유 krw 설정
        User user = db.userDAO().getAll();

        if (user != null) {
            myKoreanWon = user.getKrw();
            tv_myTotalProperty.setText(decimalFormat.format(myKoreanWon));
        }else{
            myKoreanWon = 0;
        }

        // 총 매수 설정
        myCoins = db.myCoinDAO().getAll();

        //보유 코인 정보 get
        if(myCoins.size() != 0) {
            for (int i = 0; i < myCoins.size(); i++) {
                Double purchasePrice = myCoins.get(i).getPurchasePrice();
                Double quantity = myCoins.get(i).getQuantity();
                String market = myCoins.get(i).getMarket();

                stringBuilder.append(market).append(",");
                // 객체 만들어서
                MyCoinsDTO myCoinsDTO = new MyCoinsDTO(myCoins.get(i).getKoreanCoinName(), myCoins.get(i).getSymbol(), quantity, purchasePrice, 0.0);
                myCoinsDTOS.add(myCoinsDTO);
                marketList.add(market);

                totalBuyOut += round(purchasePrice * quantity);
                currentPrices.add(0.0);
            }
            //markets 설정
            stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
            markets = stringBuilder.toString();
            //리사이클러뷰
        }
        tv_totalBuyOut.setText(decimalFormat.format(totalBuyOut));
        tv_myKoreanWon.setText(decimalFormat.format(myKoreanWon));
        adapter_rvMyCoins = new Adapter_rvMyCoins(myCoinsDTOS,context);
        rv_myCoins.setAdapter(adapter_rvMyCoins);
        adapter_rvMyCoins.setCurrentPrices(currentPrices);
        adapter_rvMyCoins.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();

        int networkStatus = CheckNetwork.CheckNetwork(context);

        if(networkStatus == 0){
            Toast.makeText(context, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
            if(customLodingDialog!=null && customLodingDialog.isShowing())
                customLodingDialog.dismiss();
        }
        if(myCoins.size() != 0) {
            if(customLodingDialog!=null && customLodingDialog.isShowing())
                customLodingDialog.dismiss();
            getMyCoins = new GetMyCoins();
            getMyCoins.start();
        }else{
            if(customLodingDialog!=null && customLodingDialog.isShowing())
                customLodingDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getMyCoins != null) {
            getMyCoins.stopThread();
            getMyCoins = null;
        }
    }
    private void setBtn_portfolio(View rootView){

        Button btn_portfolio = rootView.findViewById(R.id.btn_portfolio);
        btn_portfolio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, Activity_portfolio.class);
                startActivity(intent);
            }
        });
    }

    // krw 충전
    private void setBtn_earningKrw(View rootView){
        Button btn_earningKrw = rootView.findViewById(R.id.btn_earningKrw);
        btn_earningKrw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if(CheckNetwork.CheckNetwork(context) == 0){
                        Toast.makeText(context, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (checkSecond == 1) {
                        Toast.makeText(context, "충전 후 5초뒤에 충전 가능합니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("KRW 충전")
                            .setMessage("짧은 광고를 시청하시면 5,000,000 KRW가 보상으로 지급됩니다.")
                            .setPositiveButton("충전", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    if (rewardedInterstitialAd != null) {

                                        rewardedInterstitialAd.show((Activity) context, earnKrw);

                                        MobileAds.initialize(context, new OnInitializationCompleteListener() {
                                            @Override
                                            public void onInitializationComplete(InitializationStatus initializationStatus) {
                                                loadAd();
                                            }
                                        });
                                    } else{
                                        Toast.makeText(context, "잠시만 기다려 주세요.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
        });
    }

    //1초마다 업비트에서 코인 가격 받아오기
    public class GetMyCoins extends Thread{

        private boolean isRunning = true;

        @Override
        public void run() {
            super.run();

            while (isRunning) {
                try {
                        Double totalEvaluation = 0.0;

                        URL url = new URL("https://api.upbit.com/v1/ticker?markets=" + markets);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        StringBuffer builder = new StringBuffer();


                        Log.d("Qqqq",markets);
                        String inputString = null;

                        while ((inputString = bufferedReader.readLine()) != null) {
                            builder.append(inputString);
                        }

                        String s = builder.toString();
                        JSONArray jsonCoinInfo = new JSONArray(s);

                        conn.disconnect();
                        bufferedReader.close();
                        inputStream.close();

                        if (jsonCoinInfo != null && myCoins.size() == jsonCoinInfo.length()) {
                            JSONObject jsonObject;

                            for (int i = 0; i < jsonCoinInfo.length(); i++) {
                                jsonObject = (JSONObject) jsonCoinInfo.get(i);

                                Double currentPrice = jsonObject.getDouble("trade_price");
                                Double quantity = myCoins.get(i).getQuantity();
                                String market = jsonObject.getString("market");

                                totalEvaluation += currentPrice * quantity;
                                currentPrices.set(hashMap.get(market), currentPrice);
//                                currentPrices.set(i, currentPrice);
                            }

                            long longTotalEvaluation = round(totalEvaluation);
                            Double yield = (longTotalEvaluation - totalBuyOut) / Double.valueOf(totalBuyOut) * 100; //퍼센트 계산
                            long evaluationGainLoss =  (longTotalEvaluation - totalBuyOut);
                            long myTotalProperty =  (myKoreanWon + longTotalEvaluation);
                            String yieldResult = String.format("%.2f", yield);

                            Log.d("totalEvaluation", totalEvaluation + "");
                            Log.d("totalBuyOut", totalBuyOut + "");
                            Log.d("evaluationGainLoss", evaluationGainLoss + "");

                            if (context != null) {
                                ((MainActivity)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (longTotalEvaluation - totalBuyOut > 0) {
                                            tv_yield.setTextColor(Color.parseColor("#B77300"));
                                            tv_evaluationGainLoss.setTextColor(Color.parseColor("#B77300"));
                                        } else if (longTotalEvaluation - totalBuyOut < 0) {
                                            tv_yield.setTextColor(Color.parseColor("#0054FF"));
                                            tv_evaluationGainLoss.setTextColor(Color.parseColor("#0054FF"));
                                        } else {
                                            tv_yield.setTextColor(Color.parseColor("#000000"));
                                            tv_evaluationGainLoss.setTextColor(Color.parseColor("#000000"));
                                        }

                                        tv_totalEvaluation.setText(decimalFormat.format(longTotalEvaluation));
                                        tv_myTotalProperty.setText(decimalFormat.format(myTotalProperty));
                                        tv_evaluationGainLoss.setText(decimalFormat.format(evaluationGainLoss));
                                        tv_yield.setText(yieldResult + "%");

                                        adapter_rvMyCoins.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                }catch (Exception e) {

                    //코인 매수한게 사라지면 파이어베이스에서 사라진 코인 마켓을 불러와서 내장 db에서 삭제 하고
                    //markets 목록을 초기화 해서 다시 실행되게 한다
                    if (myCoins.size() != 0) {
                        Log.d(TAG,"catch실행");
                        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                        mDatabase.child("removeCoin").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                String market = snapshot.getValue(String.class);
                                String[] marketArray = market.split(",");

                                for (int i = 0; i < marketArray.length; i++) {

                                    db.myCoinDAO().delete(marketArray[i]);

                                    int marketIndex = marketList.indexOf(marketArray[i]);
                                    Log.d("marketIndex", marketIndex + "");

                                    if (marketIndex != -1) {
                                        myCoinsDTOS.remove(marketIndex);
                                        marketList.remove(marketIndex);
                                    }

                                }

                                markets = "";
                                StringBuilder stringBuilder = new StringBuilder();

                                for(int i = 0; i < marketList.size(); i++) {
                                    stringBuilder.append(marketList.get(i)).append(",");
                                }

                                stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
                                markets = stringBuilder.toString();

                                Log.d("markets",markets);

                                myCoins = db.myCoinDAO().getAll();

                                if (context != null) {
                                    ((MainActivity)context).runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter_rvMyCoins.notifyDataSetChanged();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        public void stopThread(){
            isRunning = false;
        }
    }

    public void loadAd() {
        // Use the test ad unit ID to load an ad.
        RewardedInterstitialAd.load(context, "ca-app-pub-8481465476603755/3905762551",
                new AdRequest.Builder().build(),  new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        Log.e(TAG, "onAdLoaded");
                    }
                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        Log.e(TAG, "onAdFailedToLoad");
                    }
                });
    }

    public class EarnKrw implements OnUserEarnedRewardListener{

        @Override
        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            User user = db.userDAO().getAll();

            if(user == null){
                db.userDAO().insert();
            }else{
                db.userDAO().updatePlusMoney(5000000);
            }

            User user1 = db.userDAO().getAll();
            myKoreanWon = user1.getKrw();
            tv_myKoreanWon.setText(decimalFormat.format(myKoreanWon));

            long myTotalProperty = Long.parseLong(tv_myTotalProperty.getText().toString().replaceAll(",",""));
            tv_myTotalProperty.setText(decimalFormat.format(myTotalProperty+5000000));

            checkSecond = 1;

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //지연시키길 원하는 밀리초 뒤에 동작
                    checkSecond = 0;
                }
            }, 5000 );

            user = null;
        }
    }

}
