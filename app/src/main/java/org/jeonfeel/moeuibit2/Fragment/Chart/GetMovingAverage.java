package org.jeonfeel.moeuibit2.Fragment.Chart;

import android.graphics.Color;
import android.util.Log;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class GetMovingAverage {

    private ArrayList<CandleEntry> candleEntries;
    private int count = 0;
    private float sumLine1;
    private float sumLine2;
    private float sumLine3;
    private ArrayList<Entry> line1Entries= new ArrayList<>();
    private ArrayList<Entry> line2Entries= new ArrayList<>();
    private ArrayList<Entry> line3Entries= new ArrayList<>();

    public GetMovingAverage(ArrayList<CandleEntry> candleEntries) {
        this.candleEntries = candleEntries;
    }

    public LineData createLineData(){

        LineData lineData = new LineData();

        int Line1 = 5;
        int Line2 = 20;
        int Line3 = 60;

        line1Entries= new ArrayList<>();
        line2Entries= new ArrayList<>();
        line3Entries= new ArrayList<>();

        sumLine1 = 0.0f;
        sumLine2 = 0.0f;
        sumLine3 = 0.0f;

        for(int i = 0; i < candleEntries.size(); i++){

            count++;

            sumLine1 += candleEntries.get(i).getClose();
            sumLine2 += candleEntries.get(i).getClose();
            sumLine3 += candleEntries.get(i).getClose();

            if(count >= Line1){
                line1Entries.add(new Entry(count-1,sumLine1/Line1));
                sumLine1 -= candleEntries.get(count-1 - (Line1 - 1)).getClose();
            }

            if(count >= Line2){
                line2Entries.add(new Entry(count-1,sumLine2/Line2));
                sumLine2 -= candleEntries.get(count-1 - (Line2 - 1)).getClose();
            }

            if(count >= Line3){
                line3Entries.add(new Entry(count-1,sumLine3/Line3));
                sumLine3 -= candleEntries.get(count-1 - (Line3 - 1)).getClose();
            }

        }

        LineDataSet lineDataSet1 = new LineDataSet(line1Entries,"");
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setColor(Color.GREEN);
        lineDataSet1.setLineWidth(1f);
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet1.setHighlightEnabled(false);
        lineDataSet1.setValueTextSize(0f);

        LineDataSet lineDataSet2 = new LineDataSet(line2Entries,"");
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setColor(Color.parseColor("#00D8FF"));
        lineDataSet2.setLineWidth(1f);
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet2.setHighlightEnabled(false);
        lineDataSet2.setValueTextSize(0f);

        LineDataSet lineDataSet3 = new LineDataSet(line3Entries,"");
        lineDataSet3.setDrawCircles(false);
        lineDataSet3.setColor(Color.RED);
        lineDataSet3.setLineWidth(1f);
        lineDataSet3.setAxisDependency(YAxis.AxisDependency.RIGHT);
        lineDataSet3.setHighlightEnabled(false);
        lineDataSet3.setValueTextSize(0f);

        lineData.addDataSet(lineDataSet1);
        lineData.addDataSet(lineDataSet2);
        lineData.addDataSet(lineDataSet3);

        return lineData;
    }

    public float getSumLine1(){
        return sumLine1;
    }

    public  float getSumLine2(){
        return sumLine2;
    }

    public float getSumLine3(){
        return sumLine3;
    }
}
