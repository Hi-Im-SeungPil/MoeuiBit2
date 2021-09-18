package org.jeonfeel.moeuibit2.Fragment.Chart;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.EntryXComparator;

import org.jeonfeel.moeuibit2.CustomLodingDialog;
import org.jeonfeel.moeuibit2.DTOS.CoinCandleDataDTO;
import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.Database.MyCoin;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import static java.lang.Math.round;

public class Fragment_chart extends Fragment {

    private CombinedChart combinedChart;
    private String market;
    private static ArrayList<CoinCandleDataDTO> coinCandleDataDTOS;
    private ArrayList<CandleEntry> candleEntries;
    private ArrayList<BarEntry> barEntries;
    private int candlePosition = 0;
    private CandleData candleData;
    private CandleDataSet candleDataSet;
    private BarData barData;
    private BarDataSet barDataSet;
    private CombinedData finalCombinedData;
    private int minute = 1;
    private String period= "minutes";
    private myValueFormatter myValueFormatter;
    private MoEuiBitDatabase db;
    LimitLine ll2;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private CustomLodingDialog customLodingDialog;

    private GetRecentCoinChart getRecentCoinChart;

    private RadioGroup rg_chart,rg_minuteGroup;
    private RadioButton radio_minuteChart,radio_oneMinute;

    // TODO: Rename and change types of parameters

    public Fragment_chart(String market) {
        this.market = market;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_chart, container, false);

        customLodingDialog = new CustomLodingDialog(getActivity());
        customLodingDialog.show();
        FindViewById(rootView);
        db = MoEuiBitDatabase.getInstance(getContext());
        initCombinedChart();
        chartDataSet();
        radio_minuteChart.setChecked(true);
        radio_oneMinute.setChecked(true);
        getCoinCandleData(minute,period);
        setRg_chart();
        setRg_minuteGroup();

        return rootView;

    }

    private void FindViewById(View rootView) {

        combinedChart = rootView.findViewById(R.id.combinedChart);
        rg_minuteGroup = rootView.findViewById(R.id.rg_minuteGroup);
        rg_chart = rootView.findViewById(R.id.rg_chart);
        radio_oneMinute = rootView.findViewById(R.id.radio_oneMinute);
        radio_minuteChart = rootView.findViewById(R.id.radio_minuteChart);
    }

    // 차트 초기화
    private void initCombinedChart() {

        MoEuiBitMarkerView moEuiBitMarkerView = new MoEuiBitMarkerView(getActivity(),R.layout.candle_info_marker,combinedChart);

        combinedChart.getDescription().setEnabled(false);
        combinedChart.setScaleYEnabled(false);
        combinedChart.setPinchZoom(false);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBorders(false);
        combinedChart.setDoubleTapToZoomEnabled(false);
        combinedChart.setMarker(moEuiBitMarkerView);

        combinedChart.setDragDecelerationEnabled(false);

        combinedChart.setDragEnabled(true);
        combinedChart.setHighlightPerDragEnabled(true);
        combinedChart.setHighlightPerTapEnabled(false);
        combinedChart.fitScreen();
        combinedChart.setAutoScaleMinMaxEnabled(true);
        combinedChart.setBackgroundColor(Color.parseColor("#212121"));

        combinedChart.setOnChartGestureListener(new OnChartGestureListener() {

            //터치 하면 하이라이트 생성
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

                try {
                    Highlight highlight = combinedChart.getHighlightByTouchPoint(me.getX(), me.getY());

                    if (highlight != null) {
                        combinedChart.highlightValue(highlight, true);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    ((Activity) getActivity()).finish();
                    Toast.makeText(getActivity(), "오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {


            }

            @Override
            public void onChartDoubleTapped(MotionEvent me) {

            }

            @Override
            public void onChartSingleTapped(MotionEvent me) {


            }

            @Override
            public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY) {

            }

            @Override
            public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

            }

            @Override
            public void onChartTranslate(MotionEvent me, float dX, float dY) {

            }
        });

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#FFFFFFFF"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelCount(3,true);
        xAxis.setDrawLabels(true);
        xAxis.setGranularity(3f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisLineColor(Color.parseColor("#FFFFFFFF"));

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setLabelCount(5, true);
        rightAxis.setTextColor(Color.WHITE);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisLineColor(Color.WHITE);
        rightAxis.setMinWidth(50f);

        Legend l = combinedChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);

        combinedChart.zoom(4f,0f,0,0);

    }

    private void chartDataSet(){

        if (candleEntries == null) {
            candleEntries = new ArrayList<>();
            barEntries = new ArrayList<>();
        }

        candleDataSet = new CandleDataSet(candleEntries, "");
        candleDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);

        candleDataSet.setShadowColor(Color.parseColor("#E6BDBDBD"));
        candleDataSet.setShadowWidth(1f);

        candleDataSet.setDecreasingColor(Color.parseColor("#E66799FF"));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);

        candleDataSet.setIncreasingColor(Color.parseColor("#E6F15F5F"));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);

        candleDataSet.setNeutralColor(Color.parseColor("#E6BDBDBD"));
        candleDataSet.setDrawValues(false);

//---------------------------------------------------------------------------------------------------------

        barDataSet = new BarDataSet(barEntries,"");
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        barDataSet.setDrawValues(false);
        barDataSet.setColor(Color.parseColor("#33FFFFFF"));

    }

    //코인 정보 200개 받아오는 메소드
    private void getCoinCandleData(int minute, String period) {

        if(myValueFormatter != null){
            myValueFormatter = null;
        }

        if (candleData != null) {
            candleData.clearValues();
            barData.clearValues();
        }else{
            candleData = new CandleData();
            barData = new BarData();
        }

        if (finalCombinedData != null) {
            finalCombinedData.clearValues();
        }

        if (coinCandleDataDTOS != null) {
            coinCandleDataDTOS.clear();
        }else{
            coinCandleDataDTOS = new ArrayList<>();
        }

        if (candleEntries.size() != 0) {
            candleEntries.clear();
            barEntries.clear();
        }

        candlePosition = 0;

        String coinUrl = "";

        if(minute == 2) {
            coinUrl = "https://api.upbit.com/v1/candles/" + period + "?market=" + market + "&count=200";
        }else{
            coinUrl = "https://api.upbit.com/v1/candles/" + period + "/" + minute + "?market=" + market + "&count=200";
        }



        GetUpBitCoins getUpBitCoins = new GetUpBitCoins();

        try {
            JSONArray jsonArray = new JSONArray();
            jsonArray = getUpBitCoins.execute(coinUrl).get();

            if (jsonArray != null) {

                JSONObject jsonObject = new JSONObject();

                for (int i = jsonArray.length() - 1; i >= 0; i--) {
                    jsonObject = (JSONObject) jsonArray.get(i);

                    String candleDateTimeKst = jsonObject.getString("candle_date_time_kst");
                    Double openingPrice = jsonObject.getDouble("opening_price");
                    Double highPrice = jsonObject.getDouble("high_price");
                    Double lowPrice = jsonObject.getDouble("low_price");
                    Double tradePrice = jsonObject.getDouble("trade_price");
                    Double candleTransactionAmount = jsonObject.getDouble("candle_acc_trade_price");

                    float openingPrice2 = 0;

                    if (openingPrice < 100) {
                        openingPrice2 = Float.parseFloat(String.format("%.2f", openingPrice));
                    } else {
                        openingPrice2 = (float) ((float) round(openingPrice * 100) * 0.01);
                    }

                    float highPrice2 = Float.parseFloat(String.format("%.2f", highPrice));
                    float lowPrice2 = Float.parseFloat(String.format("%.2f", lowPrice));
                    float tradePrice2 = Float.parseFloat(String.format("%.2f", tradePrice));
                    float candleTransactionAmount2 = Float.parseFloat(String.valueOf(candleTransactionAmount));

                    coinCandleDataDTOS.add(new CoinCandleDataDTO(candleDateTimeKst,candleTransactionAmount));

                    candleDataSet.addEntry(new CandleEntry(candlePosition, highPrice2, lowPrice2, openingPrice2, tradePrice2));
                    barDataSet.addEntry(new BarEntry(candlePosition,candleTransactionAmount2));

                    candlePosition++;
                }

                Collections.sort(candleEntries, new EntryXComparator());
                Collections.sort(barEntries, new EntryXComparator());

                candleDataSet.notifyDataSetChanged();
                barDataSet.notifyDataSetChanged();

                candleData.addDataSet(candleDataSet);

//----------------------------------------------------------
                barData = new BarData();
                barData.addDataSet(barDataSet);

                float maxValue = barData.getYMax(YAxis.AxisDependency.LEFT);

//----------------------------------------------------------
                finalCombinedData = new CombinedData();
                finalCombinedData.setData(barData);
                finalCombinedData.setData(candleData);

                if(candleEntries.isEmpty() || barEntries.isEmpty()){
                    combinedChart.clear();
                }else {
                    combinedChart.setData(finalCombinedData);
                }

                int entryCount = combinedChart.getCandleData().getEntryCount();

                combinedChart.getXAxis().setAxisMinimum(-0.7f);
                combinedChart.getXAxis().setAxisMaximum(entryCount + 3f);

                myValueFormatter = new myValueFormatter(coinCandleDataDTOS,candleEntries);
                combinedChart.getXAxis().setValueFormatter(myValueFormatter);
                combinedChart.getBarData().setHighlightEnabled(false);

                MyCoin myCoin = db.myCoinDAO().isInsert(market);

                if(myCoin != null){
                    String average = String.valueOf(myCoin.getPurchasePrice());
                    float averageResult = Float.parseFloat(average);
                    String averageResultText = "";
                    if(averageResult >= 100){
                        averageResultText = decimalFormat.format(round(averageResult));
                    }else{
                        averageResultText = String.format("%.2f",averageResult);
                    }
                    LimitLine ll1 = new LimitLine(averageResult,"매수평균(" + averageResultText+")");
                    ll1.setLineWidth(0f);
                    ll1.setLineColor(Color.parseColor("#FFFFFFFF"));
                    ll1.setTextColor(Color.parseColor("#FFFFFFFF"));
                    ll1.enableDashedLine(10f, 1f, 0f);
                    ll1.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
                    ll1.setTextSize(10f);

                    combinedChart.getAxisRight().removeAllLimitLines();
                    combinedChart.getAxisRight().addLimitLine(ll1);
                }

                combinedChart.notifyDataSetChanged();

                combinedChart.getAxisLeft().setAxisMaximum(maxValue * 2);
                combinedChart.moveViewToX(entryCount);

                combinedChart.invalidate();

                if(customLodingDialog.isShowing() && customLodingDialog != null)
                    customLodingDialog.dismiss();
            }

        } catch (NegativeArraySizeException ex){
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 시작할 때 코인 정보 받아오기

    @Override
    public void onStart() {
        super.onStart();
        getRecentCoinChart = new GetRecentCoinChart(minute,period);
        getRecentCoinChart.start();
    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();

        if (getRecentCoinChart != null) {
            getRecentCoinChart.stopThread();
            getRecentCoinChart = null;
        }
    }

    //코인 차트 1초마다 계속해서 갱신
    public static CoinCandleDataDTO getCoinCandle(int position){

        return coinCandleDataDTOS.get(position);

    }

    private void setRg_chart(){

        rg_chart.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radio_minuteChart :
                        if(minute != 1){
                            minute = 1;
                            period = "minutes";
                            setBtn(minute,period);
                            rg_minuteGroup.setVisibility(View.VISIBLE);
                            radio_oneMinute.setChecked(true);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 1분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_dailyChart :
                        if(!period.equals("days")) {
                            minute = 2;
                            period = "days";
                            setBtn(minute, period);
                            rg_minuteGroup.setVisibility(View.INVISIBLE);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 일봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_weeklyChart :
                        if(!period.equals("weeks")) {
                            minute = 2;
                            period = "weeks";
                            setBtn(minute, period);
                            rg_minuteGroup.setVisibility(View.INVISIBLE);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 주봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_monthlyChart :
                        if(!period.equals("months")) {
                            minute = 2;
                            period = "months";
                            setBtn(minute, period);
                            rg_minuteGroup.setVisibility(View.INVISIBLE);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 월봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                }
            }
        });
    }

    public void setRg_minuteGroup() {
        rg_minuteGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (radioGroup.getCheckedRadioButtonId()){
                    case R.id.radio_oneMinute:
                        if(minute != 1){
                            minute = 1;
                            period = "minutes";
                            setBtn(minute,period);
                            break;
                        }else{
                            return;
                        }
                    case R.id.radio_threeMinute:
                        if(minute != 3){
                            minute = 3;
                            period = "minutes";
                            setBtn(minute,period);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 3분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_fiveMinute:
                        if(minute != 5){
                            minute = 5;
                            period = "minutes";
                            setBtn(minute,period);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 5분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_tenMinute:
                        if(minute != 10){
                            minute = 10;
                            period = "minutes";
                            setBtn(minute,period);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 10분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_fifteenMinute:
                        if(minute != 15){
                            minute = 15;
                            period = "minutes";
                            setBtn(minute,period);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 15분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_thirtyMinute:
                        if(minute != 30){
                            minute = 30;
                            period = "minutes";
                            setBtn(minute,period);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 30분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_hour:
                        if(minute != 60){
                            minute = 60;
                            period = "minutes";
                            setBtn(minute,period);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 60분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_fourHour:
                        if(minute != 240){
                            minute = 240;
                            period = "minutes";
                            setBtn(minute,period);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 240분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                }
            }
        });
    }

    private void setBtn(int minute, String period){
        if(getRecentCoinChart != null) {
            getRecentCoinChart.stopThread();
            getRecentCoinChart = null;
            getCoinCandleData(minute,period);
            getRecentCoinChart = new GetRecentCoinChart(minute,period);
            getRecentCoinChart.start();
        }
    }

    class GetRecentCoinChart extends Thread {

        private int minute;
        private String period;
        private boolean isRunning = true;

        public GetRecentCoinChart(int minute,String period) {
            this.minute = minute;
            this.period = period;
        }

        @Override
        public void run() {
            super.run();

            while (isRunning) {
                try {

                        String coinUrl = "";
                        if (minute != 0 && minute != 2) {
                            coinUrl = "https://api.upbit.com/v1/candles/" + period + "/" + minute + "?market=" + market + "&count=1";
                        } else if(minute == 2){
                            coinUrl = "https://api.upbit.com/v1/candles/" + period + "?market=" + market + "&count=1";
                        }

                        URL url = new URL(coinUrl);
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                        StringBuffer builder = new StringBuffer();

                        String inputString = null;
                        while ((inputString = bufferedReader.readLine()) != null) {
                            builder.append(inputString);
                        }

                        String s = builder.toString();
                        JSONArray jsonRecentCoinInfo = new JSONArray(s);

                        conn.disconnect();
                        bufferedReader.close();
                        inputStream.close();

                        try {

                            String candleDateTimeKst = "";
                            if (jsonRecentCoinInfo != null) {

                                JSONObject jsonObject = (JSONObject) jsonRecentCoinInfo.get(0);

                                candleDateTimeKst = jsonObject.getString("candle_date_time_kst");
                                Double openingPrice = jsonObject.getDouble("opening_price");
                                Double highPrice = jsonObject.getDouble("high_price");
                                Double lowPrice = jsonObject.getDouble("low_price");
                                Double tradePrice = jsonObject.getDouble("trade_price");
                                Double candleTransactionAmount = jsonObject.getDouble("candle_acc_trade_price");

                                float openingPrice2 = 0;

                                if (openingPrice < 100) {
                                    openingPrice2 = Float.parseFloat(String.format("%.2f", openingPrice));
                                } else {
                                    openingPrice2 = (float) ((float) round(openingPrice * 100) * 0.01);
                                }

                                float highPrice2 = Float.parseFloat(String.format("%.2f", highPrice));
                                float lowPrice2 = Float.parseFloat(String.format("%.2f", lowPrice));
                                float tradePrice2 = Float.parseFloat(String.format("%.2f", tradePrice));
                                float candleTransactionAmount2 = Float.parseFloat(String.valueOf(candleTransactionAmount));

                                int candleSize = candleEntries.size();

                                if (coinCandleDataDTOS.size() != 0 && candleEntries.size() != 0 && coinCandleDataDTOS.get(candleSize - 1).getCandleDateTimeKst().equals(candleDateTimeKst)) {
                                    candleEntries.set(candleSize - 1, new CandleEntry(candlePosition - 1, highPrice2, lowPrice2, openingPrice2, tradePrice2));

                                    barEntries.set(candleSize - 1, new BarEntry(candlePosition - 1, candleTransactionAmount2));

                                    coinCandleDataDTOS.set(candleSize - 1, new CoinCandleDataDTO(candleDateTimeKst, candleTransactionAmount));

                                } else if (coinCandleDataDTOS.size() != 0 && candleEntries.size() != 0 && !coinCandleDataDTOS.get(candleSize - 1).getCandleDateTimeKst().equals(candleDateTimeKst)) {

                                    candleEntries.add(new CandleEntry(candlePosition, highPrice2, lowPrice2, openingPrice2, tradePrice2));

                                    barEntries.add(new BarEntry(candlePosition, candleTransactionAmount2));

                                    coinCandleDataDTOS.add(new CoinCandleDataDTO(candleDateTimeKst, candleTransactionAmount));

                                    Collections.sort(candleEntries, new EntryXComparator());
                                    Collections.sort(barEntries, new EntryXComparator());

                                    candlePosition++;

                                    combinedChart.getXAxis().setAxisMaximum(combinedChart.getXChartMax() + 1f);
                                }

                                combinedChart.getAxisRight().removeLimitLine(ll2);
                                if (tradePrice2 >= 100) {
                                    ll2 = new LimitLine(tradePrice2, decimalFormat.format(tradePrice2));
                                } else {
                                    ll2 = new LimitLine(tradePrice2, String.format("%.2f", tradePrice2));
                                }
                                ll2.setLineWidth(0f);
                                ll2.enableDashedLine(1f, 1f, 0f);
                                ll2.setLineColor(Color.parseColor("#212121"));
                                ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
                                ll2.setTextSize(10f);
                                ll2.setTextColor(Color.parseColor("#FFFFFFFF"));
                                combinedChart.getAxisRight().addLimitLine(ll2);

                                combinedChart.notifyDataSetChanged();
                                combinedChart.invalidate();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }catch (NegativeArraySizeException ex){
                            ex.printStackTrace();
                            if (getRecentCoinChart != null) {
                                setBtn(minute,period);
                            }
                        }
                }catch (UnsupportedEncodingException e) {
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

        private void stopThread() {
            isRunning = false;
        }
    }

    //차트 XAxis value formatter

    public class myValueFormatter extends ValueFormatter{

        ArrayList<CoinCandleDataDTO> mValue;
        ArrayList<CandleEntry> entries;

        public myValueFormatter(ArrayList<CoinCandleDataDTO> mValue,ArrayList<CandleEntry> entries) {
            this.mValue = mValue;
            this.entries = entries;
        }

        @Override
        public String getFormattedValue(float value) {

            if((int)value <= 0 || (int)value >= entries.size()) {
                return "";
            }

            else if((int)value < entries.size()) {

                String[] fullyDate = mValue.get((int) value).getCandleDateTimeKst().split("T");
                String[] date = fullyDate[0].split("-");
                String[] time = fullyDate[1].split(":");

                return date[1] + "-" + date[2] + " " + time[0] + ":" + time[1];
            }
            return "";
        }
    }

}
