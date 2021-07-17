package org.jeonfeel.moeuibit2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.round;

public class Activity_coinInfo extends FragmentActivity {

    private TextView tv_coinInfoCoinName,tv_coinInfoCoinPrice,tv_coinInfoCoinDayToDay,tv_coinInfoChangePrice;
    private DecimalFormat decimalFormat;
    private TimerTask timerTask;
    private Timer timer;
    private boolean checkTimer;
    private String market;
    public static Double openingPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_info);

        decimalFormat = new DecimalFormat("###,###");
        FindViewById();
        setCoinInfo();
        setTabLayout();

    }

    private void FindViewById(){
        tv_coinInfoCoinName = findViewById(R.id.tv_coinInfoCoinName);
        tv_coinInfoCoinPrice = findViewById(R.id.tv_coinInfoCoinPrice);
        tv_coinInfoCoinDayToDay = findViewById(R.id.tv_coinInfoCoinDayToDay);
        tv_coinInfoChangePrice = findViewById(R.id.tv_coinInfoChangePrice);
    }

    private void setCoinInfo(){

        Intent intent = getIntent();
        String koreanName = intent.getStringExtra("koreanName");

        String coinUrl = "https://api.upbit.com/v1/ticker?markets="+market;

        GetUpBitCoins getUpBitCoins = new GetUpBitCoins();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = getUpBitCoins.execute(coinUrl).get();

            if (jsonArray != null) {
                JSONObject jsonObject = new JSONObject();

                jsonObject = (JSONObject) jsonArray.get(0);

                Double currentPrice = jsonObject.getDouble("trade_price");
                Double dayToDay = jsonObject.getDouble("signed_change_rate");
                Double changePrice = jsonObject.getDouble("signed_change_price");

                //--------------------------------------------------
                tv_coinInfoCoinName.setText(koreanName + "( "+market+" )");
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

    private void setTabLayout(){

        Intent intent = getIntent();
        market = intent.getStringExtra("market");

        Fragment_coinOrder fragment_coinOrder = new Fragment_coinOrder(market);
        TabLayout tab_coinInfo = findViewById(R.id.tab_coinInfo);

        getSupportFragmentManager().beginTransaction().replace(R.id.coinInfo_fragment_container,fragment_coinOrder).commit();
        tab_coinInfo.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;

                if(position == 0){
                    selected = fragment_coinOrder;
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



    @Override
    public void onResume() { //사용자와 상호작용 하고 있을 때  1초마다 api 받아옴
        super.onResume();
        if(!checkTimer) {
            setTimerTask();
            checkTimer = true;
        }
    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();
        if(checkTimer) {
            timerTask.cancel();
            checkTimer = false;
        }
    }

    private void setTimerTask(){
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Activity_coinInfo.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setCoinInfo();
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,1000);
    }

}
