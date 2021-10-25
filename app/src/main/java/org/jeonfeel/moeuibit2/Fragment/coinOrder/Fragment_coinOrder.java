package org.jeonfeel.moeuibit2.Fragment.coinOrder;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import org.jeonfeel.moeuibit2.Activitys.Activity_coinInfo;
import org.jeonfeel.moeuibit2.Adapters.Adapter_rvCoinArcade;
import org.jeonfeel.moeuibit2.Adapters.Adapter_rvTransactionInfo;
import org.jeonfeel.moeuibit2.CustomLodingDialog;
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
    private TextView tv_orderableAmount,tv_sellAbleCoinQuantity,tv_sellAbleAmount,tv_sellAbleCoinSymbol,tv_transactionInfoIsNull,
            tv_orderCoinTotalAmount,tv_sellCoinTotalAmount,tv_orderCoinPrice,tv_sellCoinPrice;

    private Button btn_purchaseByDesignatingTotalAmount;
    private Button btn_coinOrder,btn_coinSell,btn_coinSellReset,btn_coinOrderReset;

    private MoEuiBitDatabase db;
    private long leftMoney;
    private EditText et_orderCoinQuantity;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private Spinner spinner_orderCoinQuantity,spinner_sellCoinQuantity;
    private EditText et_sellCoinQuantity;
    private String commaResult;
    private Context context;
    private CustomLodingDialog customLodingDialog;

    public Fragment_coinOrder(){}

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

        context = getActivity();
        customLodingDialog = new CustomLodingDialog(context);
        customLodingDialog.show();
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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        rv_coinArcade.setLayoutManager(linearLayoutManager);
    }
    private void setRv_transactionInfo(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        rv_transactionInfo.setLayoutManager(linearLayoutManager);

        transactionInfos = new ArrayList<>();
        adapter_rvTransactionInfo = new Adapter_rvTransactionInfo(transactionInfos,context);
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

        db = MoEuiBitDatabase.getInstance(context);

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

        TextWatcher tw1 = tw1();
        et_orderCoinQuantity.addTextChangedListener(tw1);
        et_sellCoinQuantity.addTextChangedListener(tw1);

// 레이아웃 터치하면 키보드 내림.
        include_coin_parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                if(linear_coinOrder.getVisibility() == View.VISIBLE) {
                    if (tv_orderCoinPrice.isFocused()) {
                        tv_orderCoinPrice.clearFocus();
                        imm.hideSoftInputFromWindow(tv_orderCoinPrice.getWindowToken(), 0);
                    } else if (et_orderCoinQuantity.isFocused()) {
                        et_orderCoinQuantity.clearFocus();
                        imm.hideSoftInputFromWindow(et_orderCoinQuantity.getWindowToken(), 0);
                    }
                }else if(linear_coinSell.getVisibility() == View.VISIBLE){
                    if (tv_sellCoinPrice.isFocused()) {
                        tv_sellCoinPrice.clearFocus();
                        imm.hideSoftInputFromWindow(tv_sellCoinPrice.getWindowToken(), 0);
                    } else if (et_sellCoinQuantity.isFocused()) {
                        et_sellCoinQuantity.clearFocus();
                        imm.hideSoftInputFromWindow(et_sellCoinQuantity.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });

        btn_purchaseByDesignatingTotalAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tv_orderCoinPrice.isFocused()) {
                    tv_orderCoinPrice.clearFocus();
                }else if(et_orderCoinQuantity.isFocused()) {
                    et_orderCoinQuantity.clearFocus();
                }
                EditText editText = new EditText(context);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                User user = db.userDAO().getAll();
                long myKrw = 0;
                if(user != null) {
                    myKrw = user.getKrw();
                }
                TextWatcher textWatcher = tw2(editText);
                editText.addTextChangedListener(textWatcher);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("매수할 총액을 입력하세요");
                builder.setMessage("\n주문가능\n\n" + decimalFormat.format(myKrw) + " KRW");
                builder.setView(editText);
                builder.setPositiveButton("매수", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Double currentPrice = ((Activity_coinInfo)context).getGlobalCurrentPrice();

                        if(editText.length() == 0){
                            Toast.makeText(context, "빈곳없이 매수요청 해주세요!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Double orderAmount = Double.parseDouble(editText.getText().toString().replace(",",""));
                        String orderQuantity = String.format("%.8f",orderAmount / currentPrice);
                        Double orderQuantityResult = Double.parseDouble(orderQuantity);

                        MyCoin myCoin = null;
                        myCoin = db.myCoinDAO().isInsert(market);

                        if (round(orderAmount) > leftMoney) {
                            Toast.makeText(context, "보유 KRW가 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (myCoin == null && orderAmount <= leftMoney) {

                            if (round(orderAmount) < 5000) {
                                Toast.makeText(context, "5000원 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            MyCoin myCoin1 = new MyCoin(market, currentPrice, koreanName, symbol, orderQuantityResult);
                            db.myCoinDAO().insert(myCoin1);
                            db.userDAO().updateMinusMoney(round(orderAmount));

                            User user = db.userDAO().getAll();
                            tv_orderableAmount.setText(decimalFormat.format(user.krw));

                            myCoin = db.myCoinDAO().isInsert(market);
                            tv_sellAbleCoinQuantity.setText(String.format("%.8f", myCoin.getQuantity()));

                            et_orderCoinQuantity.setText("");
                            tv_orderCoinTotalAmount.setText("");

                            db.transactionInfoDAO().insert(market,currentPrice,orderQuantityResult,round(orderAmount),"bid", System.currentTimeMillis());

                            Toast.makeText(context, "매수 되었습니다.", Toast.LENGTH_SHORT).show();

                        } else if (myCoin != null && orderAmount <= leftMoney) {

                            if (round(orderAmount) < 5000) {
                                Toast.makeText(context, "5000원 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Double myCoinQuantity = myCoin.getQuantity();
                            Double myCoinPrice = myCoin.getPurchasePrice();

                            Double averageOrderPrice = averageOrderPriceCalculate(myCoinQuantity, myCoinPrice, orderQuantityResult, currentPrice);
                            if (averageOrderPrice >= 100) {
                                int purchasePrice = (int) round(averageOrderPrice);
                                db.myCoinDAO().updatePurchasePriceInt(market, purchasePrice);
                            } else {
                                averageOrderPrice = Double.valueOf(String.format("%.2f", averageOrderPrice));
                                db.myCoinDAO().updatePurchasePrice(market, averageOrderPrice);
                            }

                            Double quantityResult = Double.valueOf(String.format("%.8f", myCoinQuantity + orderQuantityResult));

                            db.myCoinDAO().updateQuantity(market, quantityResult);
                            db.userDAO().updateMinusMoney(round(orderAmount));
                            User user = db.userDAO().getAll();
                            tv_orderableAmount.setText(decimalFormat.format(user.krw));

                            myCoin = db.myCoinDAO().isInsert(market);
                            et_orderCoinQuantity.setText("");
                            tv_orderCoinTotalAmount.setText("");

                            db.transactionInfoDAO().insert(market,currentPrice,orderQuantityResult,round(orderAmount),"bid",System.currentTimeMillis());

                            Toast.makeText(context, "매수 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("취소", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
                editText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        editText.requestFocus();
                        imm.showSoftInput(editText,0);
                    }
                },100);
            }
        });

//        et_sellCoinTotalAmount.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(et_sellCoinPrice.isFocused()) {
//                    et_sellCoinPrice.clearFocus();
//                }else if(et_sellCoinQuantity.isFocused()) {
//                    et_sellCoinQuantity.clearFocus();
//                }
//                EditText editText = new EditText(getActivity());
//                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("총액을 입력하세요");
//                builder.setView(editText);
//                builder.setPositiveButton("입력", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        if (!et_sellCoinPrice.getText().toString().equals("0") && et_sellCoinPrice.length() != 0 && editText.length() != 0){
//                            long amount = Long.parseLong(editText.getText().toString());
//                            Double orderPrice = Double.valueOf(et_sellCoinPrice.getText().toString().replace(",",""));
//                            Double quantity =  amount / orderPrice;
//                            et_sellCoinQuantity.setText(String.format("%.8f",quantity));
//                        }
//                    }
//                }).setNegativeButton("취소", null);
//
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
//
//                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
//                editText.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        editText.requestFocus();
//                        imm.showSoftInput(editText,0);
//                    }
//                },100);
//            }
//        });
    }
    private TextWatcher tw1(){
        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(et_orderCoinQuantity.length() != 0 && tv_orderCoinPrice.length() != 0 && linear_coinOrder.getVisibility() == View.VISIBLE){

                    Double quantity = Double.valueOf(et_orderCoinQuantity.getText().toString());
                    Double price = Double.valueOf(tv_orderCoinPrice.getText().toString().replace(",",""));
                    tv_orderCoinTotalAmount.setText(decimalFormat.format(round(quantity*price)));

                }else if(et_sellCoinQuantity.length() != 0 && tv_sellCoinPrice.length() != 0 && linear_coinSell.getVisibility() == View.VISIBLE){

                    Double quantity = Double.valueOf(et_sellCoinQuantity.getText().toString());
                    Double price = Double.valueOf(tv_sellCoinPrice.getText().toString().replace(",",""));
                    tv_sellCoinTotalAmount.setText(decimalFormat.format(round(quantity*price)));

                }else if(et_orderCoinQuantity.length() == 0 && tv_orderCoinPrice.length() != 0 && linear_coinOrder.getVisibility() == View.VISIBLE){

                    tv_orderCoinTotalAmount.setText("0");

                }else if(et_sellCoinQuantity.length() == 0 && tv_sellCoinPrice.length() != 0 && linear_coinSell.getVisibility() == View.VISIBLE){

                    tv_sellCoinTotalAmount.setText("0");

                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        return textWatcher1;
    }
    private TextWatcher tw2(EditText editText){
        TextWatcher textWatcher1 = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(!TextUtils.isEmpty(charSequence.toString()) && !charSequence.toString().equals(commaResult)
                        && Double.valueOf(charSequence.toString().replaceAll(",","")) >= 1000) {
                    commaResult = decimalFormat.format(Double.parseDouble(charSequence.toString().replaceAll(",","")));
                    editText.setText(commaResult);
                    editText.setSelection(commaResult.length());
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
        tv_orderCoinPrice = rootView.findViewById(R.id.tv_orderCoinPrice);
        et_orderCoinQuantity = rootView.findViewById(R.id.et_orderCoinQuantity);
        tv_orderCoinTotalAmount = rootView.findViewById(R.id.tv_orderCoinTotalAmount);
        include_coin_parent = rootView.findViewById(R.id.include_coin_parent);
        spinner_orderCoinQuantity = rootView.findViewById(R.id.spinner_orderCoinQuantity);
        linear_coinSell = rootView.findViewById(R.id.linear_coinSell);
        linear_coinOrder = rootView.findViewById(R.id.linear_coinOrder);
        tv_sellAbleCoinQuantity = rootView.findViewById(R.id.tv_sellAbleCoinQuantity);
        tv_sellAbleAmount = rootView.findViewById(R.id.tv_sellAbleAmount);
        et_sellCoinQuantity = rootView.findViewById(R.id.et_sellCoinQuantity);
        tv_sellCoinPrice = rootView.findViewById(R.id.tv_sellCoinPrice);
        tv_sellCoinTotalAmount = rootView.findViewById(R.id.tv_sellCoinTotalAmount);
        btn_coinSell = rootView.findViewById(R.id.btn_coinSell);
        btn_coinSellReset = rootView.findViewById(R.id.btn_coinSellReset);
        btn_coinOrderReset = rootView.findViewById(R.id.btn_coinOrderReset);
        tv_sellAbleCoinSymbol = rootView.findViewById(R.id.tv_sellAbleCoinSymbol);
        spinner_sellCoinQuantity = rootView.findViewById(R.id.spinner_sellCoinQuantity);
        rv_transactionInfo = rootView.findViewById(R.id.rv_transactionInfo);
        tv_transactionInfoIsNull = rootView.findViewById(R.id.tv_transactionInfoIsNull);
        btn_purchaseByDesignatingTotalAmount = rootView.findViewById(R.id.btn_purchaseByDesignatingTotalAmount);
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

                    tv_orderCoinPrice.setText(decimalFormat.format(round(initPrice))+"");
                    tv_sellCoinPrice.setText(decimalFormat.format(round(initPrice))+"");

                }else if(initPrice >= 1 && initPrice < 100){

                    tv_orderCoinPrice.setText(String.format("%.2f", initPrice));
                    tv_sellCoinPrice.setText(String.format("%.2f", initPrice));

                }else if(initPrice < 1){
                    tv_orderCoinPrice.setText(String.format("%.4f", initPrice));
                    tv_sellCoinPrice.setText(String.format("%.4f", initPrice));
                }
                adapter_rvCoinArcade.setItem(coinArcadeDTOS);
                adapter_rvCoinArcade.notifyDataSetChanged();
                rv_coinArcade.scrollToPosition(9);
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

                adapter_rvCoinArcade = new Adapter_rvCoinArcade(coinArcadeDTOS, context, openingPrice,customLodingDialog);
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

                    Double currentPrice = ((Activity_coinInfo)context).getGlobalCurrentPrice();

                        if(et_orderCoinQuantity.length() == 0 || tv_orderCoinPrice.length() == 0 || tv_orderCoinTotalAmount.length() == 0){
                            Toast.makeText(context, "빈곳없이 매수요청 해주세요!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Double orderQuantity = Double.valueOf(et_orderCoinQuantity.getText().toString());
                        long orderAmount = round(currentPrice * orderQuantity);

                        MyCoin myCoin = null;
                        myCoin = db.myCoinDAO().isInsert(market);

                        if (round(orderAmount) > leftMoney) {
                            Toast.makeText(context, "보유 KRW가 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (myCoin == null && orderAmount <= leftMoney) {

                            if (round(orderAmount) < 5000) {
                                Toast.makeText(context, "5000원 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            MyCoin myCoin1 = new MyCoin(market, currentPrice, koreanName, symbol, orderQuantity);
                            db.myCoinDAO().insert(myCoin1);
                            db.userDAO().updateMinusMoney(round(orderAmount));

                            User user = db.userDAO().getAll();
                            tv_orderableAmount.setText(decimalFormat.format(user.krw));

                            myCoin = db.myCoinDAO().isInsert(market);
                            tv_sellAbleCoinQuantity.setText(String.format("%.8f", myCoin.getQuantity()));

                            et_orderCoinQuantity.setText("0");
                            tv_orderCoinTotalAmount.setText("0");

                            db.transactionInfoDAO().insert(market,currentPrice,orderQuantity,round(orderAmount),"bid", System.currentTimeMillis());

                            Toast.makeText(context, "매수 되었습니다.", Toast.LENGTH_SHORT).show();

                        } else if (myCoin != null && orderAmount <= leftMoney) {

                            if (round(orderAmount) < 5000) {
                                Toast.makeText(context, "5000원 이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            Double myCoinQuantity = myCoin.getQuantity();
                            Double myCoinPrice = myCoin.getPurchasePrice();

                            Double averageOrderPrice = averageOrderPriceCalculate(myCoinQuantity, myCoinPrice, orderQuantity, currentPrice);
                            if (averageOrderPrice >= 100) {
                                int purchasePrice = (int) round(averageOrderPrice);
                                db.myCoinDAO().updatePurchasePriceInt(market, purchasePrice);
                            } else if(averageOrderPrice >= 1 && averageOrderPrice < 100){
                                averageOrderPrice = Double.valueOf(String.format("%.2f", averageOrderPrice));
                                db.myCoinDAO().updatePurchasePrice(market, averageOrderPrice);
                            }else if(averageOrderPrice > 0 && averageOrderPrice < 1){
                                averageOrderPrice = Double.valueOf(String.format("%.4f", averageOrderPrice));
                                db.myCoinDAO().updatePurchasePrice(market, averageOrderPrice);
                            }

                            Double quantityResult = Double.valueOf(String.format("%.8f", myCoinQuantity + orderQuantity));

                            db.myCoinDAO().updateQuantity(market, quantityResult);
                            db.userDAO().updateMinusMoney(round(orderAmount));
                            User user = db.userDAO().getAll();
                            tv_orderableAmount.setText(decimalFormat.format(user.krw));

                            myCoin = db.myCoinDAO().isInsert(market);
                            tv_sellAbleCoinQuantity.setText(String.format("%.8f", myCoin.getQuantity()));
                            et_orderCoinQuantity.setText("0");
                            tv_orderCoinTotalAmount.setText("0");

                            db.transactionInfoDAO().insert(market,currentPrice,orderQuantity,round(orderAmount),"bid",System.currentTimeMillis());

                            Toast.makeText(context, "매수 되었습니다.", Toast.LENGTH_SHORT).show();
                        }
            }
        });
    }
    private void setBtn_coinSell(){

        btn_coinSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Double sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString());
                    Double currentPrice = ((Activity_coinInfo)context).getGlobalCurrentPrice();

                        if (et_sellCoinQuantity.length() == 0 || tv_sellCoinPrice.length() == 0|| tv_sellCoinPrice.length() == 0 ) {
                            Toast.makeText(context, "빈곳없이 매도요청 해주세요!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Double sellCoinQuantity = Double.valueOf(et_sellCoinQuantity.getText().toString());
                        Double sellCoinPrice = Double.valueOf(tv_sellCoinPrice.getText().toString().replaceAll(",", ""));

                        if(sellAbleCoinQuantity < sellCoinQuantity){
                            Toast.makeText(context, "판매가능한 수량이 부족합니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if(round(sellCoinQuantity * currentPrice) < 5000){
                            Toast.makeText(context, "5000이상만 주문 가능합니다.", Toast.LENGTH_SHORT).show();
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

                            et_sellCoinQuantity.setText("0");
                            tv_sellCoinTotalAmount.setText("0");
                            Toast.makeText(context, "매도 되었습니다.", Toast.LENGTH_SHORT).show();

                            db.transactionInfoDAO().insert(market,currentPrice,sellCoinQuantity,round(currentPrice*sellAbleCoinQuantity),"ask",System.currentTimeMillis());
                        }
            }
        });
    }

    private void setSpinner_sellCoinQuantity(){
        String[] items = {"%선택","최대","50%","25%","10%"};

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item,items);

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

        ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item,items);

        spinner_orderCoinQuantity.setAdapter(adapter);

            spinner_orderCoinQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                        long orderablePrice = 0;

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
                        if(orderablePrice!=0) {
                            Double price = Double.valueOf(tv_orderCoinPrice.getText().toString().replace(",", ""));
                            et_orderCoinQuantity.setText(String.format("%.8f", orderablePrice / price));
                        }
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
                tv_sellCoinTotalAmount.setText("0");
            }
        });
    }

    private void setBtn_coinOrderReset(){
        btn_coinOrderReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                et_orderCoinQuantity.setText("0");
                tv_orderCoinTotalAmount.setText("0");
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

                            if (context != null) {
                                ((Activity_coinInfo)context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                        if(adapter_rvCoinArcade == null){
                                            getOpeningPriceFromApi();
                                            getCoinArcadeInfo();
                                        }

                                        Double currentPrice;
                                        try {
                                            currentPrice = ((Activity_coinInfo)context).getGlobalCurrentPrice();
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            currentPrice = null;
                                        }

                                        if (currentPrice != null) {
                                            tv_sellAbleAmount.setText("= " + decimalFormat.format(round(currentPrice * Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()))));

                                            if(adapter_rvCoinArcade != null)
                                            adapter_rvCoinArcade.notifyDataSetChanged();

                                            if (currentPrice >= 100) {
                                                tv_orderCoinPrice.setText(decimalFormat.format(currentPrice));
                                                tv_sellCoinPrice.setText(decimalFormat.format(currentPrice));
                                            } else if(currentPrice >= 1 && currentPrice < 100){

                                                tv_orderCoinPrice.setText(String.format("%.2f", currentPrice));
                                                tv_sellCoinPrice.setText(String.format("%.2f", currentPrice));

                                            }else if(currentPrice < 1){
                                                tv_orderCoinPrice.setText(String.format("%.4f", currentPrice));
                                                tv_sellCoinPrice.setText(String.format("%.4f", currentPrice));
                                            }

                                            if(linear_coinOrder.getVisibility() == View.VISIBLE && tv_orderCoinTotalAmount.length() != 0
                                                    && !tv_orderCoinTotalAmount.equals("0") && et_orderCoinQuantity.length() != 0 && !et_orderCoinQuantity.equals("0")){

                                                Double orderQuantity = Double.parseDouble(et_orderCoinQuantity.getText().toString());

                                                if(orderQuantity * currentPrice >= 100)
                                                tv_orderCoinTotalAmount.setText(decimalFormat.format(round(orderQuantity * currentPrice)));
                                                else
                                                    tv_orderCoinTotalAmount.setText(String.format("%.2f",orderQuantity * currentPrice));

                                            }else if(linear_coinSell.getVisibility() == View.VISIBLE && tv_sellCoinTotalAmount.length() != 0 && !tv_sellCoinTotalAmount.equals("0")){

                                                Double sellQuantity;
                                                if(et_sellCoinQuantity.length() != 0) {
                                                    sellQuantity = Double.parseDouble(et_sellCoinQuantity.getText().toString());
                                                }else{
                                                    sellQuantity = 0.0;
                                                }

                                                if(sellQuantity * currentPrice >= 100) {
                                                    tv_sellCoinTotalAmount.setText(decimalFormat.format(round(sellQuantity * currentPrice)));
                                                } else {
                                                    tv_sellCoinTotalAmount.setText(String.format("%.2f", sellQuantity * currentPrice));
                                                }
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