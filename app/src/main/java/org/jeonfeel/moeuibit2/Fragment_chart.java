package org.jeonfeel.moeuibit2;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.github.mikephil.charting.utils.ViewPortHandler;

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
import java.util.Timer;
import java.util.TimerTask;

import static com.github.mikephil.charting.charts.CombinedChart.DrawOrder.CANDLE;
import static java.lang.Math.round;

public class Fragment_chart extends Fragment {

    private CombinedChart combinedChart;
    private BarChart barChart;
    private ToggleButton tog_minuteChart, tog_dailyChart, tog_weeklyChart, tog_monthlyChart;
    private ToggleButton tog_fiveMinute, tog_tenMinute, tog_fifteenMinute, tog_thirtyMinute, tog_hour, tog_fourHour;
    Button tog_oneMinute, tog_threeMinute;
    private String market;
    private ArrayList<CoinCandleDataDTO> coinCandleDataDTOS;
    private ArrayList<CandleEntry> candleEntries;
    private int candlePosition = 0;
    private CandleData d;
    private CandleDataSet candleDataSet;
    private TimerTask timerTask;
    private boolean checkTimer = false;
    private CombinedData data;
    private int checkStart = 0, checkStart2 = 0;

    private GetRecentCoinChart getRecentCoinChart;


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
        getCoinMinuteCandleData(1);
        tog_oneMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTog_oneMinute();
            }
        });
        tog_threeMinute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTog_threeMinute();
            }
        });

        return rootView;

    }

    private void FindViewById(View rootView) {

        combinedChart = rootView.findViewById(R.id.combinedChart);
        tog_minuteChart = rootView.findViewById(R.id.tog_minuteChart);
        tog_dailyChart = rootView.findViewById(R.id.tog_dailyChart);
        tog_weeklyChart = rootView.findViewById(R.id.tog_weeklyChart);
        tog_monthlyChart = rootView.findViewById(R.id.tog_monthlyChart);
        tog_oneMinute = rootView.findViewById(R.id.tog_oneMinute);
        tog_threeMinute = rootView.findViewById(R.id.tog_threeMinute);
        tog_fiveMinute = rootView.findViewById(R.id.tog_fiveMinute);
        tog_tenMinute = rootView.findViewById(R.id.tog_tenMinute);
        tog_fifteenMinute = rootView.findViewById(R.id.tog_fifteenMinute);
        tog_thirtyMinute = rootView.findViewById(R.id.tog_thirtyMinute);
        tog_hour = rootView.findViewById(R.id.tog_hour);
        tog_fourHour = rootView.findViewById(R.id.tog_fourHour);

    }

    private void initChart() {

        combinedChart.getDescription().setEnabled(false);
        combinedChart.setScaleYEnabled(false);
        combinedChart.setMaxVisibleValueCount(200);
        combinedChart.setPinchZoom(false);
        combinedChart.setDrawGridBackground(false);
        combinedChart.setDrawBorders(true);
        combinedChart.setBorderColor(Color.BLACK);
        combinedChart.requestDisallowInterceptTouchEvent(true);
        combinedChart.setDoubleTapToZoomEnabled(false);

        combinedChart.setDragYEnabled(true);
        combinedChart.setDragEnabled(true);
        combinedChart.setHighlightPerTapEnabled(true);
        combinedChart.setHighlightPerDragEnabled(true);

        combinedChart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            }

            @Override
            public void onChartLongPressed(MotionEvent me) {

                Highlight highlight = combinedChart.getHighlightByTouchPoint(me.getX(),me.getY());

                if(highlight != null){
                    combinedChart.highlightValue(highlight,true);
                }
                

                if(me.getAction() == MotionEvent.ACTION_UP){
                    combinedChart.setDragEnabled(true);
                    combinedChart.setScaleEnabled(true);
                }
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
        xAxis.setAxisLineColor(Color.parseColor("#323B4C"));
        xAxis.setAvoidFirstLastClipping(true);

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawLabels(false);
        leftAxis.setMaxWidth(1f);
        leftAxis.setMinWidth(0f);

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

    private void getCoinMinuteCandleData(int minute) {

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
        combinedChart.invalidate();
        initChart();

        if (d != null) {
            Log.d("entry1", d.getEntryCount() + "");
            Log.d("entry2", data.getEntryCount() + "");
            Log.d("entry3", candleDataSet.getEntryCount() + "");
        }
        candlePosition = 0;

        String coinUrl = "https://api.upbit.com/v1/candles/minutes/" + minute + "?market=" + market + "&count=200";

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

                d.addDataSet(candleDataSet);

                data = new CombinedData();
                data.setData(d);

                combinedChart.setData(data);
                combinedChart.setVisibleXRangeMinimum(20);
                combinedChart.setVisibleXRangeMaximum(200);
                combinedChart.fitScreen();
                combinedChart.setAutoScaleMinMaxEnabled(true);

                combinedChart.zoom(4f, 1f, 0, 0);
                combinedChart.moveViewToX(combinedChart.getXChartMax());

                if (checkStart == 0) {
                    combinedChart.getXAxis().setAxisMinimum(combinedChart.getXChartMin() - 0.5f);
                    combinedChart.getXAxis().setAxisMaximum(combinedChart.getXChartMax() + 8f);
                    checkStart++;
                }
                combinedChart.invalidate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getRecentCoinChart = new GetRecentCoinChart(1);
        getRecentCoinChart.start();
    }

    @Override
    public void onResume() { //사용자와 상호작용 하고 있을 때  1초마다 api 받아옴
        super.onResume();
    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();

        if (getRecentCoinChart != null) {
            getRecentCoinChart.stopThread();
        }
    }

    class GetRecentCoinChart extends Thread {

        private int minute;
        private boolean isRunning = true;

        public GetRecentCoinChart(int minute) {
            this.minute = minute;
        }

        @Override
        public void run() {
            super.run();

            while (isRunning) {
                try {
                    String coinUrl = "https://api.upbit.com/v1/candles/minutes/" + minute + "?market=" + market + "&count=1";
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
                                coinCandleDataDTOS.set(candleEntries.size() - 1, new CoinCandleDataDTO(candleDateTimeKst, openingPrice, highPrice, lowPrice, tradePrice, candleTransactionAmount, candleTransactionVolume));

                            } else if (coinCandleDataDTOS.size() != 0 && candleEntries.size() != 0 && !coinCandleDataDTOS.get(candleEntries.size() - 1).getCandleDateTimeKst().equals(candleDateTimeKst)) {
                                candleEntries.add(new CandleEntry(candlePosition + 2f, highPrice2, lowPrice2, openingPrice2, tradePrice2));
                                coinCandleDataDTOS.add(new CoinCandleDataDTO(candleDateTimeKst, openingPrice, highPrice, lowPrice, tradePrice, candleTransactionAmount, candleTransactionVolume));
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

    public void setTog_oneMinute() {
        getRecentCoinChart.stopThread();
        getCoinMinuteCandleData(1);
        getRecentCoinChart = new GetRecentCoinChart(1);
        getRecentCoinChart.start();

    }

    public void setTog_threeMinute() {

        getRecentCoinChart.stopThread();
        getCoinMinuteCandleData(3);
        getRecentCoinChart = new GetRecentCoinChart(3);
        getRecentCoinChart.start();
    }

    private void performHighlightDrag(MotionEvent e) {
        Highlight h = combinedChart.getHighlightByTouchPoint(e.getX(), e.getY());
        if (h != null) {
            combinedChart.highlightValue(h, true);
        }
    }
}
