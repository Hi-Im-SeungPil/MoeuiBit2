package org.jeonfeel.moeuibit2.Fragment.Chart;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import org.jeonfeel.moeuibit2.R;

public class MoEuiBitMarkerView extends MarkerView {

    TextView tv_candleTime,tv_startPrice,tv_highPrice,tv_lowPrice,tv_endPrice,tv_coefficientOfPrice,tv_coefficientOfFluctuation,tv_markerTransactionAmount;

    /**
     * Constructor. Sets up the MarkerView with a custom layout resource.
     *
     * @param context
     * @param layoutResource the layout resource to use for the MarkerView
     */
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
            tv_startPrice.setText(((CandleEntry) ce).getOpen()+"");
            tv_highPrice.setText(((CandleEntry) ce).getHigh()+"");
            tv_lowPrice.setText(((CandleEntry) ce).getLow()+"");
            tv_endPrice.setText(((CandleEntry) ce).getClose()+"");
            tv_coefficientOfPrice.setText(((CandleEntry) ce).getClose() - ((CandleEntry) ce).getOpen() +"");
        }

        super.refreshContent(e,highlight);

    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {

        if (posX > (canvas.getWidth() / 2.0)) {
            getOffsetForDrawingAtPoint(posX, posY);
            super.draw(canvas);
            //Draw marker on the left top corner
        }else {
            //Otherwise draw the marker on the top right corner.
            //Check if the user is in the right half of the canvas
            super.draw(canvas, (float) (canvas.getWidth() / 1.9), (float) (canvas.getHeight()/12.0));
        }
    }
//    @Override
//    public void setOffset(float offsetX, float offsetY) {
//
//
//
//        super.setOffset(offsetX, offsetY);
//    }
//
//    @Override
//    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {
//
//        if(mOffset == null) {
//            // center the marker horizontally and fixed Y position at the top
//
//            mOffset = new MPPointF(-(getWidth() / 2f), -posY);
//
//        }
//
//        return super.getOffsetForDrawingAtPoint(posX, posY);
//    }
}
