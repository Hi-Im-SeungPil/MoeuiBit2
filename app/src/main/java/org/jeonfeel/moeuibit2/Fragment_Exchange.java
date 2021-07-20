package org.jeonfeel.moeuibit2;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.round;

public class Fragment_Exchange extends Fragment implements TextWatcher {

    private final String TAG = "Fragment_Exchange";

    EditText et_searchCoin;
    RecyclerView rv_coin;
    Adapter_rvCoin adapter_rvCoin;

    ArrayList<CoinDTO> allCoinInfoArray = new ArrayList<>();
    ArrayList<String> marketsArray;
    ArrayList<String> koreanNamesArray;
    ArrayList<String> englishNamesArray;

    String markets;
    TimerTask timerTask;
    Timer timer;
    private boolean checkTimer = false;
    int orderByCurrentPrice = 0;
    int orderByDayToDay = 0;
    int orderByTransactionAmount = 0;
    private Button btn_orderByCurrentPrice,btn_orderByDayToDay,btn_orderByTransactionAmount;
    private GetUpBitCoinsThread getUpBitCoinsThread;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters

    public Fragment_Exchange() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment exchange.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_Exchange newInstance(String param1, String param2) {
        Fragment_Exchange fragment = new Fragment_Exchange();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exchange, container, false);
        FindViewById(rootView);
        setRv_coin();
        getAllUpBitCoins();
        adapter_rvCoin = new Adapter_rvCoin(allCoinInfoArray, getActivity());
        rv_coin.setAdapter(adapter_rvCoin);
        getUpBitCoinsInfo();
        setTextWatcher();
        setOrderByTog();

        return rootView;
    }

    private void FindViewById(View rootView){
        et_searchCoin =  rootView.findViewById(R.id.et_searchCoin);
        rv_coin = rootView.findViewById(R.id.rv_coin);
        btn_orderByCurrentPrice = rootView.findViewById(R.id.btn_orderByCurrentPrice);
        btn_orderByDayToDay = rootView.findViewById(R.id.btn_orderByDayToDay);
        btn_orderByTransactionAmount = rootView.findViewById(R.id.btn_orderByTransactionAmount);
    }

    private void setRv_coin(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rv_coin.setLayoutManager(linearLayoutManager);
        rv_coin.setHasFixedSize(true);
    }

    private void getAllUpBitCoins(){

        marketsArray = new ArrayList<>();
        koreanNamesArray = new ArrayList<>();
        englishNamesArray = new ArrayList<>();

        String koreanName, englishName;

        String url = "https://api.upbit.com/v1/market/all"; // 업비트 모든 코인 종류
        GetUpBitCoins getUpBitApi = new GetUpBitCoins();
        try {
            JSONArray jsonArray = new JSONArray();

            jsonArray = getUpBitApi.execute(url).get();

            if (jsonArray != null) {

                JSONObject jsonObject = new JSONObject();

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = (JSONObject) jsonArray.get(i);

                    if (jsonObject.getString("market").contains("KRW")) { //KRW 마켓만 골라옴
                        String market = jsonObject.getString("market");
                        koreanName = jsonObject.getString("korean_name");
                        englishName = jsonObject.getString("english_name");

                        marketsArray.add(market);
                        koreanNamesArray.add(koreanName);
                        englishNamesArray.add(englishName);
                    }
                }
            } // 인터넷 연결 확인 추가해야함
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            getUpBitApi = null;
        }

        StringBuilder builder = new StringBuilder(); //StringBuilder 를 사용해서 업비트 모든 코인 종류 받아옴

        for (int i = 0; i < marketsArray.size(); i++) {
            builder.append(marketsArray.get(i)).append(",");
        }

        builder.deleteCharAt(builder.lastIndexOf(","));
        markets = builder.toString();
    }

        //업비트 api를 받아온다.
        public void getUpBitCoinsInfo() {

            if(allCoinInfoArray.size() != 0){
                allCoinInfoArray.clear();
            }

            String allCoinsInfoUrl = "https://api.upbit.com/v1/ticker?markets=" + markets;

            GetUpBitCoins getUpBitApi = new GetUpBitCoins();

            try {
                JSONArray jsonArray = new JSONArray();
                jsonArray = getUpBitApi.execute(allCoinsInfoUrl).get();

                if (jsonArray != null) {
                    JSONObject jsonObject = new JSONObject();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);

                        Double currentPrice = jsonObject.getDouble("trade_price");
                        Double dayToDay = jsonObject.getDouble("signed_change_rate");
                        Double transactionAmount = jsonObject.getDouble("acc_trade_price_24h");
                        String[] symbol = marketsArray.get(i).split("-");

                        CoinDTO coinDTO = new CoinDTO(marketsArray.get(i), koreanNamesArray.get(i), englishNamesArray.get(i)
                                , currentPrice, dayToDay, transactionAmount,symbol[1]);


                        allCoinInfoArray.add(coinDTO);
                        adapter_rvCoin.setItem(allCoinInfoArray);
                        adapter_rvCoin.notifyDataSetChanged();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                getUpBitApi = null;
            }
    }
    // 정렬 토글버튼 세팅
    private void setOrderByTog(){

        btn_orderByCurrentPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderByCurrentPrice++;

                if(orderByCurrentPrice > 2){
                    orderByCurrentPrice = 0;
                }

                orderByDayToDay = 0;
                orderByTransactionAmount = 0;
                btn_orderByDayToDay.setText("전일대비X");
                btn_orderByTransactionAmount.setText("거래대금X");
            }
        });

        btn_orderByDayToDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderByDayToDay++;

                if(orderByDayToDay > 2){
                    orderByDayToDay = 0;
                }

                orderByCurrentPrice = 0;
                orderByTransactionAmount = 0;
                btn_orderByCurrentPrice.setText("현재가X");
                btn_orderByTransactionAmount.setText("거래대금X");

            }
        });

        btn_orderByTransactionAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderByTransactionAmount++;

                if(orderByTransactionAmount > 2){
                    orderByTransactionAmount = 0;
                }

                orderByCurrentPrice = 0;
                orderByDayToDay = 0;

                btn_orderByCurrentPrice.setText("현재가X");
                btn_orderByDayToDay.setText("전일대비X");

            }
        });
    }

    private void orderByCoins(){
        if(orderByCurrentPrice == 0 && orderByDayToDay ==0 && orderByTransactionAmount == 0){
            CoinDTO.orderStatus = "transactionAmount";
            Collections.sort(allCoinInfoArray);
            Collections.reverse(allCoinInfoArray);
        }else if(orderByCurrentPrice == 1){
            btn_orderByCurrentPrice.setText("현 내림");
            CoinDTO.orderStatus = "currentPrice";
            Collections.sort(allCoinInfoArray);
            Collections.reverse(allCoinInfoArray);
        }else if(orderByCurrentPrice == 2){
            btn_orderByCurrentPrice.setText("현 오름");
            CoinDTO.orderStatus = "currentPrice";
            Collections.sort(allCoinInfoArray);
        }else if(orderByDayToDay == 1){
            btn_orderByDayToDay.setText("전일 내림");
            CoinDTO.orderStatus = "dayToDay";
            Collections.sort(allCoinInfoArray);
            Collections.reverse(allCoinInfoArray);
        }else if(orderByDayToDay == 2){
            btn_orderByDayToDay.setText("전일 오름");
            CoinDTO.orderStatus = "dayToDay";
            Collections.sort(allCoinInfoArray);
        }else if(orderByTransactionAmount == 1){
            btn_orderByTransactionAmount.setText("대금 내림");
            CoinDTO.orderStatus = "transactionAmount";
            Collections.sort(allCoinInfoArray);
            Collections.reverse(allCoinInfoArray);
        }else if(orderByTransactionAmount == 2){
            btn_orderByTransactionAmount.setText("대금 오름");
            CoinDTO.orderStatus = "transactionAmount";
            Collections.sort(allCoinInfoArray);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getUpBitCoinsThread = new GetUpBitCoinsThread();
        getUpBitCoinsThread.start();
    }

    @Override
    public void onResume() { //사용자와 상호작용 하고 있을 때  1초마다 api 받아옴
        super.onResume();

    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();
        if(getUpBitCoinsThread != null) {
            getUpBitCoinsThread.stopThread();
        }

    }

    private void setTextWatcher(){
        et_searchCoin.setCursorVisible(false);
        et_searchCoin.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        adapter_rvCoin.getFilter().filter(charSequence.toString());
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    //스레드에서 처리하기 위해..
    class GetUpBitCoinsThread extends Thread {

        private boolean isRunning = true;

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                try {
                    URL url = new URL("https://api.upbit.com/v1/ticker?markets=" + markets);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                    StringBuffer builder = new StringBuffer();

                    String inputString = null;
                    while ((inputString = bufferedReader.readLine()) != null) {
                        builder.append(inputString);
                    }

                    String s = builder.toString();
                    JSONArray jsonCoinInfo = new JSONArray(s);

                    conn.disconnect();
                    bufferedReader.close();
                    inputStream.close();

                    if (jsonCoinInfo != null) {
                        JSONObject jsonObject = new JSONObject();

                        for (int i = 0; i < jsonCoinInfo.length(); i++) {
                            jsonObject = (JSONObject) jsonCoinInfo.get(i);

                            Double currentPrice = jsonObject.getDouble("trade_price");
                            Double dayToDay = jsonObject.getDouble("signed_change_rate");
                            Double transactionAmount = jsonObject.getDouble("acc_trade_price_24h");
                            String[] symbol = marketsArray.get(i).split("-");

                            CoinDTO coinDTO = new CoinDTO(marketsArray.get(i), koreanNamesArray.get(i), englishNamesArray.get(i)
                                    , currentPrice, dayToDay, transactionAmount, symbol[1]);

                            allCoinInfoArray.set(i, coinDTO);

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter_rvCoin.setItem(allCoinInfoArray);
                                    adapter_rvCoin.notifyDataSetChanged();
                                }
                            });
                        }
                        orderByCoins();
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
        private void stopThread(){
            isRunning = false;
        }
    }
}