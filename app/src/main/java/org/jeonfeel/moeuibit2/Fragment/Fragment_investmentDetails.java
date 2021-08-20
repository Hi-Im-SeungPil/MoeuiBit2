package org.jeonfeel.moeuibit2.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.jeonfeel.moeuibit2.Activitys.Activity_portfolio;
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
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private ArrayList<MyCoinsDTO> myCoinsDTOS;
    private RecyclerView rv_myCoins;
    private String markets = "";
    private List<MyCoin> myCoins;
    private long totalBuyOut,myKoreanWon,totalEvaluation;
    private GetMyCoins getMyCoins;
    private ArrayList<Double> currentPrices;
    private Adapter_rvMyCoins adapter_rvMyCoins;

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

    private void setRv_myCoins(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rv_myCoins.setLayoutManager(linearLayoutManager);
    }

    //초기설정
    private void init() {
        StringBuilder stringBuilder = null;
        myCoinsDTOS = null;
        currentPrices = null;
        totalBuyOut = 0;

        stringBuilder = new StringBuilder();
        myCoinsDTOS = new ArrayList<>();
        currentPrices = new ArrayList<>();

        //보유 krw 설정
        User user = db.userDAO().getAll();
        if (user != null) {
            myKoreanWon = user.getKrw();
            tv_myTotalProperty.setText(decimalFormat.format(myKoreanWon));
        }
        // 총 매수 설정
        myCoins = db.myCoinDAO().getAll();

        //보유 코인 정보 get
        if(myCoins.size() != 0) {
            for (int i = 0; i < myCoins.size(); i++) {
                Double purchasePrice = myCoins.get(i).getPurchasePrice();
                Double quantity = myCoins.get(i).getQuantity();

                stringBuilder.append(myCoins.get(i).getMarket()).append(",");
                // 객체 만들어서
                MyCoinsDTO myCoinsDTO = new MyCoinsDTO(myCoins.get(i).getKoreanCoinName(), myCoins.get(i).getSymbol(), quantity, purchasePrice, 0.0);
                myCoinsDTOS.add(myCoinsDTO);

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
        adapter_rvMyCoins = new Adapter_rvMyCoins(myCoinsDTOS,getActivity());
        rv_myCoins.setAdapter(adapter_rvMyCoins);
        adapter_rvMyCoins.setCurrentPrices(currentPrices);
        adapter_rvMyCoins.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(myCoins.size() != 0) {
            getMyCoins = new GetMyCoins();
            getMyCoins.start();
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

                Intent intent = new Intent(getActivity(), Activity_portfolio.class);
                startActivity(intent);

            }
        });

    }

    private void setBtn_earningKrw(View rootView){
        Button btn_earningKrw = rootView.findViewById(R.id.btn_earningKrw);
        btn_earningKrw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = db.userDAO().getAll();

                if(user == null){
                    db.userDAO().insert();
                }else{
                    db.userDAO().updatePlusMoney(5000000);
                }
                user = null;
            }
        });
    }

    public class GetMyCoins extends Thread{

        private boolean isRunning = true;

        @Override
        public void run() {
            super.run();

            while (isRunning) {
                try {
                    totalEvaluation = 0;

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

                    if (jsonCoinInfo != null && myCoins.size() == jsonCoinInfo.length()) {
                        JSONObject jsonObject = new JSONObject();

                        for (int i = 0; i < jsonCoinInfo.length(); i++) {
                            jsonObject = (JSONObject) jsonCoinInfo.get(i);

                            Double currentPrice = jsonObject.getDouble("trade_price");
                            Double quantity = myCoins.get(i).getQuantity();
                            String coinName = myCoins.get(i).getKoreanCoinName();

                            totalEvaluation += round(currentPrice * quantity);
                            currentPrices.set(i, currentPrice);

                            Log.d("coinInfoqqqq", coinName + " : " + currentPrice);
                        }

                        Double yield = (totalEvaluation - totalBuyOut) / Double.valueOf(totalBuyOut) * 100; //퍼센트 계산
                        int evaluationGainLoss = (int) (totalEvaluation - totalBuyOut);
                        int myTotalProperty = (int) (myKoreanWon + totalEvaluation);
                        String yieldResult = String.format("%.2f", yield);

                        Log.d("totalEvaluation", totalEvaluation + "");
                        Log.d("totalBuyOut", totalBuyOut + "");
                        Log.d("evaluationGainLoss", evaluationGainLoss + "");

                        if (getActivity() != null) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if (totalEvaluation - totalBuyOut > 0) {
                                        tv_yield.setTextColor(Color.parseColor("#B77300"));
                                        tv_evaluationGainLoss.setTextColor(Color.parseColor("#B77300"));
                                    } else if (totalEvaluation - totalBuyOut < 0) {
                                        tv_yield.setTextColor(Color.parseColor("#0054FF"));
                                        tv_evaluationGainLoss.setTextColor(Color.parseColor("#0054FF"));
                                    } else {
                                        tv_yield.setTextColor(Color.parseColor("#000000"));
                                        tv_evaluationGainLoss.setTextColor(Color.parseColor("#000000"));
                                    }

                                    tv_totalEvaluation.setText(decimalFormat.format(totalEvaluation));
                                    tv_myTotalProperty.setText(decimalFormat.format(myTotalProperty));
                                    tv_evaluationGainLoss.setText(decimalFormat.format(evaluationGainLoss));
                                    tv_yield.setText(yieldResult + "%");

                                    adapter_rvMyCoins.notifyDataSetChanged();
                                }
                            });
                        }
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
        public void stopThread(){
            isRunning = false;
        }
    }
}

