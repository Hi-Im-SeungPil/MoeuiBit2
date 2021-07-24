package org.jeonfeel.moeuibit2.Fragment;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoinArcade;
import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO;
import org.jeonfeel.moeuibit2.Fragment.Chart.GetUpBitCoins;
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
import java.util.TimerTask;

import static java.lang.Math.round;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Fragment_coinOrder extends Fragment {

    private RecyclerView rv_coinArcade;
    private String market;
    private ArrayList<CoinArcadeDTO> coinArcadeDTOS;
    private boolean checkTimer = false;
    private TimerTask timerTask;
    private Adapter_rvCoinArcade adapter_rvCoinArcade;
    private Double openingPrice;
    private int index = 0; //ask coinArcade set을 위해!!
    private int index2 = 0;//bid coinArcade set을 위해!!
    GetUpBitCoinArcade getUpBitCoinArcade;
    RadioGroup radioGroup_orderWays;
    LinearLayout linear_PriceOrder1,linear_PriceOrder3,linear_PriceOrder2;
    ConstraintLayout const_priceOrder4,const_marketPriceOrder;

    public Fragment_coinOrder(String market) {
        // Required empty public constructor
        this.market = market;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_coin_order, container, false);
        // Inflate the layout for this fragment
        FindViewById(rootView);
        setRv_coinArcade();
        getOpeningPriceFromApi();
        adapter_rvCoinArcade = new Adapter_rvCoinArcade(coinArcadeDTOS, getActivity(), openingPrice);
        rv_coinArcade.setAdapter(adapter_rvCoinArcade);
        getCoinArcadeInfo();
        setRadioGroup_orderWays(rootView);

        return rootView;
    }

    private void setRv_coinArcade() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_coinArcade.setLayoutManager(linearLayoutManager);
    }

    private void FindViewById(View rootView) {
        rv_coinArcade = rootView.findViewById(R.id.rv_coinArcade);
        radioGroup_orderWays = rootView.findViewById(R.id.radioGroup_orderWays);
        linear_PriceOrder1 = rootView.findViewById(R.id.linear_PriceOrder1);
        linear_PriceOrder3 = rootView.findViewById(R.id.linear_PriceOrder3);
        linear_PriceOrder2 = rootView.findViewById(R.id.linear_PriceOrder2);
        const_priceOrder4 = rootView.findViewById(R.id.const_priceOrder4);
        const_marketPriceOrder = rootView.findViewById(R.id.const_marketPriceOrder);
    }

    private void getCoinArcadeInfo() {

        String coinArcadeUrl = "https://api.upbit.com/v1/orderbook?markets=" + market;

        coinArcadeDTOS = new ArrayList<>();

        GetUpBitCoins getUpBitCoins = new GetUpBitCoins();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = getUpBitCoins.execute(coinArcadeUrl).get();
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

            if (jsonArray != null) {

                JSONArray jsonUnits = (JSONArray) jsonObject.get("orderbook_units");
                JSONObject jsonObjUnits = new JSONObject();

                for (int i = jsonUnits.length() - 1; i >= 0; i--) {
                    jsonObjUnits = (JSONObject) jsonUnits.get(i);

                    Double arcadePrice = jsonObjUnits.getDouble("ask_price");
                    Double coinArcadeSize = jsonObjUnits.getDouble("ask_size");
                    coinArcadeDTOS.add(new CoinArcadeDTO(arcadePrice, coinArcadeSize, "ask"));
                }

                for (int i = 0; i < jsonUnits.length(); i++) {
                    jsonObjUnits = (JSONObject) jsonUnits.get(i);

                    Double arcadePrice = jsonObjUnits.getDouble("bid_price");
                    Double coinArcadeSize = jsonObjUnits.getDouble("bid_size");

                    coinArcadeDTOS.add(new CoinArcadeDTO(arcadePrice, coinArcadeSize, "bid"));
                }
                adapter_rvCoinArcade.setItem(coinArcadeDTOS);
                adapter_rvCoinArcade.notifyDataSetChanged();
                rv_coinArcade.scrollToPosition(8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            getUpBitCoins = null;
        }
    }

    private void getOpeningPriceFromApi() {

        String coinUrl = "https://api.upbit.com/v1/ticker?markets=" + market;

        GetUpBitCoins getUpBitCoins = new GetUpBitCoins();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = getUpBitCoins.execute(coinUrl).get();

            if (jsonArray != null) {
                JSONObject jsonObject = new JSONObject();

                jsonObject = (JSONObject) jsonArray.get(0);
                openingPrice = jsonObject.getDouble("prev_closing_price");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getUpBitCoinArcade = new GetUpBitCoinArcade();
        getUpBitCoinArcade.start();
    }

    @Override
    public void onResume() { //사용자와 상호작용 하고 있을 때  1초마다 api 받아옴
        super.onResume();
    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();
        if(getUpBitCoinArcade!=null) {
            getUpBitCoinArcade.stopThread();
        }
    }

    private void setRadioGroup_orderWays(View rootView){

        RadioButton radio_setPrice = rootView.findViewById(R.id.radio_setPrice);
        radio_setPrice.setChecked(true);
        const_marketPriceOrder.setVisibility(View.GONE);


        radioGroup_orderWays.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radio_setPrice:
                        linear_PriceOrder1.setVisibility(View.VISIBLE);
                        linear_PriceOrder3.setVisibility(View.VISIBLE);
                        linear_PriceOrder2.setVisibility(View.VISIBLE);
                        const_priceOrder4.setVisibility(View.VISIBLE);
                        const_marketPriceOrder.setVisibility(View.GONE);
                        break;
                    case R.id.radio_marketPrice:
                        linear_PriceOrder1.setVisibility(View.GONE);
                        linear_PriceOrder3.setVisibility(View.GONE);
                        linear_PriceOrder2.setVisibility(View.GONE);
                        const_priceOrder4.setVisibility(View.GONE);
                        const_marketPriceOrder.setVisibility(View.VISIBLE);
                        break;
                }

            }
        });
    }



    class GetUpBitCoinArcade extends Thread {

        private boolean isRunning = true;

        @Override
        public void run() {
            super.run();
            while (isRunning) {
                try {
                    URL url = new URL("https://api.upbit.com/v1/orderbook?markets=" + market);
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

                    try {
                        JSONObject jsonObject = (JSONObject) jsonCoinInfo.get(0);

                        if (jsonCoinInfo != null) {

                            JSONArray jsonUnits = (JSONArray) jsonObject.get("orderbook_units");
                            JSONObject jsonObjUnits = new JSONObject();

                            for (int i = jsonUnits.length() - 1; i >= 0; i--) {
                                jsonObjUnits = (JSONObject) jsonUnits.get(i);

                                Double arcadePrice = jsonObjUnits.getDouble("ask_price");
                                Double coinArcadeSize = jsonObjUnits.getDouble("ask_size");
                                coinArcadeDTOS.set(index++, new CoinArcadeDTO(arcadePrice, coinArcadeSize, "ask"));
                                index2++;
                            }

                            for (int i = 0; i < jsonUnits.length(); i++) {
                                jsonObjUnits = (JSONObject) jsonUnits.get(i);

                                Double arcadePrice = jsonObjUnits.getDouble("bid_price");
                                Double coinArcadeSize = jsonObjUnits.getDouble("bid_size");

                                coinArcadeDTOS.set(index2++, new CoinArcadeDTO(arcadePrice, coinArcadeSize, "bid"));
                            }

                            index = 0;
                            index2 = 0;

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter_rvCoinArcade.setItem(coinArcadeDTOS);
                                    adapter_rvCoinArcade.notifyDataSetChanged();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
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