package org.jeonfeel.moeuibit2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Fragment_Exchange extends Fragment {

    private final String TAG = "Fragment_Exchange";

    EditText et_searchCoin;
    RecyclerView rv_coin;


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
        getUpBitApi();
        return rootView;
    }

    private void FindViewById(View rootView){
        et_searchCoin =  rootView.findViewById(R.id.et_searchCoin);
        rv_coin = rootView.findViewById(R.id.rv_coin);
    }

    private void setRv_coin(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_coin.setLayoutManager(linearLayoutManager);
    }

    //업비트 api를 받아온다.
    private void getUpBitApi(){

        ArrayList<String> marketsArray = new ArrayList<>();
        ArrayList<CoinDTO> allCoinInfoArray = new ArrayList<>();
        ArrayList<String> koreanNamesArray = new ArrayList<>();
        ArrayList<String> englishNamesArray = new ArrayList<>();
        String koreanName,englishName;

        String url = "https://api.upbit.com/v1/market/all"; // 업비트 모든 코인 종류
        GetUpBitApi getUpBitApi = new GetUpBitApi();

        try {
            JSONArray jsonArray = new JSONArray();

            jsonArray = getUpBitApi.execute(url).get();

            if(jsonArray != null){

                JSONObject jsonObject = new JSONObject();


                for(int i = 0; i < jsonArray.length() ; i++){
                    jsonObject = (JSONObject) jsonArray.get(i);

                    if(jsonObject.getString("market").contains("KRW")){ //KRW 마켓만 골라옴
                        String market = jsonObject.getString("market");
                        koreanName = jsonObject.getString("korean_name");
                        englishName = jsonObject.getString("english_name");

                        marketsArray.add(market);
                        koreanNamesArray.add(koreanName);
                        englishNamesArray.add(englishName);
                        Log.d(TAG,market);
                    }
                }
            } // 인터넷 연결 확인 추가해야함
        } catch (Exception e){
            e.printStackTrace();
        }

        StringBuilder builder = new StringBuilder(); //StringBuilder 를 사용해서 업비트 모든 코인 종류 받아옴

        for(int i = 0; i < marketsArray.size(); i++){
            builder.append(marketsArray.get(i)).append(",");
        }

        builder.deleteCharAt(builder.lastIndexOf(","));
        String markets = builder.toString();

        String allCoinsInfoUrl = "https://api.upbit.com/v1/ticker?markets=" + markets;
        GetUpBitApi getUpBitApi1 = new GetUpBitApi();
        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = getUpBitApi1.execute(allCoinsInfoUrl).get();

            if(jsonArray != null){
                JSONObject jsonObject = new JSONObject();

                for(int i = 0; i < jsonArray.length(); i++){
                    jsonObject = (JSONObject) jsonArray.get(i);

                    Double currentPrice = jsonObject.getDouble("trade_price");
                    Double dayToDay = jsonObject.getDouble("signed_change_rate");
                    Double transactionAmount = jsonObject.getDouble("acc_trade_price_24h");

                    CoinDTO coinDTO = new CoinDTO(marketsArray.get(i),koreanNamesArray.get(i),englishNamesArray.get(i)
                    ,currentPrice,dayToDay,transactionAmount);

                    allCoinInfoArray.add(coinDTO);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        Adapter_rvCoin adapter_rvCoin = new Adapter_rvCoin(allCoinInfoArray,getActivity());
        rv_coin.setAdapter(adapter_rvCoin);
        adapter_rvCoin.notifyDataSetChanged();
    }
}