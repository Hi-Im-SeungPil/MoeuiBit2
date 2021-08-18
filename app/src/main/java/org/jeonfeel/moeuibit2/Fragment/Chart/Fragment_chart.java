package org.jeonfeel.moeuibit2.Fragment.Chart;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.EntryXComparator;

import org.jeonfeel.moeuibit2.DTOS.CoinCandleDataDTO;
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

import static java.lang.Math.round;

public class Fragment_chart extends Fragment {

    private CombinedChart combinedChart;
    private Button btn_minuteChart, btn_dailyChart, btn_weeklyChart, btn_monthlyChart;
    private Button btn_oneMinute, btn_threeMinute,btn_fiveMinute, btn_tenMinute, btn_fifteenMinute, btn_thirtyMinute, btn_hour, btn_fourHour;
    private LinearLayout linear_minuteGroup;
    private String market;
    private static ArrayList<CoinCandleDataDTO> coinCandleDataDTOS;
    private ArrayList<CandleEntry> candleEntries;
    private int candlePosition = 0;
    private CandleData d;
    private CandleDataSet candleDataSet;
    private boolean checkTimer = false;
    private CombinedData data;
    private int checkStart = 0, checkStart2 = 0;
    private int btn_minuteSelected = 1;
    private String period="";
    private myValueFormatter myValueFormatter;

    private GetRecentCoinChart getRecentCoinChart;

    private RadioGroup rg_chart;

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

        FindViewById(rootView);
        getCoinCandleData(1,"minutes");
        setRg_chart();
        btn_minuteChart.setOnClickListener(new SetBtn_minutes());
        btn_oneMinute.setOnClickListener(new SetBtn_minutes());
        btn_threeMinute.setOnClickListener(new SetBtn_minutes());
        btn_fiveMinute.setOnClickListener(new SetBtn_minutes());
        btn_tenMinute.setOnClickListener(new SetBtn_minutes());
        btn_fifteenMinute.setOnClickListener(new SetBtn_minutes());
        btn_thirtyMinute.setOnClickListener(new SetBtn_minutes());
        btn_hour.setOnClickListener(new SetBtn_minutes());
        btn_fourHour.setOnClickListener(new SetBtn_minutes());
        btn_dailyChart.setOnClickListener(new SetBtn_minutes());
        btn_weeklyChart.setOnClickListener(new SetBtn_minutes());
        btn_monthlyChart.setOnClickListener(new SetBtn_minutes());

        return rootView;

    }

    private void FindViewById(View rootView) {

        combinedChart = rootView.findViewById(R.id.combinedChart);
        btn_minuteChart = rootView.findViewById(R.id.btn_minuteChart);
        btn_dailyChart = rootView.findViewById(R.id.btn_dailyChart);
        btn_weeklyChart = rootView.findViewById(R.id.btn_weeklyChart);
        btn_monthlyChart = rootView.findViewById(R.id.btn_monthlyChart);
        btn_oneMinute = rootView.findViewById(R.id.btn_oneMinute);
        btn_threeMinute = rootView.findViewById(R.id.btn_threeMinute);
        btn_fiveMinute = rootView.findViewById(R.id.btn_fiveMinute);
        btn_tenMinute = rootView.findViewById(R.id.btn_tenMinute);
        btn_fifteenMinute = rootView.findViewById(R.id.btn_fifteenMinute);
        btn_thirtyMinute = rootView.findViewById(R.id.btn_thirtyMinute);
        btn_hour = rootView.findViewById(R.id.btn_hour);
        btn_fourHour = rootView.findViewById(R.id.btn_fourHour);
        linear_minuteGroup = rootView.findViewById(R.id.linear_minuteGroup);
        rg_chart = rootView.findViewById(R.id.rg_chart);

    }

    // 차트 초기화

    private void initChart() {

        MoEuiBitMarkerView moEuiBitMarkerView = new MoEuiBitMarkerView(getActivity(),R.layout.candle_info_marker);

        combinedChart.getDescription().setEnabled(false);
        combinedChart.setScaleYEnabled(false);
        combinedChart.setPinchZoom(false);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBorders(false);
        combinedChart.setDoubleTapToZoomEnabled(false);
        combinedChart.setHighlightFullBarEnabled(false);
        combinedChart.setMarker(moEuiBitMarkerView);

        combinedChart.setDragDecelerationEnabled(false);
        combinedChart.setDragEnabled(true);
        combinedChart.setHighlightPerDragEnabled(true);
        combinedChart.setHighlightPerTapEnabled(false);

        combinedChart.setOnChartGestureListener(new OnChartGestureListener() {

            //터치 하면 하이라이트 생성
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
                Highlight highlight = combinedChart.getHighlightByTouchPoint(me.getX(),me.getY());
                if(highlight != null) {
                    combinedChart.highlightValue(highlight, true);
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
        xAxis.setTextColor(Color.BLACK);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setLabelCount(3,true);
        xAxis.setGranularity(3f);
        xAxis.setGranularityEnabled(true);

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setLabelCount(5, true);
        rightAxis.setTextColor(Color.BLACK);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisLineColor(Color.parseColor("#323B4C"));
        rightAxis.setMinWidth(40f);

        Legend l = combinedChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
    }

    //코인 정보 200개 받아오는 메소드

    private void getCoinCandleData(int minute, String period) {

        if(myValueFormatter != null){
            myValueFormatter = null;
        }

        if (d != null) {
            d.clearValues();
            d = null;
        }
        if (data != null) {
            data.clearValues();
            data = null;
        }
        if (candleDataSet != null) {
            candleDataSet.clear();
            candleDataSet = null;
        }
        if (combinedChart != null) {
            combinedChart.clear();
        }
        initChart();

        candlePosition = 0;
        String coinUrl = "";
        if(minute == 0) {
            coinUrl = "https://api.upbit.com/v1/candles/" + period + "?market=" + market + "&count=200";
        }else{
            coinUrl = "https://api.upbit.com/v1/candles/" + period + "/" + minute + "?market=" + market + "&count=200";
        }
        if (coinCandleDataDTOS == null)
            coinCandleDataDTOS = new ArrayList<>();

        if (candleEntries == null)
            candleEntries = new ArrayList<>();

        if (coinCandleDataDTOS.size() != 0)
            coinCandleDataDTOS.clear();

        if (candleEntries.size() != 0)
            candleEntries.clear();


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
                    Double candleTransactionVolume = jsonObject.getDouble("candle_acc_trade_volume");

                    float openingPrice2 = 0;

                    if (openingPrice < 100) {
                        openingPrice2 = Float.parseFloat(String.format("%.2f", openingPrice));
                    } else {
                        openingPrice2 = (float) ((float) round(openingPrice * 100) * 0.01);
                    }

                    float highPrice2 = Float.parseFloat(String.format("%.2f", highPrice));
                    float lowPrice2 = Float.parseFloat(String.format("%.2f", lowPrice));
                    float tradePrice2 = Float.parseFloat(String.format("%.2f", tradePrice));

                    coinCandleDataDTOS.add(new CoinCandleDataDTO(candleDateTimeKst, openingPrice, highPrice, lowPrice, tradePrice, candleTransactionAmount, candleTransactionVolume));

                    candleEntries.add(new CandleEntry(candlePosition + 2f, highPrice2, lowPrice2, openingPrice2, tradePrice2));
                    candlePosition++;
                }

                Collections.sort(candleEntries, new EntryXComparator());

                d = new CandleData();

                candleDataSet = new CandleDataSet(candleEntries, "");
                candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                candleDataSet.setShadowColor(Color.DKGRAY);
                candleDataSet.setShadowWidth(1f);

                candleDataSet.setDecreasingColor(Color.BLUE);
                candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);

                candleDataSet.setIncreasingColor(Color.RED);
                candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);

                candleDataSet.setNeutralColor(Color.DKGRAY);
                candleDataSet.setDrawValues(false);
                candleDataSet.disableDashedHighlightLine();

                d.addDataSet(candleDataSet);

                data = new CombinedData();
                data.setData(d);

                if(candleEntries.isEmpty()){
                    combinedChart.clear();
                }else {
                    combinedChart.setData(data);
                }

                combinedChart.fitScreen();
                combinedChart.setAutoScaleMinMaxEnabled(true);
                int entryCount = combinedChart.getCandleData().getEntryCount();

                combinedChart.getXAxis().setAxisMinimum(combinedChart.getXChartMin()-0.7f);
                combinedChart.getXAxis().setAxisMaximum(entryCount + 3f);
                if(combinedChart.getVisibleXRange() > 20f){
                    combinedChart.zoom(4f,0f,0,0);
                }
                combinedChart.moveViewToX(entryCount);

                myValueFormatter = new myValueFormatter(coinCandleDataDTOS,candleEntries);
                combinedChart.getXAxis().setValueFormatter(myValueFormatter);

                combinedChart.invalidate();
                Log.d("qqqq",combinedChart.getCandleData().getEntryCount()+"");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 시작할 때 코인 정보 받아오기

    @Override
    public void onStart() {
        super.onStart();
        getRecentCoinChart = new GetRecentCoinChart(1,"minutes");
        getRecentCoinChart.start();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();

        if (getRecentCoinChart != null) {
            getRecentCoinChart.stopThread();
            getRecentCoinChart = null;
        }
    }

    //코인 차트 0.5초마다 계속해서 갱신

    public static String getCoinCandle(int position){

        return coinCandleDataDTOS.get(position).getCandleDateTimeKst();

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
                    if(minute != 0) {
                        coinUrl = "https://api.upbit.com/v1/candles/" + period + "/" + minute + "?market=" + market + "&count=1";
                    }else{
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
                            Double candleTransactionVolume = jsonObject.getDouble("candle_acc_trade_volume");

                            float openingPrice2 = 0;

                            if (openingPrice < 100) {
                                openingPrice2 = Float.parseFloat(String.format("%.2f", openingPrice));
                            } else {
                                openingPrice2 = (float) ((float) round(openingPrice * 100) * 0.01);
                            }

                            float highPrice2 = Float.parseFloat(String.format("%.2f", highPrice));
                            float lowPrice2 = Float.parseFloat(String.format("%.2f", lowPrice));
                            float tradePrice2 = Float.parseFloat(String.format("%.2f", tradePrice));

                            if (coinCandleDataDTOS.size() != 0 && candleEntries.size() != 0 && coinCandleDataDTOS.get(candleEntries.size() - 1).getCandleDateTimeKst().equals(candleDateTimeKst)) {
                                candleEntries.set(candleEntries.size() - 1, new CandleEntry(candlePosition - 1 + 2f, highPrice2, lowPrice2, openingPrice2, tradePrice2));
                                Collections.sort(candleEntries, new EntryXComparator());
                                coinCandleDataDTOS.set(candleEntries.size() - 1, new CoinCandleDataDTO(candleDateTimeKst, openingPrice, highPrice, lowPrice, tradePrice, candleTransactionAmount, candleTransactionVolume));

                            } else if (coinCandleDataDTOS.size() != 0 && candleEntries.size() != 0 && !coinCandleDataDTOS.get(candleEntries.size() - 1).getCandleDateTimeKst().equals(candleDateTimeKst)) {
                                candleEntries.add(new CandleEntry(candlePosition + 2f, highPrice2, lowPrice2, openingPrice2, tradePrice2));
                                coinCandleDataDTOS.add(new CoinCandleDataDTO(candleDateTimeKst, openingPrice, highPrice, lowPrice, tradePrice, candleTransactionAmount, candleTransactionVolume));
                                Collections.sort(candleEntries, new EntryXComparator());
                                candlePosition++;
                                combinedChart.getXAxis().setAxisMaximum(combinedChart.getXChartMax() + 1f);
                            }
                            combinedChart.notifyDataSetChanged();
                            combinedChart.invalidate();
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
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void stopThread() {
            isRunning = false;
        }
    }


    private void setRg_chart(){

        rg_chart.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                switch (radioGroup.getId()){
                    case R.id.radio_minuteChart :
                        if(btn_minuteSelected != 1){
                            btn_minuteSelected = 1;
                            period = "minutes";
                            setBtn(btn_minuteSelected,period);
                            linear_minuteGroup.setVisibility(View.VISIBLE);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 1분봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_dailyChart :
                        if(!period.equals("days")) {
                            btn_minuteSelected = 2;
                            period = "days";
                            setBtn(0, period);
                            linear_minuteGroup.setVisibility(View.INVISIBLE);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 일봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_weeklyChart :
                        if(!period.equals("weeks")) {
                            btn_minuteSelected = 2;
                            period = "weeks";
                            setBtn(0, period);
                            linear_minuteGroup.setVisibility(View.INVISIBLE);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 주봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    case R.id.radio_monthlyChart :
                        if(!period.equals("months")) {
                            btn_minuteSelected = 2;
                            period = "months";
                            setBtn(0, period);
                            linear_minuteGroup.setVisibility(View.INVISIBLE);
                            break;
                        }else{
                            Toast.makeText(getActivity(), "현재 월봉 입니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                }
            }
        });
    }

    private void setBtn(int btn_minuteSelected,String period){
        getRecentCoinChart.stopThread();
        getRecentCoinChart = null;
        getCoinCandleData(btn_minuteSelected,period);
        getRecentCoinChart = new GetRecentCoinChart(btn_minuteSelected,period);
        getRecentCoinChart.start();
    }

    //차트 버튼 클릭 리스터

    public class SetBtn_minutes implements View.OnClickListener {

        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.btn_minuteChart :
                case R.id.btn_oneMinute :
                    if(btn_minuteSelected != 1){
                        btn_minuteSelected = 1;
                        period = "minutes";
                        setBtn(btn_minuteSelected,period);
                        linear_minuteGroup.setVisibility(View.VISIBLE);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 1분봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_threeMinute :
                    if(btn_minuteSelected != 3){
                        btn_minuteSelected = 3;
                        period = "minutes";
                        setBtn(btn_minuteSelected,period);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 3분봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_fiveMinute :
                    if(btn_minuteSelected != 5){
                        btn_minuteSelected = 5;
                        period = "minutes";
                        setBtn(btn_minuteSelected,period);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 5분봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_tenMinute :
                    if(btn_minuteSelected != 10){
                        btn_minuteSelected = 10;
                        period = "minutes";
                        setBtn(btn_minuteSelected,period);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 10분봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_fifteenMinute :
                    if(btn_minuteSelected != 15){
                        btn_minuteSelected = 15;
                        period = "minutes";
                        setBtn(btn_minuteSelected,period);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 15분봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_thirtyMinute :
                    if(btn_minuteSelected != 30){
                        btn_minuteSelected = 30;
                        period = "minutes";
                        setBtn(btn_minuteSelected,period);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 30분봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_hour :
                    if(btn_minuteSelected != 60){
                        btn_minuteSelected = 60;
                        period = "minutes";
                        setBtn(btn_minuteSelected,period);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 60분봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_fourHour :
                    if(btn_minuteSelected != 240){
                        btn_minuteSelected = 240;
                        period = "minutes";
                        setBtn(btn_minuteSelected,period);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 240분봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_dailyChart :
                    if(!period.equals("days")) {
                        btn_minuteSelected = 2;
                        period = "days";
                        setBtn(0, period);
                        linear_minuteGroup.setVisibility(View.INVISIBLE);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 일봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_weeklyChart :
                    if(!period.equals("weeks")) {
                        btn_minuteSelected = 2;
                        period = "weeks";
                        setBtn(0, period);
                        linear_minuteGroup.setVisibility(View.INVISIBLE);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 주봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                case R.id.btn_monthlyChart :
                    if(!period.equals("months")) {
                        btn_minuteSelected = 2;
                        period = "months";
                        setBtn(0, period);
                        linear_minuteGroup.setVisibility(View.INVISIBLE);
                        break;
                    }else{
                        Toast.makeText(getActivity(), "현재 월봉 입니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }
            }
        }
    }

    //차트 XAxis value formatter

    public class myValueFormatter extends ValueFormatter{

        ArrayList<CoinCandleDataDTO> mValue;
        ArrayList<CandleEntry> entries;
        private int mValueCount = 0;

        public myValueFormatter(ArrayList<CoinCandleDataDTO> mValue,ArrayList<CandleEntry> entries) {
            this.mValue = mValue;
            this.entries = entries;
            mValueCount = entries.size();
        }

        @Override
        public String getFormattedValue(float value) {

            if((int)value <= 0 || (int)value >= entries.size()) {
                return "";
            }

            else if((int)value < entries.size()) {

                String[] fullyDate = mValue.get((int) value - 1).getCandleDateTimeKst().split("T");
                String[] date = fullyDate[0].split("-");
                String[] time = fullyDate[1].split(":");

                return date[1] + "-" + date[2] + " " + time[0] + ":" + time[1];
            }
            return "";
        }
    }

}
