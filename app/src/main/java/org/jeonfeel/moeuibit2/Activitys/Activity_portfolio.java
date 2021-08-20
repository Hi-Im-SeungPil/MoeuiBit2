package org.jeonfeel.moeuibit2.Activitys;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.jeonfeel.moeuibit2.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.MyCoin;
import org.jeonfeel.moeuibit2.R;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class Activity_portfolio extends AppCompatActivity {

    private PieChart pieChart;
    private LinearLayout linear_pieChartContent;
    private Button btn_portfolioBackSpace;
    private MoEuiBitDatabase db;
    private List<MyCoin> myCoinList;
    private ArrayList<Integer> coinTotalPrice;
    private ArrayList<String> coinSymbol;
    private ArrayList<String> coinKoreanName;
    private int[] colors;
    int coinAmount = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portfolio);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        FindViewById();
        db = MoEuiBitDatabase.getInstance(Activity_portfolio.this);
        getMyCoins();
        setPieChart();
        insertTextView();

        btn_portfolioBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void FindViewById(){
        pieChart = findViewById(R.id.pieChart);
        linear_pieChartContent = findViewById(R.id.linear_pieChartContent);
        btn_portfolioBackSpace = findViewById(R.id.btn_portfolioBackSpace);
    }

    private void getMyCoins(){
        coinTotalPrice = new ArrayList<>();
        coinSymbol = new ArrayList<>();
        coinKoreanName = new ArrayList<>();

        myCoinList = db.myCoinDAO().getAll();

        for(int i = 0; i < myCoinList.size(); i++){

            int coinPrice = (int) round(myCoinList.get(i).getQuantity() * myCoinList.get(i).getPurchasePrice());

            coinTotalPrice.add(coinPrice);
            coinSymbol.add(myCoinList.get(i).getSymbol());
            coinKoreanName.add(myCoinList.get(i).getKoreanCoinName());
            coinAmount += coinPrice;
        }

    }

    private void setPieChart(){

        Resources pieChart_color = getResources();
        TypedArray color_Array = pieChart_color.obtainTypedArray(R.array.pieChart_color);

        colors = new int[coinTotalPrice.size()];

        for(int i = 0; i < coinTotalPrice.size(); i++){
            colors[i] = color_Array.getColor(i,0);
        }

        ArrayList<PieEntry> data = new ArrayList<>();

        for(int i = 0; i < coinTotalPrice.size(); i++) {
            data.add(new PieEntry(coinTotalPrice.get(i)));
        }

        PieDataSet pieDataSet = new PieDataSet(data,"");
        pieDataSet.setColors(colors);
        pieDataSet.setValueTextColor(Color.parseColor("#FFFFFFFF"));
        PieData pieData = new PieData(pieDataSet);
        pieChart.setDrawEntryLabels(true);
        pieChart.setUsePercentValues(true);

        pieData.setValueTextSize(15);
        pieChart.setCenterText("보유 현황 %");
        pieChart.setCenterTextSize(15);
        pieChart.getLegend().setEnabled(false);

        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    private void insertTextView(){

        for(int i = 0; i < coinTotalPrice.size(); i++) {
            String coinPercent = String.format("%.1f",(coinTotalPrice.get(i) / (float) coinAmount) * 100);
            TextView textView = new TextView(Activity_portfolio.this);
            textView.setText("★ " + coinKoreanName.get(i) +" ("+ coinSymbol.get(i)+") "+coinPercent+"%");
            textView.setTextColor(colors[i]);
            textView.setTextSize(17f);
            linear_pieChartContent.addView(textView);
        }
    }

}
