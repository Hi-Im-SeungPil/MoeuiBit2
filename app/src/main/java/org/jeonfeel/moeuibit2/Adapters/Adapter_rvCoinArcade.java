package org.jeonfeel.moeuibit2.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.moeuibit2.Activitys.Activity_coinInfo;
import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO;
import org.jeonfeel.moeuibit2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.lang.Math.round;

public class Adapter_rvCoinArcade extends RecyclerView.Adapter<Adapter_rvCoinArcade.CustomViewHolder> {

    private ArrayList<CoinArcadeDTO> item;
    private Context context;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private EditText et_orderCoinPrice,et_orderCoinQuantity,et_sellCoinQuantity,et_sellCoinPrice;
    private Double openingPrice;
    private LinearLayout linear_coinOrder,linear_coinSell;
    private Activity_coinInfo activity_coinInfo = new Activity_coinInfo();


    public Adapter_rvCoinArcade(ArrayList<CoinArcadeDTO> item, Context context,Double openingPrice
            ,EditText et_orderCoinPrice,EditText et_orderCoinQuantity,EditText et_sellCoinPrice,EditText et_sellCoinQuantity
    ,LinearLayout linear_coinOrder,LinearLayout linear_coinSell) {
        this.item = item;
        this.context = context;
        this.openingPrice = openingPrice;
        this.et_orderCoinPrice = et_orderCoinPrice;
        this.et_orderCoinQuantity = et_orderCoinQuantity;
        this.et_sellCoinPrice = et_sellCoinPrice;
        this.et_sellCoinQuantity = et_sellCoinQuantity;
        this.linear_coinOrder = linear_coinOrder;
        this.linear_coinSell = linear_coinSell;
    }

    public void setItem(ArrayList<CoinArcadeDTO> item){
        this.item = item;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_arcade_item,parent,false);

        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_rvCoinArcade.CustomViewHolder holder, int position) {
        int integerArcadePrice = (int) round(item.get(position).getCoinArcadePrice());
        int integerOpenPrice = (int) round(openingPrice);
        Double arcadePrice = item.get(position).getCoinArcadePrice();
        Double dayToDay = 0.0;

        if(integerArcadePrice > 1000000){
            holder.tv_coinArcadeDayToDay.setVisibility(View.GONE);
            holder.tv_coinArcadePrice.setGravity(Gravity.RIGHT);
            holder.tv_coinArcadePrice.setPadding(0,0,10,0);
        }

        //-------------------------------------------------------------------------------------------------
        if(integerArcadePrice > 100){
            holder.tv_coinArcadePrice.setText(decimalFormat.format(integerArcadePrice));

            dayToDay = (Double) (((integerArcadePrice - integerOpenPrice) / openingPrice) * 100);
            holder.tv_coinArcadeDayToDay.setText(String.format("%.2f",dayToDay)+"%");

        }else if(arcadePrice < 100){

            holder.tv_coinArcadePrice.setText(String.format("%.2f",arcadePrice));

            dayToDay = (Double) (((item.get(position).getCoinArcadePrice() - openingPrice) / openingPrice) * 100);
            holder.tv_coinArcadeDayToDay.setText(String.format("%.2f",dayToDay)+"%");

        }else if(arcadePrice == 100.0){

            holder.tv_coinArcadePrice.setText(integerArcadePrice+"");

            dayToDay = (Double) (((integerArcadePrice - integerOpenPrice) / openingPrice) * 100);
            holder.tv_coinArcadeDayToDay.setText(String.format("%.2f",dayToDay)+"%");
        }

        if(((Activity_coinInfo)context).getGlobalCurrentPrice() != null){

            Double currentPrice = ((Activity_coinInfo)context).getGlobalCurrentPrice();
            int intCurrentPrice = 0;

            if(currentPrice > 100){
                intCurrentPrice = (int) round(currentPrice);

                if(intCurrentPrice == integerArcadePrice){
                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_border2);
                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_border3);
                }else{
                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_item_border);
                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_item_border);
                }

            }else {
                String price = String.format("%.2f", arcadePrice);
                String Price2 = String.format("%.2f", currentPrice);

                Log.d("qqqq",price);
                Log.d("qqqq2",Price2);

                if (Price2.equals(price)) {
                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_border2);
                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_border3);
                }else{
                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_item_border);
                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_item_border);
                }

            }
        }

        //-------------------------------------------------------------------------------------------------
        holder.tv_coinArcadeAmount.setText(String.format("%.3f",item.get(position).getCoinArcadeSize()));
        //-------------------------------------------------------------------------------------------------
        if(item.get(position).getArcadeStatus().equals("ask")){
            holder.linear_wholeItem.setBackgroundColor(Color.parseColor("#330100FF"));
        }else{
            holder.linear_wholeItem.setBackgroundColor(Color.parseColor("#33FF0000"));
        }

        if(dayToDay > 0){
            holder.tv_coinArcadePrice.setTextColor(Color.parseColor("#B77300"));
            holder.tv_coinArcadeDayToDay.setTextColor(Color.parseColor("#B77300"));
        }else if(dayToDay < 0){
            holder.tv_coinArcadePrice.setTextColor(Color.parseColor("#0054FF"));
            holder.tv_coinArcadeDayToDay.setTextColor(Color.parseColor("#0054FF"));
        }else if(String.format("%.2f",dayToDay).equals("0.00")){
            holder.tv_coinArcadePrice.setTextColor(Color.parseColor("#000000"));
            holder.tv_coinArcadeDayToDay.setTextColor(Color.parseColor("#000000"));
        }

//        holder.linear_wholeItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String arcadePrice = holder.tv_coinArcadePrice.getText().toString();
//
//                if(linear_coinOrder.getVisibility() == View.VISIBLE) {
//
//                    et_orderCoinPrice.setText(arcadePrice);
//                    InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
//                    if (et_orderCoinPrice.isFocused()) {
//                        et_orderCoinPrice.clearFocus();
//                        imm.hideSoftInputFromWindow(et_orderCoinPrice.getWindowToken(), 0);
//                    } else if (et_orderCoinQuantity.isFocused()) {
//                        et_orderCoinQuantity.clearFocus();
//                        imm.hideSoftInputFromWindow(et_orderCoinQuantity.getWindowToken(), 0);
//                    }
//                }else if(linear_coinSell.getVisibility() == View.VISIBLE){
//
//                    et_sellCoinPrice.setText(arcadePrice);
//                    InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
//
//                    if (et_sellCoinPrice.isFocused()) {
//                        et_sellCoinPrice.clearFocus();
//                        imm.hideSoftInputFromWindow(et_sellCoinPrice.getWindowToken(), 0);
//                    } else if (et_sellCoinQuantity.isFocused()) {
//                        et_sellCoinQuantity.clearFocus();
//                        imm.hideSoftInputFromWindow(et_sellCoinQuantity.getWindowToken(), 0);
//                    }
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected TextView tv_coinArcadePrice,tv_coinArcadeAmount,tv_coinArcadeDayToDay;
        protected LinearLayout linear_wholeItem,linear_arcade1,linear_wholeItem2;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.linear_arcade1 = itemView.findViewById(R.id.linear_arcade1);
            this.tv_coinArcadePrice = itemView.findViewById(R.id.tv_coinArcadePrice);
            this.tv_coinArcadeAmount = itemView.findViewById(R.id.tv_coinArcadeAmount);
            this.tv_coinArcadeDayToDay = itemView.findViewById(R.id.tv_coinArcadeDayToDay);
            this.linear_wholeItem = itemView.findViewById(R.id.linear_wholeItem);
            this.linear_wholeItem2 = itemView.findViewById(R.id.linear_wholeItem2);
        }
    }

}
