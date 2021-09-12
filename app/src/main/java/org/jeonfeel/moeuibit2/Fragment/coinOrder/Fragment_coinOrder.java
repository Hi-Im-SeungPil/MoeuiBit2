package org.jeonfeel.moeuibit2.Fragment.coinOrder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.jeonfeel.moeuibit2.Activitys.Activity_coinInfo;
import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoinArcade;
import org.jeonfeel.moeuibit2.Adapters.Adapter_rvTransactionInfo;
import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO;
import org.jeonfeel.moeuibit2.Database.TransactionInfo;
import org.jeonfeel.moeuibit2.Fragment.Chart.GetUpBitCoins;
import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.Database.MyCoin;
import org.jeonfeel.moeuibit2.R;
import org.jeonfeel.moeuibit2.Database.User;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Math.round;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class Fragment_coinOrder extends Fragment {

    private LinearLayout include_coin_parent,linear_coinSell,linear_coinOrder;
    private RecyclerView rv_coinArcade,rv_transactionInfo;
    private String market,koreanName,symbol;
    private ArrayList<CoinArcadeDTO> coinArcadeDTOS;
    private Adapter_rvCoinArcade adapter_rvCoinArcade;
    private Adapter_rvTransactionInfo adapter_rvTransactionInfo;
    private List<TransactionInfo> transactionInfos;
    private Double openingPrice;
    private int index = 0; //ask coinArcade set을 위해!!
    private int index2 = 0;//bid coinArcade set을 위해!!
    private GetUpBitCoinArcade getUpBitCoinArcade;
    private TextView tv_orderableAmount,tv_sellAbleCoinQuantity,tv_sellAbleAmount,tv_sellAbleCoinSymbol,tv_transactionInfoIsNull;

    private Button btn_coinOrder,btn_coinSell,btn_coinSellReset,btn_coinOrderReset;

    private MoEuiBitDatabase db;
    private long leftMoney;
    private EditText et_orderCoinPrice, et_orderCoinQuantity, et_orderCoinTotalAmount;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private Spinner spinner_orderCoinQuantity,spinner_sellCoinQuantity;
    private EditText et_sellCoinQuantity,et_sellCoinPrice,et_sellCoinTotalAmount;
    private String commaResult;

    public Fragment_coinOrder(String market,String koreanName,String symbol) {
        // Required empty public constructor
        this.market = market;
        this.koreanName = koreanName;
        this.symbol = symbol;
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
        getCoinArcadeInfo();
        initFragment_coinOrder();
        setBtn_coinSellReset();
        setBtn_coinOrderReset();
        setTabLayout(rootView);

        return rootView;
    }

    private void setRv_coinArcade() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_coinArcade.setLayoutManager(linearLayoutManager);
    }
    private void setRv_transactionInfo(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rv_transactionInfo.setLayoutManager(linearLayoutManager);

        transactionInfos = new ArrayList<>();
        adapter_rvTransactionInfo = new Adapter_rvTransactionInfo(transactionInfos,getActivity());
        rv_transactionInfo.setAdapter(adapter_rvTransactionInfo);

    }
    @SuppressLint("ClickableViewAccessibility")
    private void initFragment_coinOrder(){
        setBtn_coinOrder();
        setBtn_coinSell();
        setSpinner_orderCoinQuantity();
        setSpinner_sellCoinQuantity();
        setRv_transactionInfo();

        linear_coinSell.setVisibility(View.GONE);
        rv_transactionInfo.setVisibility(View.GONE);
        tv_sellAbleCoinSymbol.setText(symbol);

        db = MoEuiBitDatabase.getInstance(getActivity());

        // 주문가능 설정 ---------------

        User user = db.userDAO().getAll();
        if(user != null) {
            leftMoney = user.getKrw();
        }else{
            leftMoney = 0;
        }
        tv_orderableAmount.setText(decimalFormat.format(leftMoney));

        // 주문가능 설정 ---------------

        // 판매가능 코인 설정 ---------------

        MyCoin myCoin = db.myCoinDAO().isInsert(market);
        if(myCoin != null){
            tv_sellAbleCoinQuantity.setText(String.format("%.8f",myCoin.getQuantity()));
        }else{
            tv_sellAbleCoinQuantity.setText("0");
        }

        // 판매가능 코인 설정 ---------------

        et_orderCoinTotalAmount.setCursorVisible(false);
        et_sellCoinTotalAmount.setCursorVisible(false);
        TextWatcher tw1 = tw1();
        et_orderCoinQuantity.addTextChangedListener(tw1);
        et_sellCoinQuantity.addTextChangedListener(tw1);

// 레이아웃 터치하면 키보드 내림.
        include_coin_parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                if(linear_coinOrder.getVisibility() == View.VISIBLE) {
                    if (et_orderCoinPrice.isFocused()) {
                        et_orderCoinPrice.clearFocus();
                        imm.hideSoftInputFromWindow(et_orderCoinPrice.getWindowToken(), 0);
                    } else if (et_orderCoinQuantity.isFocused()) {
                        et_orderCoinQuantity.clearFocus();
                        imm.hideSoftInputFromWindow(et_orderCoinQuantity.getWindowToken(), 0);
                    }
                }else if(linear_coinSell.getVisibility() == View.VISIBLE){
                    if (et_sellCoinPrice.isFocused()) {
                        et_sellCoinPrice.clearFocus();
                        imm.hideSoftInputFromWindow(et_sellCoinPrice.getWindowToken(), 0);
                    } else if (et_sellCoinQuantity.isFocused()) {
                        et_sellCoinQuantity.clearFocus();
                        imm.hideSoftInputFromWindow(et_sellCoinQuantity.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });


        et_orderCoinTotalAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_orderCoinPrice.isFocused()) {
                    et_orderCoinPrice.clearFocus();
                }else if(et_orderCoinQuantity.isFocused()) {
                    et_orderCoinQuantity.clearFocus();
                }
                EditText editText = new EditText(getActivity());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("총액을 입력하세요");
                builder.setView(editText);
                builder.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!et_orderCoinPrice.getText().toString().equals("0") && et_orderCoinPrice.length() != 0 && editText.length() != 0){
                            long amount = Long.parseLong(editText.getText().toString());
                            Double orderPrice = Double.valueOf(et_orderCoinPrice.getText().toString().replace(",",""));
                            Double quantity =  amount / orderPrice;
                            et_orderCoinQuantity.setText(String.format("%.8f",quantity));
                        }
                    }
                }).setNegativeButton("취소", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                editText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editText.requestFocus();
                        imm.showSoftInput(editText,0);
                    }
                },100);
            }
        });

        et_sellCoinTotalAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_sellCoinPrice.isFocused()) {
                    et_sellCoinPrice.clearFocus();
                }else if(et_sellCoinQuantity.isFocused()) {
                    et_sellCoinQuantity.clearFocus();
                }
                EditText editText = new EditText(getActivity());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("총액을 입력하세요");
                builder.setView(editText);
                builder.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!et_sellCoinPrice.getText().toString().equals("0") && et_sellCoinPrice.length() != 0 && editText.length() != 0){
                            long amount = Long.parseLong(editText.getText().toString());
                            Double orderPrice = Double.valueOf(et_sellCoinPrice.getText().toString().replace(",",""));
                            Double quantity =  amount / orderPrice;
                            et_sellCoinQuantity.setText(String.format("%.8f",quantity));
                        }
                    }
                }).setNegativeButton("취소", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                editText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editText.requestFocus();
                        imm.showSoftInput(editText,0);
                    }
                },100);
            }
        });
    }
    private TextWatcher tw1(){
        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(et_orderCoinQuantity.length() != 0 && et_orderCoinPrice.length() != 0 && linear_coinOrder.getVisibility() == View.VISIBLE){

                    Double quantity = Double.valueOf(et_orderCoinQuantity.getText().toString());
                    Double price = Double.valueOf(et_orderCoinPrice.getText().toString().replace(",",""));
                    et_orderCoinTotalAmount.setText(decimalFormat.format(round(quantity*price)));

                }else if(et_sellCoinQuantity.length() != 0 && et_sellCoinPrice.length() != 0 && linear_coinSell.getVisibility() == View.VISIBLE){

                    Double quantity = Double.valueOf(et_sellCoinQuantity.getText().toString());
                    Double price = Double.valueOf(et_sellCoinPrice.getText().toString().replace(",",""));
                    et_sellCoinTotalAmount.setText(decimalFormat.format(round(quantity*price)));

                }else if(et_orderCoinQuantity.length() == 0 && et_orderCoinPrice.length() != 0 && linear_coinOrder.getVisibility() == View.VISIBLE){

                    et_orderCoinTotalAmount.setText("0");

                }else if(et_sellCoinQuantity.length() == 0 && et_sellCoinPrice.length() != 0 && linear_coinSell.getVisibility() == View.VISIBLE){

                    et_sellCoinTotalAmount.setText("0");

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        return textWatcher1;
    }
    private TextWatcher tw2(){
        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(commaResult)
                        && Double.valueOf(charSequence.toString().replaceAll(",","")) >= 1000) {
                    commaResult = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    et_orderCoinPrice.setText(commaResult);
                    et_sellCoinPrice.setText(commaResult);
                    et_orderCoinPrice.setSelection(commaResult.length());
                    et_sellCoinPrice.setSelection(commaResult.length());
                }

                if(et_orderCoinQuantity.length() != 0 && et_orderCoinPrice.length() != 0 && linear_coinOrder.getVisibility() == View.VISIBLE){

                    Double quantity = Double.valueOf(et_orderCoinQuantity.getText().toString());
                    Double price = Double.valueOf(et_orderCoinPrice.getText().toString().replace(",",""));
                    et_orderCoinTotalAmount.setText(decimalFormat.format(round(quantity*price)));

                }else if(et_sellCoinQuantity.length() != 0 && et_sellCoinPrice.length() != 0 && linear_coinSell.getVisibility() == View.VISIBLE){
                    Double quantity = Double.valueOf(et_sellCoinQuantity.getText().toString());
                    Double price = Double.valueOf(et_sellCoinPrice.getText().toString().replace(",",""));
                    et_sellCoinTotalAmount.setText(decimalFormat.format(round(quantity*price)));
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        return textWatcher1;
    }
    private void FindViewById(View rootView) {

        rv_coinArcade = rootView.findViewById(R.id.rv_coinArcade);
        tv_orderableAmount = rootView.findViewById(R.id.tv_orderableAmount);
        btn_coinOrder = rootView.findViewById(R.id.btn_coinOrder);
        et_orderCoinPrice = rootView.findViewById(R.id.et_orderCoinPrice);
        et_orderCoinQuantity = rootView.findViewById(R.id.et_orderCoinQuantity);
        et_orderCoinTotalAmount = rootView.findViewById(R.id.et_orderCoinTotalAmount);
        include_coin_parent = rootView.findViewById(R.id.include_coin_parent);
        spinner_orderCoinQuantity = rootView.findViewById(R.id.spinner_orderCoinQuantity);
        linear_coinSell = rootView.findViewById(R.id.linear_coinSell);
        linear_coinOrder = rootView.findViewById(R.id.linear_coinOrder);
        tv_sellAbleCoinQuantity = rootView.findViewById(R.id.tv_sellAbleCoinQuantity);
        tv_sellAbleAmount = rootView.findViewById(R.id.tv_sellAbleAmount);
        et_sellCoinQuantity = rootView.findViewById(R.id.et_sellCoinQuantity);
        et_sellCoinPrice = rootView.findViewById(R.id.et_sellCoinPrice);
        et_sellCoinTotalAmount = rootView.findViewById(R.id.et_sellCoinTotalAmount);
        btn_coinSell = rootView.findViewById(R.id.btn_coinSell);
        btn_coinSellReset = rootView.findViewById(R.id.btn_coinSellReset);
        btn_coinOrder = rootView.findViewById(R.id.btn_coinOrder);
        btn_coinOrderReset = rootView.findViewById(R.id.btn_coinOrderReset);
        tv_sellAbleCoinSymbol = rootView.findViewById(R.id.tv_sellAbleCoinSymbol);
        spinner_sellCoinQuantity = rootView.findViewById(R.id.spinner_sellCoinQuantity);
        rv_transactionInfo = rootView.findViewById(R.id.rv_transactionInfo);
        tv_transactionInfoIsNull = rootView.findViewById(R.id.tv_transactionInfoIsNull);

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
                //매수 , 매도 잔량 중간.
                Double initPrice = coinArcadeDTOS.get(14).getCoinArcadePrice();

                //초기 코인 가격 설정

                if(initPrice >= 100){

                    et_orderCoinPrice.setText(decimalFormat.format(round(initPrice))+"");
                    et_sellCoinPrice.setText(decimalFormat.format(round(initPrice))+"");

                }else {

                    et_orderCoinPrice.setText(String.format("%.2f", initPrice));
                    et_sellCoinPrice.setText(String.format("%.2f", initPrice));

                }
                adapter_rvCoinArcade.setItem(coinArcadeDTOS);
                adapter_rvCoinArcade.notifyDataSetChanged();
                rv_coinArcade.scrollToPosition(8);

            }

        } catch (Exception e) {
            e.printStackTrace();
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

                adapter_rvCoinArcade = new Adapter_rvCoinArcade(coinArcadeDTOS, getActivity(), openingPrice,et_orderCoinPrice,et_orderCoinQuantity
                ,et_sellCoinPrice,et_sellCoinQuantity,linear_coinOrder,linear_coinSell);
                rv_coinArcade.setAdapter(adapter_rvCoinArcade);
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
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();
        if(getUpBitCoinArcade!=null) {
            getUpBitCoinArcade.stopThread();
        }
    }

    private void setBtn_coinOrder(){
        btn_coinOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Double currentPrice = ((Activity_coinInfo)getActivity()).getGlobalCurrentPrice();

                        if(et_orderCoinQuantity.length() == 0 || et_orderCoinPrice.length() == 0 || et_orderCoinTotalAmount.length() == 0){
                            Toast.makeText(getActivity(), "빈곳없이 매수요청 해주세요!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Double orderQuantity = Double.valueOf(et_orderCoinQuantity.getText().toString());
                        Double orderAmount = currentPrice * orderQuantity;

                        MyCoin myCoin = null;
                        myCoin = db.myCoinDAO().isInsert(market);

                        if (round(orderAmount) > leftMoney) {
                            Toast.makeText(getActivity(), "보유 KRW가 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (myCoin == null && orderAmount <= leftMoney) {

                            if (round(orderAmount) < 5000) {
                                Toast.makeText(getActivity(), "5000원 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            MyCoin myCoin1 = new MyCoin(market, currentPrice, koreanName, symbol, orderQuantity);
                            db.myCoinDAO().insert(myCoin1);
                            db.userDAO().updateMinusMoney(round(orderAmount));

                            User user = db.userDAO().getAll();
                            tv_orderableAmount.setText(decimalFormat.format(user.krw));

                            myCoin = db.myCoinDAO().isInsert(market);
                            tv_sellAbleCoinQuantity.setText(String.format("%.8f", myCoin.getQuantity()));

                            et_orderCoinQuantity.setText("");
                            et_orderCoinTotalAmount.setText("");

                            db.transactionInfoDAO().insert(market,currentPrice,orderQuantity,round(orderAmount),"bid", System.currentTimeMillis());

                            Toast.makeText(getActivity(), "매수 되었습니다.", Toast.LENGTH_SHORT).show();

                        } else if (myCoin != null && orderAmount <= leftMoney) {

                            if (round(orderAmount) < 5000) {
                                Toast.makeText(getActivity(), "5000원 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Double myCoinQuantity = myCoin.getQuantity();
                            Double myCoinPrice = myCoin.getPurchasePrice();

                            Double averageOrderPrice = averageOrderPriceCalculate(myCoinQuantity, myCoinPrice, orderQuantity, currentPrice);
                            if (averageOrderPrice >= 100) {
                                int purchasePrice = (int) round(averageOrderPrice);
                                db.myCoinDAO().updatePurchasePriceInt(market, purchasePrice);
                            } else {
                                averageOrderPrice = Double.valueOf(String.format("%.2f", averageOrderPrice));
                                db.myCoinDAO().updatePurchasePrice(market, averageOrderPrice);
                            }

                            Double quantityResult = Double.valueOf(String.format("%.8f", myCoinQuantity + orderQuantity));

                            db.myCoinDAO().updateQuantity(market, quantityResult);
                            db.userDAO().updateMinusMoney(round(orderAmount));
                            User user = db.userDAO().getAll();
                            tv_orderableAmount.setText(decimalFormat.format(user.krw));

                            myCoin = db.myCoinDAO().isInsert(market);
                            tv_sellAbleCoinQuantity.setText(String.format("%.8f", myCoin.getQuantity()));
                            et_orderCoinQuantity.setText("");
                            et_orderCoinTotalAmount.setText("");

                            db.transactionInfoDAO().insert(market,currentPrice,orderQuantity,round(orderAmount),"bid",System.currentTimeMillis());

                            Toast.makeText(getActivity(), "매수 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
            }
        });
    }
    private void setBtn_coinSell(){

        btn_coinSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Double sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString());
                    Double currentPrice = ((Activity_coinInfo)getActivity()).getGlobalCurrentPrice();

                        if (et_sellCoinQuantity.length() == 0 || et_sellCoinTotalAmount.length() == 0 || et_sellCoinPrice.length() == 0 ) {
                            Toast.makeText(getActivity(), "빈곳없이 매도요청 해주세요!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Double sellCoinQuantity = Double.valueOf(et_sellCoinQuantity.getText().toString());
                        Double sellCoinPrice = Double.valueOf(et_sellCoinPrice.getText().toString().replaceAll(",", ""));

                        if(sellAbleCoinQuantity < sellCoinQuantity){
                            Toast.makeText(getActivity(), "판매가능한 수량이 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(round(sellCoinQuantity * currentPrice) < 5000){
                            Toast.makeText(getActivity(), "5000이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (sellCoinQuantity <= sellAbleCoinQuantity && round(sellCoinQuantity * sellCoinPrice) >= 5000) {

                            Double initQuantity = db.myCoinDAO().isInsert(market).getQuantity();
                            Double quantityResult = Double.valueOf(String.format("%.8f", initQuantity - sellCoinQuantity));

                            db.myCoinDAO().updateQuantity(market, quantityResult);

                            db.userDAO().updatePlusMoney(round(currentPrice * sellCoinQuantity));

                            Double quantity = db.myCoinDAO().isInsert(market).getQuantity();

                            if (quantity == 0.00000000) {
                                db.myCoinDAO().delete(market);
                                tv_sellAbleCoinQuantity.setText("0");
                            } else {
                                tv_sellAbleCoinQuantity.setText(String.format("%.8f", quantity));
                            }

                            User user = db.userDAO().getAll();
                            tv_orderableAmount.setText(decimalFormat.format(user.krw));

                            et_sellCoinQuantity.setText("");
                            et_sellCoinTotalAmount.setText("");
                            Toast.makeText(getActivity(), "매도 되었습니다.", Toast.LENGTH_SHORT).show();

                            db.transactionInfoDAO().insert(market,currentPrice,sellCoinQuantity,round(currentPrice*sellAbleCoinQuantity),"ask",System.currentTimeMillis());
                        }
            }
        });
    }

    private void setSpinner_sellCoinQuantity(){
        String[] items = {"%선택","최대","50%","25%","10%"};

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,items);

        spinner_sellCoinQuantity.setAdapter(adapter);

        spinner_sellCoinQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                    Double sellAbleCoinQuantity = 0.0;

                    switch (spinner_sellCoinQuantity.getSelectedItemPosition()) {
                        case 0:
                            sellAbleCoinQuantity = 0.0;
                            break;
                        case 1:
                            sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString());
                            break;
                        case 2:
                            sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()) / 2;
                            break;
                        case 3:
                            sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()) / 4;
                            break;
                        case 4:
                            sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()) / 10;
                            break;
                    }
                    if(sellAbleCoinQuantity != 0)
                    et_sellCoinQuantity.setText(String.format("%.8f", sellAbleCoinQuantity));
                    else
                        et_sellCoinQuantity.setText(String.format("0"));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setSpinner_orderCoinQuantity(){

        String[] items = {"%선택","최대","50%","25%","10%"};

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,items);

        spinner_orderCoinQuantity.setAdapter(adapter);

            spinner_orderCoinQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                        Double price = Double.valueOf(et_orderCoinPrice.getText().toString().replace(",", ""));

                        int orderablePrice = 0;

                        switch (spinner_orderCoinQuantity.getSelectedItemPosition()) {
                            case 0:
                                orderablePrice = 0;
                                break;
                            case 1:
                                orderablePrice = Integer.parseInt(tv_orderableAmount.getText().toString().replace(",", ""));
                                break;
                            case 2:
                                orderablePrice = round(Integer.parseInt(tv_orderableAmount.getText().toString().replace(",", "")) / 2);
                                break;
                            case 3:
                                orderablePrice = round(Integer.parseInt(tv_orderableAmount.getText().toString().replace(",", "")) / 4);
                                break;
                            case 4:
                                orderablePrice = round(Integer.parseInt(tv_orderableAmount.getText().toString().replace(",", "")) / 10);
                                break;
                        }
                        if(orderablePrice!=0)
                        et_orderCoinQuantity.setText(String.format("%.8f", orderablePrice / price));
                        else
                            et_orderCoinQuantity.setText("0");
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }

    private void setTabLayout(View rootView){

        TabLayout tab_orderSellTransaction = rootView.findViewById(R.id.tab_orderSellTransaction);

        tab_orderSellTransaction.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                if(position == 0){
                    linear_coinOrder.setVisibility(View.VISIBLE);
                    linear_coinSell.setVisibility(View.GONE);
                    rv_transactionInfo.setVisibility(View.GONE);
                    tv_transactionInfoIsNull.setVisibility(View.GONE);
                }else if(position == 1){
                    linear_coinOrder.setVisibility(View.GONE);
                    linear_coinSell.setVisibility(View.VISIBLE);
                    rv_transactionInfo.setVisibility(View.GONE);
                    tv_transactionInfoIsNull.setVisibility(View.GONE);
                }else if(position == 2){
                    linear_coinOrder.setVisibility(View.GONE);
                    linear_coinSell.setVisibility(View.GONE);
                    setRv_transactionInfoData();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }
    private void setBtn_coinSellReset(){
        btn_coinSellReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_sellCoinQuantity.setText("0");
                et_sellCoinTotalAmount.setText("0");
            }
        });
    }

    private void setBtn_coinOrderReset(){
        btn_coinOrderReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_orderCoinQuantity.setText("0");
                et_orderCoinTotalAmount.setText("0");
            }
        });
    }

    private void setRv_transactionInfoData(){

        List<TransactionInfo> transactionInfos = db.transactionInfoDAO().select(market);

        adapter_rvTransactionInfo.setItem(transactionInfos);
        adapter_rvTransactionInfo.notifyDataSetChanged();

        if (adapter_rvTransactionInfo.getItemCount() == 0){
            rv_transactionInfo.setVisibility(View.GONE);
            tv_transactionInfoIsNull.setVisibility(View.VISIBLE);
        }else{
            rv_transactionInfo.setVisibility(View.VISIBLE);
            tv_transactionInfoIsNull.setVisibility(View.GONE);
        }
    }

    private Double averageOrderPriceCalculate(Double myCoinQuantity,Double myCoinPrice,Double newCoinQuantity,Double newCoinPrice){

        Double averageOrderPrice = ((myCoinQuantity * myCoinPrice) + (newCoinQuantity * newCoinPrice))
                /(myCoinQuantity + newCoinQuantity);

        return averageOrderPrice;
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

                            if (getActivity() != null) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Double currentPrice;
                                        try {
                                            currentPrice = ((Activity_coinInfo)getActivity()).getGlobalCurrentPrice();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            currentPrice = null;
                                        }

                                        if (currentPrice != null) {
                                            tv_sellAbleAmount.setText("= " + decimalFormat.format(round(currentPrice * Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()))));

                                            adapter_rvCoinArcade.notifyDataSetChanged();
                                            if (currentPrice >= 100) {
                                                et_orderCoinPrice.setText(decimalFormat.format(currentPrice));
                                                et_sellCoinPrice.setText(decimalFormat.format(currentPrice));
                                            } else {
                                                et_orderCoinPrice.setText(String.format("%.2f", currentPrice));
                                                et_sellCoinPrice.setText(String.format("%.2f",currentPrice));
                                            }

                                            if(linear_coinOrder.getVisibility() == View.VISIBLE && et_orderCoinTotalAmount.length() != 0 && !et_orderCoinTotalAmount.equals("0")){
                                                String total = et_orderCoinTotalAmount.getText().toString().replace(",","");
                                                Double total1 = Double.parseDouble(total);
                                                et_orderCoinQuantity.setText(String.format("%.8f",total1/currentPrice));
                                            }else if(linear_coinSell.getVisibility() == View.VISIBLE && et_sellCoinTotalAmount.length() != 0 && !et_sellCoinTotalAmount.equals("0")){
                                                String total = et_sellCoinTotalAmount.getText().toString().replace(",","");
                                                Double total1 = Double.parseDouble(total);
                                                et_sellCoinQuantity.setText(String.format("%.8f",total1/currentPrice));
                                            }

                                        }
                                    }
                                });
                            }
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