package org.jeonfeel.moeuibit2;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.lang.Math.round;

public class Fragment_chart extends Fragment {

    CombinedChart combinedChart;
    BarChart barChart;
    ToggleButton tog_minuteChart,tog_dailyChart,tog_weeklyChart,tog_monthlyChart;
    ToggleButton tog_oneMinute,tog_threeMinute,tog_fiveMinute,tog_tenMinute,tog_fifteenMinute,tog_thirtyMinute,tog_hour,tog_fourHour;
    String market;
    ArrayList<CoinCandleDataDTO> coinCandleDataDTOS = new ArrayList<>();
    ArrayList<CandleEntry> candleEntries = new ArrayList<>();


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
        initChart();
        getCoinMinuteCandleData();

        return rootView;

    }

    private void FindViewById(View rootView){

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

    private void initChart(){

        combinedChart.getDescription().setEnabled(false);
        combinedChart.setMaxVisibleValueCount(200);
        combinedChart.setPinchZoom(false);
        combinedChart.setDrawGridBackground(false);

        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(true);
        xAxis.setAxisLineColor(Color.parseColor("#323B4C"));
        xAxis.setGridColor(Color.parseColor("#323B4C"));

        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.isEnabled();

        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setLabelCount(7, false);
        rightAxis.setTextColor(Color.BLACK);
        rightAxis.setDrawGridLines(true);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setAxisLineColor(Color.parseColor("#323B4C"));
        rightAxis.setGridColor(Color.parseColor("#323B4C"));

        Legend l = combinedChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(true);
    }

    private void getCoinMinuteCandleData(){

            String coinUrl = "https://api.upbit.com/v1/candles/minutes/1?market="+market+"&count=200";

            GetUpBitCoins getUpBitCoins = new GetUpBitCoins();

            try {
                JSONArray jsonArray = new JSONArray();
                jsonArray = getUpBitCoins.execute(coinUrl).get();

                if (jsonArray != null) {
                    JSONObject jsonObject = new JSONObject();

                    for(int i = 0; i < jsonArray.length(); i++) {
                        jsonObject = (JSONObject) jsonArray.get(i);

                        String candleDateTimeKst = jsonObject.getString("candle_date_time_kst");
                        Double openingPrice  = jsonObject.getDouble("opening_price");
                        Double highPrice = jsonObject.getDouble("high_price");
                        Double lowPrice = jsonObject.getDouble("low_price");
                        Double tradePrice = jsonObject.getDouble("trade_price");
                        Double candleTransactionAmount = jsonObject.getDouble("candle_acc_trade_price");
                        Double candleTransactionVolume = jsonObject.getDouble("candle_acc_trade_volume");

                        float openingPrice2 = 0;

                        if(openingPrice< 100) {
                            openingPrice2 = Float.parseFloat(String.format("%.2f", openingPrice));
                        }else{
                            openingPrice2 = (float) ((float) round(openingPrice * 100) * 0.01);
                        }

                        float highPrice2 = round(highPrice*100) / 100;
                        float lowPrice2 = round(lowPrice*100) / 100;
                        float tradePrice2 = round(tradePrice*100) / 100;

                        coinCandleDataDTOS.add(new CoinCandleDataDTO(candleDateTimeKst,openingPrice,highPrice,lowPrice,tradePrice,candleTransactionAmount,candleTransactionVolume));
                        CandleEntry candleEntry = new CandleEntry(i+1f,highPrice2,lowPrice2,openingPrice2,tradePrice2);
                        candleEntries.add(candleEntry);
                    }

                    CandleDataSet candleDataSet = new CandleDataSet(candleEntries,"Candle dataSet");
                    candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

                    candleDataSet.setShadowColor(Color.LTGRAY);
                    candleDataSet.setShadowWidth(0.7f);

                    candleDataSet.setDecreasingColor(Color.BLUE);
                    candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);

                    candleDataSet.setIncreasingColor(Color.RED);
                    candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);

                    candleDataSet.setNeutralColor(Color.BLACK);
                    candleDataSet.setDrawValues(false);

                    CandleData d = new CandleData();
                    d.addDataSet(candleDataSet);

                    CombinedData data = new CombinedData();
                    data.setData(d);
                    data.setValueFormatter(new myViewFormatter());

                    combinedChart.setData(data);
                    combinedChart.invalidate();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
    }


    public class myViewFormatter extends ValueFormatter{

        private DecimalFormat decimalFormat;

        public myViewFormatter(){
            this.decimalFormat = new DecimalFormat("###,###,##0.00");
        }

    }
}
