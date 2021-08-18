package org.jeonfeel.moeuibit2.Fragment.Chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import org.jeonfeel.moeuibit2.R;

import java.text.DecimalFormat;

import static java.lang.Math.round;

public class MoEuiBitMarkerView extends MarkerView {

    TextView tv_candleTime,tv_startPrice,tv_highPrice,
            tv_lowPrice,tv_endPrice,tv_coefficientOfPrice,
            tv_coefficientOfFluctuation,tv_markerTransactionAmount;

    private DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public MoEuiBitMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
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

            String kst = Fragment_chart.getCoinCandle((int) ce.getX() - 2);

            String[] fullyDate = kst.split("T");
            String[] date = fullyDate[0].split("-");
            String[] time = fullyDate[1].split(":");


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
        }

        super.refreshContent(e,highlight);

    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {

        if (posX > (canvas.getWidth() / 2.0)) {
            getOffsetForDrawingAtPoint(posX, posY);
            Log.d("qqqq","posx : "+posX+"  /  "+"posy : "+posY+"");
            super.draw(canvas);
            //Draw marker on the left top corner
        }else {

            Log.d("qqqq","posx : "+canvas.getWidth()+"  /  "+"posy : "+posY+"");
            super.draw(canvas,canvas.getWidth() / 5 * 3,-(int) posY);
        }
    }

}
