package org.jeonfeel.moeuibit2.Fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoin;
import org.jeonfeel.moeuibit2.DTOS.CoinDTO;
import org.jeonfeel.moeuibit2.Database.Favorite;
import org.jeonfeel.moeuibit2.Fragment.Chart.GetUpBitCoins;
import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Math.round;

public class Fragment_Exchange extends Fragment implements TextWatcher {

    private final String TAG = "Fragment_Exchange";

    private EditText et_searchCoin;
    private RecyclerView rv_coin;
    private Adapter_rvCoin adapter_rvCoin;

    private ArrayList<CoinDTO> allCoinInfoArray = new ArrayList<>();
    private ArrayList<String> marketsArray;
    private ArrayList<String> koreanNamesArray;
    private ArrayList<String> englishNamesArray;
    private ArrayList<Integer> favoritePosition;
    private List<Favorite> favorites;
    private MoEuiBitDatabase db;
    private Switch sch_favorite;
    private boolean switchIsChecked = false;
    TextView tv_nonFavorite;


    private String markets;
    private int orderByCurrentPrice = 0;
    private int orderByDayToDay = 0;
    private int orderByTransactionAmount = 0;
    private Button btn_orderByCurrentPrice,btn_orderByDayToDay,btn_orderByTransactionAmount;
    private GetUpBitCoinsThread getUpBitCoinsThread;

    private HashMap<String,Integer> orderPosition;
    private Handler handler = null;
    private Timer timer = null;
    private String onText="";

    public Fragment_Exchange() {
        // Required empty public constructor
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
        db = MoEuiBitDatabase.getInstance(getActivity());
        setRv_coin();
        getAllUpBitCoins();
        adapter_rvCoin = new Adapter_rvCoin(allCoinInfoArray, getActivity());
        rv_coin.setAdapter(adapter_rvCoin);
        getUpBitCoinsInfo();
        setTextWatcher();
        setOrderByBtn();
        setSch_favorite();

        return rootView;
    }

    private void FindViewById(View rootView){
        et_searchCoin =  rootView.findViewById(R.id.et_searchCoin);
        rv_coin = rootView.findViewById(R.id.rv_coin);
        btn_orderByCurrentPrice = rootView.findViewById(R.id.btn_orderByCurrentPrice);
        btn_orderByDayToDay = rootView.findViewById(R.id.btn_orderByDayToDay);
        btn_orderByTransactionAmount = rootView.findViewById(R.id.btn_orderByTransactionAmount);
        sch_favorite = rootView.findViewById(R.id.sch_favorite);
        tv_nonFavorite = rootView.findViewById(R.id.tv_nonFavorite);
    }

    private void setRv_coin(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rv_coin.setLayoutManager(linearLayoutManager);
        rv_coin.setHasFixedSize(true);
    }

    //관심목록 초기설정
    private void initFavorite(){

        favoritePosition = null;
        favorites = null;

        favoritePosition = new ArrayList<>();
        favorites = db.favoriteDAO().getAll();

        for (int i = 0; i < favorites.size(); i++) {
            favoritePosition.add(orderPosition.get(favorites.get(i).getMarket()));
        }

        Collections.sort(favoritePosition);
        adapter_rvCoin.setMarkets(favoritePosition);
    }

    //관심목록 설정

    private void setSch_favorite(){
        sch_favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if(isChecked){

                    initFavorite();

                    switchIsChecked = true;

                    if(favorites.size() != 0) {

                        adapter_rvCoin.setFavoriteStatus(true);
                        adapter_rvCoin.getFilter().filter(onText);

                        if (timer == null && handler == null) {
                            timer = new Timer(true); //인자가 Daemon 설정인데 true 여야 죽지 않음.
                            handler = new Handler();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        public void run() {
                                            if (!onText.equals("") || switchIsChecked) {
                                                adapter_rvCoin.getFilter().filter(onText);
                                            }
                                        }
                                    });
                                }
                            }, 500, 1000);
                        }
                    }else{
                        rv_coin.setVisibility(View.GONE);
                        tv_nonFavorite.setVisibility(View.VISIBLE);
                    }
                }else{

                    if(rv_coin.getVisibility() == View.GONE){
                        tv_nonFavorite.setVisibility(View.GONE);
                        rv_coin.setVisibility(View.VISIBLE);
                    }

                    switchIsChecked = false;
                    adapter_rvCoin.setFavoriteStatus(false);
                    adapter_rvCoin.getFilter().filter(onText);
                }
            }
        });
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

            orderPosition = new HashMap<>();

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
                    }
                    orderByCoins();
                    adapter_rvCoin.setItem(allCoinInfoArray);
                    adapter_rvCoin.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                getUpBitApi = null;
            }
    }
    // 정렬 토글버튼 세팅
    private void setOrderByBtn(){
        OrderWays orderWays = new OrderWays();
        btn_orderByTransactionAmount.setOnClickListener(orderWays);
        btn_orderByDayToDay.setOnClickListener(orderWays);
        btn_orderByCurrentPrice.setOnClickListener(orderWays);
    }

    private void orderByFavoriteCoins(){

        if(switchIsChecked) {

            for (int i = 0; i < favorites.size(); i++) {
                favoritePosition.set(i, orderPosition.get(favorites.get(i).getMarket()));
                Log.d("qqqq",favorites.get(i).getMarket());
            }

            Collections.sort(favoritePosition);
            adapter_rvCoin.setMarkets(favoritePosition);
            adapter_rvCoin.getFilter().filter(onText);
        }
    }

    private void orderByCoins(){

        if(orderByCurrentPrice == 0 && orderByDayToDay ==0 && orderByTransactionAmount == 0){

            btn_orderByCurrentPrice.setText("현재가↓↑");
            btn_orderByDayToDay.setText("전일대비↓↑");
            btn_orderByTransactionAmount.setText("거래대금↓↑");

            CoinDTO.orderStatus = "transactionAmount";
            Collections.sort(allCoinInfoArray);
            Collections.reverse(allCoinInfoArray);

            if(!switchIsChecked)
            adapter_rvCoin.notifyDataSetChanged();
        }else {

            if (orderByCurrentPrice == 0) {
                btn_orderByCurrentPrice.setText("현재가↓↑");
            } else if (orderByCurrentPrice == 1) {
                btn_orderByCurrentPrice.setText("현재가↓");
                CoinDTO.orderStatus = "currentPrice";
                Collections.sort(allCoinInfoArray);
                Collections.reverse(allCoinInfoArray);

            } else if (orderByCurrentPrice == 2) {
                btn_orderByCurrentPrice.setText("현재가↑");
                CoinDTO.orderStatus = "currentPrice";
                Collections.sort(allCoinInfoArray);
            }

            if (orderByDayToDay == 0) {
                btn_orderByDayToDay.setText("전일대비↓↑");
            } else if (orderByDayToDay == 1) {
                btn_orderByDayToDay.setText("전일대비↓");
                CoinDTO.orderStatus = "dayToDay";
                Collections.sort(allCoinInfoArray);
                Collections.reverse(allCoinInfoArray);

            } else if (orderByDayToDay == 2) {
                btn_orderByDayToDay.setText("전일대비↑");
                CoinDTO.orderStatus = "dayToDay";
                Collections.sort(allCoinInfoArray);
            }

            if (orderByTransactionAmount == 0) {
                btn_orderByTransactionAmount.setText("거래대금↓↑");
            } else if (orderByTransactionAmount == 1) {
                btn_orderByTransactionAmount.setText("거래대금↓");
                CoinDTO.orderStatus = "transactionAmount";
                Collections.sort(allCoinInfoArray);
                Collections.reverse(allCoinInfoArray);

            } else if (orderByTransactionAmount == 2) {
                btn_orderByTransactionAmount.setText("거래대금↑");
                CoinDTO.orderStatus = "transactionAmount";
                Collections.sort(allCoinInfoArray);
            }
            if(!switchIsChecked)
            adapter_rvCoin.notifyDataSetChanged();
        }

        if(orderByTransactionAmount != 0){
            btn_orderByTransactionAmount.setTextColor(Color.parseColor("#FFFFFFFF"));
            btn_orderByTransactionAmount.setBackgroundColor(Color.parseColor("#0F0F5C"));
        }else if(orderByDayToDay != 0){
            btn_orderByDayToDay.setTextColor(Color.parseColor("#FFFFFFFF"));
            btn_orderByDayToDay.setBackgroundColor(Color.parseColor("#0F0F5C"));
        }else if(orderByCurrentPrice != 0){
            btn_orderByCurrentPrice.setTextColor(Color.parseColor("#FFFFFFFF"));
            btn_orderByCurrentPrice.setBackgroundColor(Color.parseColor("#0F0F5C"));
        }

        if(!orderPosition.isEmpty()){
            orderPosition.clear();
        }

        for(int i =0; i < allCoinInfoArray.size(); i++){
            orderPosition.put(allCoinInfoArray.get(i).getMarket(),i);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getUpBitCoinsThread = new GetUpBitCoinsThread();
        getUpBitCoinsThread.start();
    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();
        if(getUpBitCoinsThread != null) {
            getUpBitCoinsThread.stopThread();
            getUpBitCoinsThread = null;
        }
    }

    private void setTextWatcher(){
        et_searchCoin.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        onText = charSequence.toString();
        adapter_rvCoin.getFilter().filter(onText);
    }

    @Override
    public void afterTextChanged(Editable editable) {

        if(timer == null && handler == null) {
            timer = new Timer(true); //인자가 Daemon 설정인데 true 여야 죽지 않음.
            handler = new Handler();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable(){
                        public void run(){
                            if(!onText.equals("") || switchIsChecked) {
                                adapter_rvCoin.getFilter().filter(onText);
                            }
                        }
                    });
                }
            }, 500, 1000);
        }
    }

    //정렬방식 class
    class OrderWays implements View.OnClickListener {

        @Override
        public void onClick(View view) {
           Button[] btns = {btn_orderByCurrentPrice,btn_orderByDayToDay,btn_orderByTransactionAmount};

           int selected = view.getId();

           for(int i = 0; i < 3; i++){
               if(btns[i].getId() != selected) {
                   if (btns[i].getId() == R.id.btn_orderByCurrentPrice) {
                       btn_orderByCurrentPrice.setText("현재가↓↑");
                       orderByCurrentPrice = 0;
                   }else if (btns[i].getId() == R.id.btn_orderByTransactionAmount) {
                       btn_orderByTransactionAmount.setText("거래대금↓↑");
                       orderByTransactionAmount = 0;
                   }else if (btns[i].getId() == R.id.btn_orderByDayToDay) {
                       btn_orderByDayToDay.setText("전일대비↓↑");
                       orderByDayToDay = 0;
                   }
                   btns[i].setTextColor(Color.parseColor("#ACABAB"));
                   btns[i].setBackgroundColor(Color.parseColor("#FAFAFA"));
                }else{
                       if(R.id.btn_orderByCurrentPrice == selected){
                               if(orderByCurrentPrice != 2) {
                                   orderByCurrentPrice++;
                               }else{
                                   orderByCurrentPrice = 0;
                                   btns[i].setTextColor(Color.parseColor("#ACABAB"));
                                   btns[i].setBackgroundColor(Color.parseColor("#FAFAFA"));
                               }
                            }else if(R.id.btn_orderByTransactionAmount == selected){
                           if(orderByTransactionAmount != 2) {
                               orderByTransactionAmount++;
                           }else{
                               orderByTransactionAmount = 0;
                               btns[i].setTextColor(Color.parseColor("#ACABAB"));
                               btns[i].setBackgroundColor(Color.parseColor("#FAFAFA"));
                           }
                       }else if(R.id.btn_orderByDayToDay == selected){
                           if(orderByDayToDay != 2) {
                               orderByDayToDay++;
                           }else{
                               orderByDayToDay = 0;
                               btns[i].setTextColor(Color.parseColor("#ACABAB"));
                               btns[i].setBackgroundColor(Color.parseColor("#FAFAFA"));
                           }
                       }
                   }
           }
            orderByCoins();
            orderByFavoriteCoins();
        }
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
                            String[] symbol = {};
                            if (marketsArray.size() != 0) {
                                symbol = marketsArray.get(i).split("-");
                            } else {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            CoinDTO coinDTO = null;

                            if(marketsArray != null && koreanNamesArray != null && englishNamesArray != null && symbol.length != 0) {
                                coinDTO = new CoinDTO(marketsArray.get(i), koreanNamesArray.get(i), englishNamesArray.get(i)
                                        , currentPrice, dayToDay, transactionAmount, symbol[1]);
                            }else{
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            if (orderPosition.get(marketsArray.get(i)) != null && allCoinInfoArray.size() != 0) {

                                int position = orderPosition.get(marketsArray.get(i));
                                allCoinInfoArray.set(position, coinDTO);

                            } else {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(et_searchCoin.length() == 0) {
                                        adapter_rvCoin.notifyDataSetChanged();
                                    }
                                }
                            });
                        }else{
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    getAllUpBitCoins();
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