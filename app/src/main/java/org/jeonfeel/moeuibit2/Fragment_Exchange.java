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
import android.widget.EditText;
import android.widget.Toast;

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
    boolean isRunning = false;
    private boolean checkTimer = false;


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

        return rootView;
    }

    private void FindViewById(View rootView){
        et_searchCoin =  rootView.findViewById(R.id.et_searchCoin);
        rv_coin = rootView.findViewById(R.id.rv_coin);
    }

    private void setRv_coin(){
        LinearLayoutManagerWrapper linearLayoutManagerWrapper = new LinearLayoutManagerWrapper(getActivity(),LinearLayoutManager.VERTICAL,false);
        rv_coin.setLayoutManager(linearLayoutManagerWrapper);
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

                        CoinDTO coinDTO = new CoinDTO(marketsArray.get(i), koreanNamesArray.get(i), englishNamesArray.get(i)
                                , currentPrice, dayToDay, transactionAmount);

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



    @Override
    public void onResume() { //사용자와 상호작용 하고 있을 때  1초마다 api 받아옴
        super.onResume();

        if(!checkTimer){
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
                GetUpBitCoinsThread getUpBitCoinsThread = new GetUpBitCoinsThread();
                getUpBitCoinsThread.start();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter_rvCoin.setItem(allCoinInfoArray);
                        adapter_rvCoin.notifyDataSetChanged();
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask,0,1000);
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
        @Override
        public void run() {
            super.run();
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

                            CoinDTO coinDTO = new CoinDTO(marketsArray.get(i), koreanNamesArray.get(i), englishNamesArray.get(i)
                                    , currentPrice, dayToDay, transactionAmount);

                            allCoinInfoArray.set(i,coinDTO);
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


        }
    }


//리사이클러뷰 오류 때문에 정의한 클래스
    class LinearLayoutManagerWrapper extends LinearLayoutManager{

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            super.onLayoutChildren(recycler, state);
        }

        public LinearLayoutManagerWrapper(Context context) {
            super(context);
        }

        public LinearLayoutManagerWrapper(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public LinearLayoutManagerWrapper(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }
    }
}