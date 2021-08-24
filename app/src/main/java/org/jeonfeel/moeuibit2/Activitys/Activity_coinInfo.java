package org.jeonfeel.moeuibit2.Activitys;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import org.jeonfeel.moeuibit2.Favorite;
import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart;
import org.jeonfeel.moeuibit2.Fragment.Fragment_Exchange;
import org.jeonfeel.moeuibit2.Fragment.Fragment_coinOrder;
import org.jeonfeel.moeuibit2.Fragment.Chart.GetUpBitCoins;
import org.jeonfeel.moeuibit2.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import static java.lang.Math.round;

public class Activity_coinInfo extends FragmentActivity {

    private final String TAG = "Activity_coinInfo";

    private TextView tv_coinInfoCoinName,tv_coinInfoCoinPrice,tv_coinInfoCoinDayToDay,tv_coinInfoChangePrice;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private MoEuiBitDatabase db;
    private ImageView iv_coinLogo;
    private String market;
    private String symbol;
    private String koreanName;
    private GetUpBitCoinInfoThread getUpBitCoinInfoThread;
    private Button btn_coinInfoBackSpace;
    private Button btn_bookMark;
    public static Double currentPrice = 0.0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_info);

        db = MoEuiBitDatabase.getInstance(Activity_coinInfo.this);
        FindViewById();
        setCoinInfo();
        setTabLayout();
        setCoinSymbol();
        favoriteInit();
        setBtn_bookMark();

        btn_coinInfoBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }



    private void FindViewById(){
        tv_coinInfoCoinName = findViewById(R.id.tv_coinInfoCoinName);
        tv_coinInfoCoinPrice = findViewById(R.id.tv_coinInfoCoinPrice);
        tv_coinInfoCoinDayToDay = findViewById(R.id.tv_coinInfoCoinDayToDay);
        tv_coinInfoChangePrice = findViewById(R.id.tv_coinInfoChangePrice);
        btn_coinInfoBackSpace = findViewById(R.id.btn_coinInfoBackSpace);
        iv_coinLogo = findViewById(R.id.iv_coinLogo);
        btn_bookMark = findViewById(R.id.btn_bookMark);
    }

    private void setCoinInfo(){

        Intent intent = getIntent();
        koreanName = intent.getStringExtra("koreanName");
        symbol = intent.getStringExtra("symbol");
        String coinUrl = "https://api.upbit.com/v1/ticker?markets="+market;

        GetUpBitCoins getUpBitCoins = new GetUpBitCoins();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = getUpBitCoins.execute(coinUrl).get();

            if (jsonArray != null) {
                JSONObject jsonObject = new JSONObject();

                jsonObject = (JSONObject) jsonArray.get(0);

                currentPrice = jsonObject.getDouble("trade_price");
                Double dayToDay = jsonObject.getDouble("signed_change_rate");
                Double changePrice = jsonObject.getDouble("signed_change_price");


                //--------------------------------------------------
                tv_coinInfoCoinName.setText(koreanName + "( KRW / "+symbol+" )");
                //--------------------------------------------------
                if(currentPrice >= 100){ //만약 100원보다 가격이 높으면 천단위 콤마
                    String currentPriceResult = decimalFormat.format(round(currentPrice));
                    tv_coinInfoCoinPrice.setText(currentPriceResult);
                }else{
                    tv_coinInfoCoinPrice.setText(String.format("%.2f",currentPrice));
                }
                //--------------------------------------------------
                tv_coinInfoCoinDayToDay.setText(String.format("%.2f",dayToDay*100) + "%");
                //--------------------------------------------------
                if(changePrice >= 100){
                    tv_coinInfoChangePrice.setText("+"+ decimalFormat.format(round(changePrice)));
                }else if(changePrice <= -100){
                    tv_coinInfoChangePrice.setText(decimalFormat.format(round(changePrice))+"");
                }else if(changePrice < 100 && changePrice > 0){
                    tv_coinInfoChangePrice.setText("+"+String.format("%.2f",changePrice));
                }else{
                    tv_coinInfoChangePrice.setText(String.format("%.2f",changePrice));
                }

                if(changePrice > 0){
                    tv_coinInfoCoinPrice.setTextColor(Color.parseColor("#B77300"));
                    tv_coinInfoCoinDayToDay.setTextColor(Color.parseColor("#B77300"));
                    tv_coinInfoChangePrice.setTextColor(Color.parseColor("#B77300"));
                }else if(changePrice < 0){
                    tv_coinInfoCoinPrice.setTextColor(Color.parseColor("#0054FF"));
                    tv_coinInfoCoinDayToDay.setTextColor(Color.parseColor("#0054FF"));
                    tv_coinInfoChangePrice.setTextColor(Color.parseColor("#0054FF"));
                }else if(changePrice == 0 ){
                    tv_coinInfoCoinPrice.setTextColor(Color.parseColor("#000000"));
                    tv_coinInfoCoinDayToDay.setTextColor(Color.parseColor("#000000"));
                    tv_coinInfoChangePrice.setTextColor(Color.parseColor("#000000"));
                }
                //--------------------------------------------------
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            getUpBitCoins = null;
        }
    }

    private void setBtn_bookMark(){
        btn_bookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment_Exchange fragment_exchange = new Fragment_Exchange();
                Favorite favorite = db.favoriteDAO().select(market);

                if(favorite != null){
                    db.favoriteDAO().delete(market);
                    btn_bookMark.setBackgroundResource(R.drawable.favorite_off);
                }else{
                    db.favoriteDAO().insert(market);
                    btn_bookMark.setBackgroundResource(R.drawable.favorite_on);
                }
            }
        });
    }

    private void favoriteInit(){
        Favorite favorite = db.favoriteDAO().select(market);

        if(favorite != null){
            btn_bookMark.setBackgroundResource(R.drawable.favorite_on);
        }else{
            btn_bookMark.setBackgroundResource(R.drawable.favorite_off);
        }
    }

    private void setTabLayout(){

        Intent intent = getIntent();
        market = intent.getStringExtra("market");

        Fragment_coinOrder fragment_coinOrder = new Fragment_coinOrder(market,koreanName,symbol);
        Fragment_chart fragment_chart = new Fragment_chart(market);
        TabLayout tab_coinInfo = findViewById(R.id.tab_coinInfo);

        getSupportFragmentManager().beginTransaction().replace(R.id.coinInfo_fragment_container,fragment_coinOrder).commit();
        tab_coinInfo.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;

                if(position == 0){
                    selected = fragment_coinOrder;
                }else if(position == 1){
                    selected = fragment_chart;
                }

                getSupportFragmentManager().beginTransaction().replace(R.id.coinInfo_fragment_container,selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCoinSymbol(){
        String imgUrl = "https://raw.githubusercontent.com/Hi-Im-SeungPil/moeuibitImg/main/coinlogo2/"+symbol+".png";
        Glide.with(Activity_coinInfo.this).load(imgUrl).into(iv_coinLogo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUpBitCoinInfoThread = new GetUpBitCoinInfoThread();
        getUpBitCoinInfoThread.start();
    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();
        if(getUpBitCoinInfoThread != null){
            getUpBitCoinInfoThread.stopRunning();
            currentPrice = null;
        }
    }

    class GetUpBitCoinInfoThread extends Thread {

        private boolean isRunning = true;

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                try {
                    URL url = new URL("https://api.upbit.com/v1/ticker?markets=" + market);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuffer builder = new StringBuffer();

                    String inputString = null;
                    while ((inputString = bufferedReader.readLine()) != null) {
                        builder.append(inputString);
                    }


                    Log.d("qqqq",market);

                    String s = builder.toString();
                    JSONArray jsonCoinInfo = new JSONArray(s);

                    conn.disconnect();
                    bufferedReader.close();
                    inputStream.close();

                    if (jsonCoinInfo != null) {
                        JSONObject jsonObject = new JSONObject();

                        jsonObject = (JSONObject) jsonCoinInfo.get(0);

                        currentPrice = jsonObject.getDouble("trade_price");
                        Double dayToDay = jsonObject.getDouble("signed_change_rate");
                        Double changePrice = jsonObject.getDouble("signed_change_price");

                        Log.d("qqqq",currentPrice+"");
                        Log.d("qqqq",changePrice+"");

                        Activity_coinInfo.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_coinInfoCoinName.setText(koreanName + "( KRW / " + symbol + " )");
                                if (currentPrice >= 100) { //만약 100원보다 가격이 높으면 천단위 콤마
                                    String currentPriceResult = decimalFormat.format(round(currentPrice));
                                    tv_coinInfoCoinPrice.setText(currentPriceResult);
                                } else {
                                    tv_coinInfoCoinPrice.setText(String.format("%.2f", currentPrice));
                                }
                                //--------------------------------------------------
                                tv_coinInfoCoinDayToDay.setText(String.format("%.2f", dayToDay * 100) + "%");
                                //--------------------------------------------------
                                if (changePrice >= 100) {
                                    tv_coinInfoChangePrice.setText("+" + decimalFormat.format(round(changePrice)));
                                } else if (changePrice <= -100) {
                                    tv_coinInfoChangePrice.setText(decimalFormat.format(round(changePrice)) + "");
                                } else if (changePrice < 100 && changePrice > 0) {
                                    tv_coinInfoChangePrice.setText("+" + String.format("%.2f", changePrice));
                                } else {
                                    tv_coinInfoChangePrice.setText(String.format("%.2f", changePrice));
                                }

                                if (changePrice > 0) {
                                    tv_coinInfoCoinPrice.setTextColor(Color.parseColor("#B77300"));
                                    tv_coinInfoCoinDayToDay.setTextColor(Color.parseColor("#B77300"));
                                    tv_coinInfoChangePrice.setTextColor(Color.parseColor("#B77300"));
                                } else if (changePrice < 0) {
                                    tv_coinInfoCoinPrice.setTextColor(Color.parseColor("#0054FF"));
                                    tv_coinInfoCoinDayToDay.setTextColor(Color.parseColor("#0054FF"));
                                    tv_coinInfoChangePrice.setTextColor(Color.parseColor("#0054FF"));
                                } else if (changePrice == 0) {
                                    tv_coinInfoCoinPrice.setTextColor(Color.parseColor("#000000"));
                                    tv_coinInfoCoinDayToDay.setTextColor(Color.parseColor("#000000"));
                                    tv_coinInfoChangePrice.setTextColor(Color.parseColor("#000000"));
                                }
                            }
                        });
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        private void stopRunning(){
            isRunning = false;
        }
    }

}
