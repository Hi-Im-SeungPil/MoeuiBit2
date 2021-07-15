package org.jeonfeel.moeuibit2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.lang.Math.round;

public class Adapter_rvCoin extends RecyclerView.Adapter<Adapter_rvCoin.CustomViewHolder> {

    private ArrayList<CoinDTO> item;
    private Context context;

    public Adapter_rvCoin(ArrayList<CoinDTO> item, Context context) {
        this.item = item;
        this.context = context;
    }

    public void setItem(ArrayList<CoinDTO> item) {
        this.item = item;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_coin_item,parent,false);

        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    public long getItemId(int position){
        item.get(position).setId(item.get(position).hashCode());
        return Long.parseLong(item.get(position).getId()+"");
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        Double dayToDay = item.get(position).getDayToDay()*100;
        DecimalFormat decimalFormat = new DecimalFormat("###,###"); //데시말 포맷 설정 (천단위 콤마찍기)
//---------------------------------
        holder.tv_coinName.setText(item.get(position).getKoreanName());
//---------------------------------
        if(item.get(position).getCurrentPrice() > 100){ //만약 100원보다 가격이 높으면 천단위 콤마
            int currentPrice = (int) round(item.get(position).getCurrentPrice());
            String currentPriceResult = decimalFormat.format(currentPrice);
            holder.tv_currentPrice.setText(currentPriceResult+"");
        }else{
            holder.tv_currentPrice.setText(String.valueOf(item.get(position).getCurrentPrice()));
        }

        //---------------------------------

        holder.tv_dayToDay.setText(String.format("%.2f",dayToDay) + " %"); //스트링 포맷으로 소수점 2자리 반올림

        if(dayToDay > 0 ){ //어제보다 높으면 주황 낮으면 파랑
            holder.tv_dayToDay.setTextColor(Color.parseColor("#B77300"));
            holder.tv_currentPrice.setTextColor(Color.parseColor("#B77300"));
        }else{
            holder.tv_dayToDay.setTextColor(Color.parseColor("#0054FF"));
            holder.tv_currentPrice.setTextColor(Color.parseColor("#0054FF"));
        }
//---------------------------------
        int transactionAmount = (int) round(item.get(position).getTransactionAmount()*0.000001); //백만단위로 끊기
        String transactionAmountResult = decimalFormat.format(transactionAmount);

        holder.tv_transactionAmount.setText( transactionAmountResult+" 백만");
//---------------------------------
        holder.linear_coin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,Activity_coinInfo.class);
                intent.putExtra("market",item.get(position).getMarket());
                intent.putExtra("koreanName",item.get(position).getKoreanName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected LinearLayout linear_coin;
        protected TextView tv_coinName;
        protected TextView tv_currentPrice;
        protected TextView tv_dayToDay;
        protected TextView tv_transactionAmount;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.linear_coin = itemView.findViewById(R.id.linear_coin);
            this.tv_coinName = itemView.findViewById(R.id.tv_coinName);
            this.tv_currentPrice = itemView.findViewById(R.id.tv_currentPrice);
            this.tv_dayToDay = itemView.findViewById(R.id.tv_dayToDay);
            this.tv_transactionAmount = itemView.findViewById(R.id.tv_transactionAmount);

        }
    }
}
