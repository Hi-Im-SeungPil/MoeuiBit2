package org.jeonfeel.moeuibit2.Fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
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
import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO;
import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart;
import org.jeonfeel.moeuibit2.Fragment.Chart.GetUpBitCoins;
import org.jeonfeel.moeuibit2.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.MyCoin;
import org.jeonfeel.moeuibit2.R;
import org.jeonfeel.moeuibit2.User;
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
    private LinearLayout linear_PriceSell1,linear_PriceSell2,linear_PriceSell3;
    private ConstraintLayout const_priceSell4,const_marketPriceSell;
    private RecyclerView rv_coinArcade;
    private String market,koreanName,symbol;
    private ArrayList<CoinArcadeDTO> coinArcadeDTOS;
    private Adapter_rvCoinArcade adapter_rvCoinArcade;
    private Double openingPrice;
    private int index = 0; //ask coinArcade set을 위해!!
    private int index2 = 0;//bid coinArcade set을 위해!!
    private GetUpBitCoinArcade getUpBitCoinArcade;
    private RadioGroup radioGroup_orderWays,radioGroup_sellWays;
    private LinearLayout linear_PriceOrder1,linear_PriceOrder3,linear_PriceOrder2;
    private ConstraintLayout const_priceOrder4,const_marketPriceOrder;
    private TextView tv_orderableAmount,tv_sellAbleCoinQuantity,tv_sellAbleAmount,tv_sellAbleCoinSymbol;
    private Button btn_coinOrder;
    private MoEuiBitDatabase db;
    private long leftMoney;
    private EditText et_orderCoinPrice,et_orderCoinQuantity, et_orderCoinTotalAmount;
    private EditText et_orderCoinTotalAmountMarketPriceVer;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private Spinner spinner_orderCoinQuantity,spinner_orderCoinQuantityMarketPriceVer,spinner_sellCoinQuantity,spinner_sellCinQuantityMarketPriceVer;
    private EditText et_sellCoinQuantity,et_sellCoinPrice,et_sellCoinTotalAmount,et_sellCoinTotalAmountMarketPriceVer;
    private Button btn_coinSell,btn_coinSellReset;
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
        setRadioGroup_orderWays(rootView);
        setRadioGroup_sellWays(rootView);
        initFragment_coinOrder();
        setTabLayout(rootView);

        return rootView;
    }
    private void setRv_coinArcade() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        rv_coinArcade.setLayoutManager(linearLayoutManager);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void initFragment_coinOrder(){
        setBtn_coinOrder();
        setBtn_coinSell();
        setSpinner_orderCoinQuantity();
        setSpinner_orderCoinQuantityMarketPriceVer();
        setSpinner_sellCoinQuantity();
        setSpinner_sellCinQuantityMarketPriceVer();

        linear_coinSell.setVisibility(View.GONE);
        tv_sellAbleCoinSymbol.setText(symbol);

        db = MoEuiBitDatabase.getInstance(getActivity());

        User user = db.userDAO().getAll();
        leftMoney = user.krw;
        tv_orderableAmount.setText(decimalFormat.format(leftMoney));

        MyCoin myCoin = db.myCoinDAO().isInsert(market);
        if(myCoin != null){
            tv_sellAbleCoinQuantity.setText(String.format("%.8f",myCoin.getQuantity()));
        }else{
            tv_sellAbleCoinQuantity.setText("0");
        }

        et_orderCoinTotalAmount.setCursorVisible(false);
        et_sellCoinTotalAmount.setCursorVisible(false);
        TextWatcher tw1 = tw1();
        TextWatcher tw2 = tw2();
        et_orderCoinPrice.addTextChangedListener(tw2);
        et_orderCoinQuantity.addTextChangedListener(tw1);
        et_sellCoinPrice.addTextChangedListener(tw2);
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
        // 총액 설정.
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
 // 총액 설정.
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
                            int amount = Integer.parseInt(editText.getText().toString());
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
        radioGroup_orderWays = rootView.findViewById(R.id.radioGroup_orderWays);
        linear_PriceOrder1 = rootView.findViewById(R.id.linear_PriceOrder1);
        linear_PriceOrder3 = rootView.findViewById(R.id.linear_PriceOrder3);
        linear_PriceOrder2 = rootView.findViewById(R.id.linear_PriceOrder2);
        const_priceOrder4 = rootView.findViewById(R.id.const_priceOrder4);
        const_marketPriceOrder = rootView.findViewById(R.id.const_marketPriceOrder);
        tv_orderableAmount = rootView.findViewById(R.id.tv_orderableAmount);
        btn_coinOrder = rootView.findViewById(R.id.btn_coinOrder);
        et_orderCoinPrice = rootView.findViewById(R.id.et_orderCoinPrice);
        et_orderCoinQuantity = rootView.findViewById(R.id.et_orderCoinQuantity);
        et_orderCoinTotalAmount = rootView.findViewById(R.id.et_orderCoinTotalAmount);
        include_coin_parent = rootView.findViewById(R.id.include_coin_parent);
        spinner_orderCoinQuantity = rootView.findViewById(R.id.spinner_orderCoinQuantity);
        et_orderCoinTotalAmountMarketPriceVer = rootView.findViewById(R.id.et_orderCoinTotalAmountMarketPriceVer);
        spinner_orderCoinQuantityMarketPriceVer = rootView.findViewById(R.id.spinner_orderCoinQuantityMarketPriceVer);
        linear_coinSell = rootView.findViewById(R.id.linear_coinSell);
        linear_coinOrder = rootView.findViewById(R.id.linear_coinOrder);
        linear_PriceSell1 = rootView.findViewById(R.id.linear_PriceSell1);
        linear_PriceSell2 = rootView.findViewById(R.id.linear_PriceSell2);
        linear_PriceSell3 = rootView.findViewById(R.id.linear_PriceSell3);
        const_priceSell4 = rootView.findViewById(R.id.const_priceSell4);
        const_marketPriceSell = rootView.findViewById(R.id.const_marketPriceSell);
        radioGroup_sellWays = rootView.findViewById(R.id.radioGroup_sellWays);
        tv_sellAbleCoinQuantity = rootView.findViewById(R.id.tv_sellAbleCoinQuantity);
        tv_sellAbleAmount = rootView.findViewById(R.id.tv_sellAbleAmount);
        et_sellCoinQuantity = rootView.findViewById(R.id.et_sellCoinQuantity);
        et_sellCoinPrice = rootView.findViewById(R.id.et_sellCoinPrice);
        et_sellCoinTotalAmount = rootView.findViewById(R.id.et_sellCoinTotalAmount);
        et_sellCoinTotalAmountMarketPriceVer = rootView.findViewById(R.id.et_sellCoinTotalAmountMarketPriceVer);
        btn_coinSell = rootView.findViewById(R.id.btn_coinSell);
        btn_coinSellReset = rootView.findViewById(R.id.btn_coinSellReset);
        tv_sellAbleCoinSymbol = rootView.findViewById(R.id.tv_sellAbleCoinSymbol);
        spinner_sellCoinQuantity = rootView.findViewById(R.id.spinner_sellCoinQuantity);
        spinner_sellCinQuantityMarketPriceVer = rootView.findViewById(R.id.spinner_sellCinQuantityMarketPriceVer);
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
    private void setRadioGroup_orderWays(View rootView) {

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
    private void setRadioGroup_sellWays(View rootView){
        RadioButton radio_setPriceVerSell = rootView.findViewById(R.id.radio_setPriceVerSell);
        radio_setPriceVerSell.setChecked(true);
        const_marketPriceSell.setVisibility(View.GONE);
        radioGroup_sellWays.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radio_setPriceVerSell :
                        linear_PriceSell1.setVisibility(View.VISIBLE);
                        linear_PriceSell2.setVisibility(View.VISIBLE);
                        linear_PriceSell3.setVisibility(View.VISIBLE);
                        const_priceSell4.setVisibility(View.VISIBLE);
                        const_marketPriceSell.setVisibility(View.GONE);
                        break;
                    case R.id.radio_marketPriceVerSell :
                        linear_PriceSell1.setVisibility(View.GONE);
                        linear_PriceSell2.setVisibility(View.GONE);
                        linear_PriceSell3.setVisibility(View.GONE);
                        const_priceSell4.setVisibility(View.GONE);
                        const_marketPriceSell.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }
    private void setBtn_coinOrder(){
        btn_coinOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Double currentPrice = Activity_coinInfo.currentPrice;

                int radioId = radioGroup_orderWays.getCheckedRadioButtonId();

                if (radioId == R.id.radio_setPrice) {
                    Double totalPrice = Double.valueOf(et_orderCoinTotalAmount.getText().toString().replace(",",""));

                    Double orderPrice = Double.parseDouble(et_orderCoinPrice.getText().toString().replace(",", ""));
                    Double orderQuantity = Double.valueOf(String.format("%.8f",totalPrice/currentPrice));
                    Double orderAmount = currentPrice * orderQuantity;

                    MyCoin myCoin = null;
                    myCoin = db.myCoinDAO().isInsert(market);

                    if (myCoin == null && orderPrice >= currentPrice && orderAmount <= leftMoney && orderAmount >= 5000) {

                        MyCoin myCoin1 = new MyCoin(market, currentPrice, koreanName, symbol, orderQuantity);
                        db.myCoinDAO().insert(myCoin1);
                        db.userDAO().updateMinusMoney(round(orderAmount));

                        User user = db.userDAO().getAll();
                        tv_orderableAmount.setText(decimalFormat.format(user.krw));

                        myCoin = db.myCoinDAO().isInsert(market);
                        tv_sellAbleCoinQuantity.setText(String.format("%.8f",myCoin.getQuantity()));

                        et_orderCoinPrice.setText("");
                        et_orderCoinQuantity.setText("");
                        et_orderCoinTotalAmount.setText("");
                        et_orderCoinTotalAmountMarketPriceVer.setText("");
                        Toast.makeText(getActivity(), "매수 되었습니다.", Toast.LENGTH_SHORT).show();

                    } else if (myCoin != null && orderPrice >= currentPrice && orderAmount <= leftMoney && orderAmount >= 5000) {

                        Double myCoinQuantity = myCoin.getQuantity();
                        Double myCoinPrice = myCoin.getPurchasePrice();

                        Double averageOrderPrice = averageOrderPriceCalculate(myCoinQuantity, myCoinPrice, orderQuantity, currentPrice);
                        if(averageOrderPrice >= 100){
                            int purchasePrice = (int) round(averageOrderPrice);
                            db.myCoinDAO().updatePurchasePriceInt(market,purchasePrice);
                        }else {
                            averageOrderPrice = Double.valueOf(String.format("%.2f",averageOrderPrice));
                            db.myCoinDAO().updatePurchasePrice(market, averageOrderPrice);
                        }

                        Double quantityResult = Double.valueOf(String.format("%.8f",myCoinQuantity + orderQuantity));

                        db.myCoinDAO().updateQuantity(market, quantityResult);
                        db.userDAO().updateMinusMoney(round(orderAmount));
                        User user = db.userDAO().getAll();
                        tv_orderableAmount.setText(decimalFormat.format(user.krw));

                        myCoin = db.myCoinDAO().isInsert(market);
                        tv_sellAbleCoinQuantity.setText(String.format("%.8f",myCoin.getQuantity()));
                        et_orderCoinPrice.setText("");
                        et_orderCoinQuantity.setText("");
                        et_orderCoinTotalAmount.setText("");
                        et_orderCoinTotalAmountMarketPriceVer.setText("");
                        Toast.makeText(getActivity(), "매수 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else if(radioId == R.id.radio_marketPrice){
                    int totalPrice = Integer.parseInt(et_orderCoinTotalAmountMarketPriceVer.getText().toString().replace(",",""));
                    Double orderQuantity = Double.valueOf(String.format("%.8f",totalPrice/currentPrice));

                    MyCoin myCoin = null;
                    myCoin = db.myCoinDAO().isInsert(market);

                    if(myCoin == null && totalPrice <= leftMoney && totalPrice >= 5000){
                        MyCoin myCoin1 = new MyCoin(market, currentPrice, koreanName, symbol, orderQuantity);
                        db.myCoinDAO().insert(myCoin1);
                        db.userDAO().update((long) leftMoney - totalPrice);

                        User user = db.userDAO().getAll();
                        tv_orderableAmount.setText(decimalFormat.format(user.krw));
                        et_orderCoinPrice.setText("");
                        et_orderCoinQuantity.setText("");
                        et_orderCoinTotalAmount.setText("");
                        et_orderCoinTotalAmountMarketPriceVer.setText("");
                        Toast.makeText(getActivity(), "매수 되었습니다.", Toast.LENGTH_SHORT).show();

                    }else if(myCoin != null && totalPrice <= leftMoney && totalPrice >= 5000){
                        Double myCoinQuantity = myCoin.getQuantity();
                        Double myCoinPrice = myCoin.getPurchasePrice();

                        Double averageOrderPrice = averageOrderPriceCalculate(myCoinQuantity, myCoinPrice, orderQuantity, currentPrice);

                        if(averageOrderPrice >= 100){
                            int purchasePrice = (int) round(averageOrderPrice);
                            db.myCoinDAO().updatePurchasePriceInt(market,purchasePrice);
                        }else {
                            averageOrderPrice = Double.valueOf(String.format("%.2f",averageOrderPrice));
                            db.myCoinDAO().updatePurchasePrice(market, averageOrderPrice);
                        }
                        Double quantityResult = Double.valueOf(String.format("%.8f",myCoinQuantity + orderQuantity));

                        db.myCoinDAO().updatePlusQuantity(market, quantityResult);
                        db.userDAO().update((long) leftMoney - totalPrice);

                        User user = db.userDAO().getAll();
                        tv_orderableAmount.setText(decimalFormat.format(user.krw));
                        et_orderCoinPrice.setText("");
                        et_orderCoinQuantity.setText("");
                        et_orderCoinTotalAmount.setText("");
                        et_orderCoinTotalAmountMarketPriceVer.setText("");
                        Toast.makeText(getActivity(), "매수 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void setBtn_coinSell(){
        btn_coinSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int radioId = radioGroup_sellWays.getCheckedRadioButtonId();
                Double sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString());

                if(radioId == R.id.radio_setPriceVerSell && sellAbleCoinQuantity > 0){

                    Double sellCoinQuantity = Double.valueOf(et_sellCoinQuantity.getText().toString());
                    Double sellCoinPrice = Double.valueOf(et_sellCoinPrice.getText().toString().replaceAll(",",""));

                    //지정 시장가 매도
                    if(sellCoinQuantity <= sellAbleCoinQuantity && sellCoinPrice <= Activity_coinInfo.currentPrice && round(sellCoinQuantity*sellCoinPrice) >= 5000){

                        Double initQuantity = db.myCoinDAO().isInsert(market).getQuantity();
                        Double quantityResult = Double.valueOf(String.format("%.8f",initQuantity - sellCoinQuantity));

                        db.myCoinDAO().updateQuantity(market,quantityResult);

                        db.userDAO().updatePlusMoney(round(Activity_coinInfo.currentPrice * sellCoinQuantity));

                        Double quantity = db.myCoinDAO().isInsert(market).getQuantity();
                        tv_sellAbleCoinQuantity.setText(String.format("%.8f",quantity));

                        User user = db.userDAO().getAll();
                        tv_orderableAmount.setText(decimalFormat.format(user.krw));

                        et_sellCoinQuantity.setText("");
                        et_sellCoinPrice.setText("");
                        et_sellCoinTotalAmount.setText("");
                        et_sellCoinTotalAmountMarketPriceVer.setText("");
                        Toast.makeText(getActivity(), "매도 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }else if(radioId == R.id.radio_marketPriceVerSell && sellAbleCoinQuantity > 0){

                    Double sellCoinQuantity = Double.valueOf(et_sellCoinTotalAmountMarketPriceVer.getText().toString());

                    if(sellCoinQuantity <= sellAbleCoinQuantity && round(sellCoinQuantity * Activity_coinInfo.currentPrice) >= 5000){

                        Double initQuantity = db.myCoinDAO().isInsert(market).getQuantity();
                        Double quantityResult = Double.valueOf(String.format("%.8f",initQuantity - sellCoinQuantity));

                        db.myCoinDAO().updateQuantity(market,quantityResult);

                        db.userDAO().updatePlusMoney(round(Activity_coinInfo.currentPrice*sellCoinQuantity));

                        Double quantity = db.myCoinDAO().isInsert(market).getQuantity();
                        tv_sellAbleCoinQuantity.setText(String.format("%.8f",quantity));

                        User user = db.userDAO().getAll();

                        tv_orderableAmount.setText(decimalFormat.format(user.krw));

                        et_sellCoinQuantity.setText("");
                        et_sellCoinPrice.setText("");
                        et_sellCoinTotalAmount.setText("");
                        et_sellCoinTotalAmountMarketPriceVer.setText("");
                        Toast.makeText(getActivity(), "매도 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void setSpinner_sellCoinQuantity(){
        String[] items = {"최대","50%","25%","10%"};

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,items);

        spinner_sellCoinQuantity.setAdapter(adapter);

        spinner_sellCoinQuantity.setSelection(3,false);

        spinner_sellCoinQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                Double sellAbleCoinQuantity = 0.0;

                switch (spinner_sellCoinQuantity.getSelectedItemPosition()) {
                    case 0:
                        sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString());
                        break;
                    case 1:
                        sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()) / 2;
                        break;
                    case 2:
                        sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()) / 4;
                        break;
                    case 3:
                        sellAbleCoinQuantity = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()) / 10;
                        break;
                }
                et_sellCoinQuantity.setText(String.format("%.8f", sellAbleCoinQuantity));

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void setSpinner_sellCinQuantityMarketPriceVer(){
        String[] items = {"최대", "50%", "25%", "10%"};

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);

        spinner_sellCinQuantityMarketPriceVer.setAdapter(adapter);

        spinner_sellCinQuantityMarketPriceVer.setSelection(3,false);

        spinner_sellCinQuantityMarketPriceVer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Double sellAbleCoinAmount = Double.valueOf(tv_sellAbleCoinQuantity.getText().toString());

                switch (spinner_sellCinQuantityMarketPriceVer.getSelectedItemPosition()){
                    case 0:
                        et_sellCoinTotalAmountMarketPriceVer.setText(String.format("%.8f",sellAbleCoinAmount));
                        break;
                    case 1:
                        et_sellCoinTotalAmountMarketPriceVer.setText(String.format("%.8f",sellAbleCoinAmount / 2));
                        break;
                    case 2:
                        et_sellCoinTotalAmountMarketPriceVer.setText(String.format("%.8f",sellAbleCoinAmount / 4));
                        break;
                    case 3:
                        et_sellCoinTotalAmountMarketPriceVer.setText(String.format("%.8f",sellAbleCoinAmount / 10));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }
    private void setSpinner_orderCoinQuantity(){

        String[] items = {"최대","50%","25%","10%"};

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item,items);

        spinner_orderCoinQuantity.setAdapter(adapter);

        spinner_orderCoinQuantity.setSelection(3,false);

            spinner_orderCoinQuantity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {

                        Double price = Double.valueOf(et_orderCoinPrice.getText().toString().replace(",", ""));
                        int orderablePrice = 0;

                            switch (spinner_orderCoinQuantity.getSelectedItemPosition()) {
                                case 0:
                                    orderablePrice = Integer.parseInt(tv_orderableAmount.getText().toString().replace(",", ""));
                                    break;
                                case 1:
                                    orderablePrice = round(Integer.parseInt(tv_orderableAmount.getText().toString().replace(",", "")) / 2);
                                    break;
                                case 2:
                                    orderablePrice = round(Integer.parseInt(tv_orderableAmount.getText().toString().replace(",", "")) / 4);
                                    break;
                                case 3:
                                    orderablePrice = round(Integer.parseInt(tv_orderableAmount.getText().toString().replace(",", "")) / 10);
                                    break;
                            }
                            et_orderCoinQuantity.setText(String.format("%.8f", orderablePrice / price));


                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
    private void setSpinner_orderCoinQuantityMarketPriceVer() {

        String[] items = {"최대", "50%", "25%", "10%"};

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);

        spinner_orderCoinQuantityMarketPriceVer.setAdapter(adapter);

        spinner_orderCoinQuantityMarketPriceVer.setSelection(3,false);

        spinner_orderCoinQuantityMarketPriceVer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 int orderAbleAmount = Integer.parseInt(tv_orderableAmount.getText().toString().replaceAll(",",""));

                    switch (spinner_orderCoinQuantityMarketPriceVer.getSelectedItemPosition()){
                        case 0:
                            et_orderCoinTotalAmountMarketPriceVer.setText(decimalFormat.format(orderAbleAmount));
                            break;
                        case 1:
                            et_orderCoinTotalAmountMarketPriceVer.setText(decimalFormat.format(round(orderAbleAmount/2)));
                            break;
                        case 2:
                            et_orderCoinTotalAmountMarketPriceVer.setText(decimalFormat.format(round(orderAbleAmount/4)));
                            break;
                        case 3:
                            et_orderCoinTotalAmountMarketPriceVer.setText(decimalFormat.format(round(orderAbleAmount/10)));
                            break;
                    }
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
                }else if(position == 1){
                    linear_coinOrder.setVisibility(View.GONE);
                    linear_coinSell.setVisibility(View.VISIBLE);
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

                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                    if(Activity_coinInfo.currentPrice != null)
                                    tv_sellAbleAmount.setText("= "+decimalFormat.format(round(Activity_coinInfo.currentPrice * Double.valueOf(tv_sellAbleCoinQuantity.getText().toString()))));

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