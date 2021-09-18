package org.jeonfeel.moeuibit2.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.moeuibit2.Activitys.Activity_coinInfo;
import org.jeonfeel.moeuibit2.CheckNetwork;
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

        String symbol = item.get(position).getMyCoinsSymbol();
        String koreanName = item.get(position).getMyCoinsKoreanName();
        Double buyingAverage = item.get(position).getMyCoinsBuyingAverage();
        Double quantity = item.get(position).getMyCoinsQuantity();
        long purchaseAmount = round(quantity * buyingAverage);

        holder.tv_myCoinsSymbol.setText(symbol);
        holder.tv_myCoinsSymbol2.setText(symbol);
        holder.tv_myCoinsKoreanName.setText(koreanName);
        holder.tv_myCoinsQuantity.setText(String.valueOf(quantity));

        if(buyingAverage >= 100) {
            long buyingAverage1 = round(buyingAverage);
            holder.tv_myCoinsBuyingAverage.setText(decimalFormat.format(buyingAverage1));
        }else{
            holder.tv_myCoinsBuyingAverage.setText(String.format("%.2f",buyingAverage));
        }

        holder.tv_myCoinsPurchaseAmount.setText(decimalFormat.format(purchaseAmount));

        if(currentPrices != null) {

            Double currentPrice = currentPrices.get(position);
            long evaluationAmount = round(quantity * currentPrice);
            long lossGainAmount = evaluationAmount - purchaseAmount;
            double lossGainCoin = currentPrice - buyingAverage;
            double earningRate = lossGainCoin / buyingAverage * 100;

            if(lossGainAmount > 0){
                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#B77300"));
                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#B77300"));
                holder.tv_myCoinsDifference.setTextColor(Color.parseColor("#B77300"));
            }else if(lossGainAmount < 0 ){
                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#0054FF"));
                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#0054FF"));
                holder.tv_myCoinsDifference.setTextColor(Color.parseColor("#0054FF"));
            }else{
                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#000000"));
                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#000000"));
                holder.tv_myCoinsDifference.setTextColor(Color.parseColor("#000000"));
            }
            if(currentPrice >= 100) {
                holder.tv_myCoinsCurrentPrice.setText(decimalFormat.format(round(currentPrice)));
            }else{
                holder.tv_myCoinsCurrentPrice.setText(String.format("%.2f",currentPrice));
            }

            if(lossGainCoin >= 100 || lossGainCoin <= -100){
                long text = round(lossGainCoin);
                holder.tv_myCoinsDifference.setText(decimalFormat.format(text));
            }else{
                holder.tv_myCoinsDifference.setText(String.format("%.2f",lossGainCoin));
            }

            holder.tv_myCoinsEvaluationAmount.setText(decimalFormat.format(evaluationAmount));
            holder.tv_myCoinsEvaluation.setText(decimalFormat.format(lossGainAmount));
            holder.tv_myCoinsEarningsRate.setText(String.format("%.2f",earningRate) + "%");
        }

        holder.linear_myCoinsWholeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("거래").setMessage(item.get(position).getMyCoinsKoreanName() +" 거래하기")
                        .setPositiveButton("이동", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                if(CheckNetwork.CheckNetwork(context) != 0) {

                                    Intent intent = new Intent(context, Activity_coinInfo.class);
                                    intent.putExtra("koreanName", koreanName);
                                    intent.putExtra("symbol", symbol);
                                    intent.putExtra("market", "KRW-" + symbol);
                                    context.startActivity(intent);

                                }else{
                                    Toast.makeText(context, "네트워크 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

            }
        });

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
                ,tv_myCoinsBuyingAverage,tv_myCoinsEvaluationAmount,tv_myCoinsPurchaseAmount,tv_myCoinsCurrentPrice,tv_myCoinsDifference;

        private LinearLayout linear_myCoinsWholeItem;

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
            this.tv_myCoinsCurrentPrice = itemView.findViewById(R.id.tv_myCoinsCurrentPrice);
            this.tv_myCoinsDifference = itemView.findViewById(R.id.tv_myCoinsDifference);
            this.linear_myCoinsWholeItem = itemView.findViewById(R.id.linear_myCoinsWholeItem);

        }
    }
}
