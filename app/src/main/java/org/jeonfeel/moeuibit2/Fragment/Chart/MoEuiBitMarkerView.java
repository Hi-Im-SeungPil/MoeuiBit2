package org.jeonfeel.moeuibit2.Fragment.Chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import org.jeonfeel.moeuibit2.DTOS.CoinCandleDataDTO;
import org.jeonfeel.moeuibit2.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.MyCoin;
import org.jeonfeel.moeuibit2.R;

import java.text.DecimalFormat;

import static java.lang.Math.round;

public class MoEuiBitMarkerView extends MarkerView {

    TextView tv_candleTime,tv_startPrice,tv_highPrice,
            tv_lowPrice,tv_endPrice,tv_coefficientOfPrice,
            tv_coefficientOfFluctuation,tv_markerTransactionAmount;

    String kst;
    String[] fullyDate,date,time;

    CombinedChart combinedChart;

    CoinCandleDataDTO coinCandleDataDTO;

    private DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public MoEuiBitMarkerView(Context context, int layoutResource,CombinedChart combinedChart) {
        super(context, layoutResource);
        this.combinedChart = combinedChart;
        tv_candleTime = findViewById(R.id.tv_candleTime);
        tv_startPrice = findViewById(R.id.tv_startPrice);
        tv_highPrice = findViewById(R.id.tv_highPrice);
        tv_lowPrice = findViewById(R.id.tv_lowPrice);
        tv_endPrice = findViewById(R.id.tv_endPrice);
        tv_coefficientOfPrice = findViewById(R.id.tv_coefficientOfPrice);
        tv_coefficientOfFluctuation = findViewById(R.id.tv_coefficientOfFluctuation);
        tv_markerTransactionAmount = findViewById(R.id.tv_markerTransactionAmount);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if(e instanceof CandleEntry){
            CandleEntry ce = (CandleEntry) e;

            coinCandleDataDTO = Fragment_chart.getCoinCandle((int) ce.getX() - 2);

            if(coinCandleDataDTO != null) {
                kst = coinCandleDataDTO.getCandleDateTimeKst();

                fullyDate = kst.split("T");
                date = fullyDate[0].split("-");
                time = fullyDate[1].split(":");
            }

            float openPrice = ce.getOpen();
            float highPrice = ce.getHigh();
            float lowPrice = ce.getLow();
            float closePrice = ce.getClose();
            float coefficientOfPrice = closePrice - openPrice;
            float coefficientOfFluctuation = (coefficientOfPrice / openPrice) * 100;

            if(coefficientOfPrice > 0){
                tv_coefficientOfPrice.setTextColor(Color.parseColor("#B77300"));
                tv_coefficientOfFluctuation.setTextColor(Color.parseColor("#B77300"));
            }else if(coefficientOfPrice < 0){
                tv_coefficientOfPrice.setTextColor(Color.parseColor("#0054FF"));
                tv_coefficientOfFluctuation.setTextColor(Color.parseColor("#0054FF"));
            }else{
                tv_coefficientOfPrice.setTextColor(Color.parseColor("#000000"));
                tv_coefficientOfFluctuation.setTextColor(Color.parseColor("#000000"));
            }

            tv_candleTime.setText(date[1] + "-" + date[2] + " " + time[0] + ":" + time[1]);

            if(openPrice >= 100|| openPrice <= -100){
                tv_startPrice.setText(decimalFormat.format(round(openPrice)));
            }else{
                tv_startPrice.setText(String.format(String.format("%.2f",openPrice)));
            }

            if(highPrice >= 100|| highPrice <= -100){
                tv_highPrice.setText(decimalFormat.format(round(highPrice)));
            }else{
                tv_highPrice.setText(String.format(String.format("%.2f",highPrice)));
            }

            if(lowPrice >= 100|| lowPrice <= -100){
                tv_lowPrice.setText(decimalFormat.format(round(lowPrice)));
            }else{
                tv_lowPrice.setText(String.format(String.format("%.2f",lowPrice)));
            }

            if(closePrice >= 100 || closePrice <= -100){
                tv_endPrice.setText(decimalFormat.format(round(closePrice)));
            }else{
                tv_endPrice.setText(String.format(String.format("%.2f",closePrice)));
            }

            if(coefficientOfPrice >= 100 || coefficientOfPrice <= -100){
                tv_coefficientOfPrice.setText(decimalFormat.format(round(coefficientOfPrice)));
            }else{
                tv_coefficientOfPrice.setText(String.format(String.format("%.2f",coefficientOfPrice)));
            }

            tv_coefficientOfFluctuation.setText(String.format("%.2f",coefficientOfFluctuation)+"%");
            if(coinCandleDataDTO != null) {
                Double transactionAmount = coinCandleDataDTO.getCandleTransactionAmount() * 0.000001;
                if(transactionAmount >= 1)
                tv_markerTransactionAmount.setText(decimalFormat.format(round(transactionAmount)));
                else if(transactionAmount < 1){
                    tv_markerTransactionAmount.setText(String.format("%.2f",transactionAmount));
                }
            }
        }
        super.refreshContent(e,highlight);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(35f);

        Paint paint2 = new Paint();
        paint2.setColor(Color.parseColor("#F361A6"));

        Paint paint3 = new Paint();
        paint3.setColor(Color.BLACK);
        paint3.setTextSize(35f);

        float textSize = paint.getTextSize();
        float length = paint.measureText("08-20 13:18") + 10f;

        Canvas canvas2 = canvas;

        canvas2.drawRect(posX-10f,canvas.getHeight() - textSize-5f,posX+length,canvas.getHeight()+textSize+5f,paint2);

        if(date != null && time != null)
        canvas2.drawText(date[1] + "-" + date[2] + " " + time[0] + ":" + time[1],posX,canvas.getHeight()-5f,paint);

        if (posX > (canvas.getWidth() / 2.0)) {
            getOffsetForDrawingAtPoint(posX, posY);
            super.draw(canvas);
        }else {
            super.draw(canvas,canvas.getWidth() / 5 * 3,-(int) posY);
        }
    }
}
