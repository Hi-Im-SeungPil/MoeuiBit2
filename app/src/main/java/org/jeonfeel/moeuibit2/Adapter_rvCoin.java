package org.jeonfeel.moeuibit2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import static java.lang.Math.round;

public class Adapter_rvCoin extends RecyclerView.Adapter<Adapter_rvCoin.CustomViewHolder> {

    private ArrayList<CoinDTO> item;
    private Context context;

    public Adapter_rvCoin(ArrayList<CoinDTO> item, Context context) {
        this.item = item;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_coin_item,parent,false);

        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        holder.tv_coinName.setText(item.get(position).getKoreanName());
        if(item.get(position).getCurrentPrice() > 100){
            int currentPrice = (int) round(item.get(position).getCurrentPrice());
            holder.tv_currentPrice.setText(currentPrice+"");
        }else{
            holder.tv_currentPrice.setText(String.valueOf(item.get(position).getCurrentPrice()));
        }
        holder.tv_dayToDay.setText(String.valueOf(item.get(position).getDayToDay() * 100 + " %"));
        holder.tv_transactionAmount.setText((int) round(item.get(position).getTransactionAmount())+"");

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected TextView tv_coinName;
        protected TextView tv_currentPrice;
        protected TextView tv_dayToDay;
        protected TextView tv_transactionAmount;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_coinName = itemView.findViewById(R.id.tv_coinName);
            this.tv_currentPrice = itemView.findViewById(R.id.tv_currentPrice);
            this.tv_dayToDay = itemView.findViewById(R.id.tv_dayToDay);
            this.tv_transactionAmount = itemView.findViewById(R.id.tv_transactionAmount);

        }
    }
}
