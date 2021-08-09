package org.jeonfeel.moeuibit2.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jeonfeel.moeuibit2.Adapters.Adapter_rvMyCoins;
import org.jeonfeel.moeuibit2.DTOS.CoinDTO;
import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO;
import org.jeonfeel.moeuibit2.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.MyCoin;
import org.jeonfeel.moeuibit2.R;
import org.jeonfeel.moeuibit2.User;
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
import java.util.List;

import static java.lang.Math.round;

public class Fragment_investmentDetails extends Fragment {

    private TextView tv_myKoreanWon, tv_myTotalProperty, tv_totalBuyOut, tv_totalEvaluation, tv_evaluationGainLoss, tv_yield;
    private MoEuiBitDatabase db;
    private boolean isRunning = true;
    private Thread thread;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private ArrayList<MyCoinsDTO> myCoinsDTOS;
    private RecyclerView rv_myCoins;

    public Fragment_investmentDetails() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_investment_details, container, false);

        FindViewById(rootView);
        db = MoEuiBitDatabase.getInstance(getActivity());
        setRv_myCoins();
        init();

        return rootView;
    }

    private void FindViewById(View rootView) { //아이디 찾기
        tv_myKoreanWon = rootView.findViewById(R.id.tv_myKoreanWon);
        tv_myTotalProperty = rootView.findViewById(R.id.tv_myTotalProperty);
        tv_totalBuyOut = rootView.findViewById(R.id.tv_totalBuyOut);
        tv_totalEvaluation = rootView.findViewById(R.id.tv_totalEvaluation);
        tv_evaluationGainLoss = rootView.findViewById(R.id.tv_evaluationGainLoss);
        tv_yield = rootView.findViewById(R.id.tv_yield);
        rv_myCoins = rootView.findViewById(R.id.rv_myCoins);
    }

    private void setRv_myCoins(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rv_myCoins.setLayoutManager(linearLayoutManager);
    }

    private void init() { //초기설정
        //보유 krw 설정
        User user = db.userDAO().getAll();
        long myKoreanWon = user.getKrw();
        tv_myKoreanWon.setText(decimalFormat.format(myKoreanWon));
        myCoinsDTOS = new ArrayList<>();

        // 총 매수 설정
        List<MyCoin> myCoins = db.myCoinDAO().getAll();
        long totalBuyOut = 0;

        String markets = "";
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < myCoins.size(); i++) {
            Double purchasePrice = myCoins.get(i).getPurchasePrice();
            Double quantity = myCoins.get(i).getQuantity();
            stringBuilder.append(myCoins.get(i).getMarket()).append(",");

            MyCoinsDTO myCoinsDTO = new MyCoinsDTO(myCoins.get(i).getKoreanCoinName(),myCoins.get(i).getSymbol(),quantity,purchasePrice);
            myCoinsDTOS.add(myCoinsDTO);

            totalBuyOut += round(purchasePrice * quantity);
        }

        long finalTotalBuyOut = totalBuyOut; //final
        tv_totalBuyOut.setText(decimalFormat.format(totalBuyOut));

        //내가 산 코인들 api로 불러오기
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(","));
        markets = stringBuilder.toString();
        String finalMarkets = markets; //final

        //리사이클러뷰
        Adapter_rvMyCoins adapter_rvMyCoins = new Adapter_rvMyCoins(myCoinsDTOS,getActivity());
        rv_myCoins.setAdapter(adapter_rvMyCoins);
        adapter_rvMyCoins.notifyDataSetChanged();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                while (isRunning) {
                    try {
                        URL url = new URL("https://api.upbit.com/v1/ticker?markets=" + finalMarkets);
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

                        long totalEvaluation = 0;

                        if (jsonCoinInfo != null && myCoins.size() == jsonCoinInfo.length()) {
                            JSONObject jsonObject = new JSONObject();

                            for (int i = 0; i < jsonCoinInfo.length(); i++) {
                                jsonObject = (JSONObject) jsonCoinInfo.get(i);
                                Double currentPrice = jsonObject.getDouble("trade_price");
                                Double quantity = myCoins.get(i).getQuantity();
                                totalEvaluation += round(currentPrice * quantity);
                            }
                            long finalTotalEvaluation = totalEvaluation; //final

                            Double yield = (finalTotalEvaluation - finalTotalBuyOut) / Double.valueOf(finalTotalBuyOut) * 100; //퍼센트 계산

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_totalEvaluation.setText(decimalFormat.format(finalTotalEvaluation));
                                    tv_myTotalProperty.setText(decimalFormat.format(myKoreanWon + finalTotalEvaluation));
                                    tv_evaluationGainLoss.setText(decimalFormat.format(finalTotalEvaluation - finalTotalBuyOut));
                                    tv_yield.setText(String.format("%.2f", yield) + "%");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isRunning) {
            isRunning = true;
        }
        if(!thread.isAlive()) {
            thread.start();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (thread != null) {
            isRunning = false;
        }
    }
}

