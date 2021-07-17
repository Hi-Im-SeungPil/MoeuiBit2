package org.jeonfeel.moeuibit2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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
        adapter_rvCoinArcade = new Adapter_rvCoinArcade(coinArcadeDTOS,getActivity(),openingPrice);
        rv_coinArcade.setAdapter(adapter_rvCoinArcade);
        getCoinArcadeInfo();
        rv_coinArcade.scrollToPosition(8);

        return rootView;
    }

    private void setRv_coinArcade(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_coinArcade.setLayoutManager(linearLayoutManager);
    }

    private void FindViewById(View rootView){
        rv_coinArcade = rootView.findViewById(R.id.rv_coinArcade);
    }

    private void getCoinArcadeInfo(){

        String coinArcadeUrl = "https://api.upbit.com/v1/orderbook?markets="+market;

        coinArcadeDTOS = new ArrayList<>();
        GetUpBitCoins getUpBitCoins = new GetUpBitCoins();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = getUpBitCoins.execute(coinArcadeUrl).get();
            JSONObject jsonObject = (JSONObject) jsonArray.get(0);

            if (jsonArray != null) {

                JSONArray jsonUnits = (JSONArray) jsonObject.get("orderbook_units");
                JSONObject jsonObjUnits = new JSONObject();

                for ( int i = jsonUnits.length() - 1; i >= 0 ; i--) {
                    jsonObjUnits = (JSONObject) jsonUnits.get(i);

                    Double arcadePrice = jsonObjUnits.getDouble("ask_price");
                    Double coinArcadeSize = jsonObjUnits.getDouble("ask_size");
                    coinArcadeDTOS.add(new CoinArcadeDTO(arcadePrice,coinArcadeSize,"ask"));
                }

                for(int i = 0; i < jsonUnits.length(); i++){
                    jsonObjUnits = (JSONObject) jsonUnits.get(i);

                    Double arcadePrice = jsonObjUnits.getDouble("bid_price");
                    Double coinArcadeSize = jsonObjUnits.getDouble("bid_size");

                    coinArcadeDTOS.add(new CoinArcadeDTO(arcadePrice,coinArcadeSize,"bid"));
                }
                adapter_rvCoinArcade.setItem(coinArcadeDTOS);
                adapter_rvCoinArcade.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            getUpBitCoins = null;
        }
    }

    private void getOpeningPriceFromApi(){

        String coinUrl = "https://api.upbit.com/v1/ticker?markets="+market;

        GetUpBitCoins getUpBitCoins = new GetUpBitCoins();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = getUpBitCoins.execute(coinUrl).get();

            if (jsonArray != null) {
                JSONObject jsonObject = new JSONObject();

                jsonObject = (JSONObject) jsonArray.get(0);

                openingPrice = jsonObject.getDouble("prev_closing_price");
            }

        }catch (Exception e){
            e.printStackTrace();
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getCoinArcadeInfo();
                        adapter_rvCoinArcade.setItem(coinArcadeDTOS);
                        adapter_rvCoinArcade.notifyDataSetChanged();
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask,0,1000);
    }
}