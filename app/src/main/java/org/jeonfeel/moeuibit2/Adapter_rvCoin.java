package org.jeonfeel.moeuibit2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter_rvCoin extends RecyclerView.Adapter<Adapter_rvCoin.CustomViewHolder> {

    private ArrayList<item_rvCoin> item;
    private Context context;

    public Adapter_rvCoin(ArrayList<item_rvCoin> item, Context context) {
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

        holder.tv_coinName.setText(item.get(position).getCoinName());
        holder.tv_currentPrice.setText(item.get(position).getCurrentPrice());
        holder.tv_dayToDay.setText(item.get(position).getDayToDay());
        holder.tv_transactionAmount.setText(item.get(position).getTransactionAmount());

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
