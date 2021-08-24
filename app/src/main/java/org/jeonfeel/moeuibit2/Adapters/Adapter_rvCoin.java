package org.jeonfeel.moeuibit2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.moeuibit2.Activitys.Activity_coinInfo;
import org.jeonfeel.moeuibit2.DTOS.CoinDTO;
import org.jeonfeel.moeuibit2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static java.lang.Math.round;

public class Adapter_rvCoin extends RecyclerView.Adapter<Adapter_rvCoin.CustomViewHolder> implements Filterable {

    private ArrayList<CoinDTO> item;
    private ArrayList<CoinDTO> filteredItem;
    private ArrayList<CoinDTO> secondFilteredItem;
    private Context context;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private ArrayList<Integer> marketPosition;
    private boolean favoriteStatus = false;

    public Adapter_rvCoin(ArrayList<CoinDTO> item, Context context) {
        this.item = item;
        this.filteredItem = item;
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

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        if(filteredItem.size() != 0) {
            Double dayToDay = filteredItem.get(position).getDayToDay() * 100;
//---------------------------------
            holder.tv_coinName.setText(filteredItem.get(position).getKoreanName());
            holder.tv_coinMarket.setText(filteredItem.get(position).getSymbol() + " / KRW");
//---------------------------------
            if (filteredItem.get(position).getCurrentPrice() >= 100) { //만약 100원보다 가격이 높으면 천단위 콤마
                int currentPrice = (int) round(filteredItem.get(position).getCurrentPrice());
                String currentPriceResult = decimalFormat.format(currentPrice);
                holder.tv_currentPrice.setText(currentPriceResult + "");
            } else {
                holder.tv_currentPrice.setText(String.format("%.2f", filteredItem.get(position).getCurrentPrice()));
            }
            //---------------------------------
            holder.tv_dayToDay.setText(String.format("%.2f", dayToDay) + " %"); //스트링 포맷으로 소수점 2자리 반올림

            if (dayToDay > 0) { //어제보다 높으면 주황 낮으면 파랑
                holder.tv_dayToDay.setTextColor(Color.parseColor("#B77300"));
                holder.tv_currentPrice.setTextColor(Color.parseColor("#B77300"));
            } else if (dayToDay < 0) {
                holder.tv_dayToDay.setTextColor(Color.parseColor("#0054FF"));
                holder.tv_currentPrice.setTextColor(Color.parseColor("#0054FF"));
            } else if (String.format("%.2f", dayToDay).equals("0.00")) { //같으면 검정
                holder.tv_dayToDay.setTextColor(Color.parseColor("#000000"));
                holder.tv_currentPrice.setTextColor(Color.parseColor("#000000"));
            }
//---------------------------------
            int transactionAmount = (int) round(filteredItem.get(position).getTransactionAmount() * 0.000001); //백만단위로 끊기
            String transactionAmountResult = decimalFormat.format(transactionAmount);

            holder.tv_transactionAmount.setText(transactionAmountResult + " 백만");
//---------------------------------
            holder.linear_coin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, Activity_coinInfo.class);
                    intent.putExtra("market", filteredItem.get(position).getMarket());
                    intent.putExtra("symbol", filteredItem.get(position).getSymbol());
                    intent.putExtra("koreanName", filteredItem.get(position).getKoreanName());
                    context.startActivity(intent);
                }
            });
        }
    }

    public void setMarkets(ArrayList<Integer> marketPosition){
        this.marketPosition = null;
        this.marketPosition = new ArrayList<>();
        this.marketPosition.addAll(marketPosition);
    }

    public void setFavoriteStatus(boolean favoriteStatus){
        this.favoriteStatus = favoriteStatus;
    }

    @Override
    public int getItemCount() {

        return filteredItem.size();

    }

 //검색을 위한 필터.
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String str = charSequence.toString().toLowerCase();

                if(str.isEmpty() && !favoriteStatus){

                    secondFilteredItem = null;
                    secondFilteredItem = item;

                }else if(favoriteStatus){

                    ArrayList<CoinDTO> filteringItem = new ArrayList<>();
                    ArrayList<CoinDTO> sampleItem = new ArrayList<>();

                    for(int i = 0; i < marketPosition.size(); i++){
                        filteringItem.add(item.get(marketPosition.get(i)));
                    }

                    if(!str.isEmpty()){

                        for(CoinDTO i : filteringItem){
                            if(i.getKoreanName().contains(str) || i.getEnglishName().toLowerCase().contains(str) || i.getSymbol().toLowerCase().contains(str)){
                                sampleItem.add(i);
                            }
                        }
                        secondFilteredItem = null;
                        secondFilteredItem = new ArrayList<>();
                        secondFilteredItem.addAll(sampleItem);
                    }else{
                        secondFilteredItem = null;
                        secondFilteredItem = new ArrayList<>();
                        secondFilteredItem.addAll(filteringItem);
                    }

                }else{
                    ArrayList<CoinDTO> filteringItem = new ArrayList<>();

                    for(CoinDTO i : item){
                        if(i.getKoreanName().contains(str) || i.getEnglishName().toLowerCase().contains(str) || i.getSymbol().toLowerCase().contains(str)){
                            filteringItem.add(i);
                        }
                    }
                    secondFilteredItem = null;
                    secondFilteredItem = new ArrayList<>();
                    secondFilteredItem.addAll(filteringItem);
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = secondFilteredItem;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredItem = (ArrayList<CoinDTO>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        protected LinearLayout linear_coin;
        protected TextView tv_coinName;
        protected TextView tv_currentPrice;
        protected TextView tv_dayToDay;
        protected TextView tv_transactionAmount;
        protected TextView tv_coinMarket;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.linear_coin = itemView.findViewById(R.id.linear_coin);
            this.tv_coinName = itemView.findViewById(R.id.tv_coinName);
            this.tv_currentPrice = itemView.findViewById(R.id.tv_currentPrice);
            this.tv_dayToDay = itemView.findViewById(R.id.tv_dayToDay);
            this.tv_transactionAmount = itemView.findViewById(R.id.tv_transactionAmount);
            this.tv_coinMarket = itemView.findViewById(R.id.tv_coinMarket);

        }
    }
}
