package org.jeonfeel.moeuibit2.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO;
import org.jeonfeel.moeuibit2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

import static java.lang.Math.round;

public class Adapter_rvMyCoins extends RecyclerView.Adapter<Adapter_rvMyCoins.CustomViewHolder> {

    private ArrayList<MyCoinsDTO> item;
    private Context context;
    private ArrayList<Double> currentPrices = null;
    private DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public Adapter_rvMyCoins(ArrayList<MyCoinsDTO> item,Context context){
        this.item = item;
        this.context = context;
    }

    @NonNull
    @Override
    public Adapter_rvMyCoins.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_my_coins_item,parent,false);

        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_rvMyCoins.CustomViewHolder holder, int position) {
        Double quantity = item.get(position).getMyCoinsQuantity();
        Double purchaseAmount = quantity * item.get(position).getMyCoinsBuyingAverage();

        holder.tv_myCoinsSymbol.setText(item.get(position).getMyCoinsSymbol());
        holder.tv_myCoinsSymbol2.setText(item.get(position).getMyCoinsSymbol());
        holder.tv_myCoinsKoreanName.setText(item.get(position).getMyCoinsKoreanName());
        holder.tv_myCoinsQuantity.setText(String.valueOf(item.get(position).getMyCoinsQuantity()));

        if(item.get(position).getMyCoinsBuyingAverage() >= 100) {
            holder.tv_myCoinsBuyingAverage.setText(decimalFormat.format(round(item.get(position).getMyCoinsBuyingAverage())));
        }else{
            holder.tv_myCoinsBuyingAverage.setText(String.valueOf(item.get(position).getMyCoinsBuyingAverage()));
        }
        holder.tv_myCoinsPurchaseAmount.setText(decimalFormat.format(round(item.get(position).getMyCoinsQuantity() * item.get(position).getMyCoinsBuyingAverage())));

        if(currentPrices != null) {
            Double currentPrice = currentPrices.get(position);
            Double evaluationAmount = quantity * currentPrice;

            if(round(evaluationAmount - purchaseAmount) > 0){
                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#B77300"));
                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#B77300"));
            }else if(round(evaluationAmount - purchaseAmount) < 0 ){
                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#0054FF"));
                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#0054FF"));
            }else{
                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#000000"));
                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#000000"));
            }

            holder.tv_myCoinsEvaluationAmount.setText(decimalFormat.format(round(quantity * currentPrice)));
            holder.tv_myCoinsEvaluation.setText(decimalFormat.format(round(evaluationAmount - purchaseAmount)));
            holder.tv_myCoinsEarningsRate.setText(String.format("%.2f",(evaluationAmount - purchaseAmount) / purchaseAmount * 100) + "%");
        }
    }
    public void setItem(ArrayList<MyCoinsDTO> myCoinsDTOS){
        this.item = myCoinsDTOS;
    }

    public void setCurrentPrices(ArrayList<Double> currentPrices){
        this.currentPrices = currentPrices;
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_myCoinsKoreanName,tv_myCoinsSymbol,tv_myCoinsEvaluation,tv_myCoinsEarningsRate,tv_myCoinsQuantity,tv_myCoinsSymbol2
                ,tv_myCoinsBuyingAverage,tv_myCoinsEvaluationAmount,tv_myCoinsPurchaseAmount;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_myCoinsKoreanName = itemView.findViewById(R.id.tv_myCoinsKoreanName);
            this.tv_myCoinsSymbol = itemView.findViewById(R.id.tv_myCoinsSymbol);
            this.tv_myCoinsEvaluation = itemView.findViewById(R.id.tv_myCoinsEvaluation);
            this.tv_myCoinsEarningsRate = itemView.findViewById(R.id.tv_myCoinsEarningsRate);
            this.tv_myCoinsQuantity = itemView.findViewById(R.id.tv_myCoinsQuantity);
            this.tv_myCoinsSymbol2 = itemView.findViewById(R.id.tv_myCoinsSymbol2);
            this.tv_myCoinsBuyingAverage = itemView.findViewById(R.id.tv_myCoinsBuyingAverage);
            this.tv_myCoinsEvaluationAmount = itemView.findViewById(R.id.tv_myCoinsEvaluationAmount);
            this.tv_myCoinsPurchaseAmount = itemView.findViewById(R.id.tv_myCoinsPurchaseAmount);

        }
    }
}
