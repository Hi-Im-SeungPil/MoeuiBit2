package org.jeonfeel.moeuibit2.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jeonfeel.moeuibit2.Database.TransactionInfo;
import org.jeonfeel.moeuibit2.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.round;

public class Adapter_rvTransactionInfo extends RecyclerView.Adapter<Adapter_rvTransactionInfo.CustomViewHolder> {

    List<TransactionInfo> item;
    Context context;
    DecimalFormat decimalFormat = new DecimalFormat("###,###");

    public Adapter_rvTransactionInfo(List<TransactionInfo> item, Context context) {
        this.item = item;
        this.context = context;
    }

    public void setItem(List<TransactionInfo> item){
        this.item = new ArrayList<>();
        this.item.addAll(item);
    }

    @NonNull
    @Override
    public Adapter_rvTransactionInfo.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.rv_transaction_info_item,parent,false);

        CustomViewHolder customViewHolder = new CustomViewHolder(view);

        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_rvTransactionInfo.CustomViewHolder holder, int position) {

        String transactionStatus;
        Double price = item.get(position).getPrice();
        Double quantity = item.get(position).getQuantity();

        if(item.get(position).getTransactionStatus().equals("bid")){
            transactionStatus = "매수";
            holder.tv_transactionStatus.setTextColor(Color.parseColor("#B77300"));
        }else{
            transactionStatus = "매도";
            holder.tv_transactionStatus.setTextColor(Color.parseColor("#0054FF"));
        }

        holder.tv_transactionStatus.setText(transactionStatus);
        holder.tv_transactionMarket.setText(item.get(position).getMarket());
        holder.tv_transactionTime.setText(item.get(position).getTransactionTime());

        if(price >= 100)
        holder.tv_transactionPrice.setText(decimalFormat.format(round(price)));
        else{
            holder.tv_transactionPrice.setText(String.format("%.2f",price));
        }
        holder.tv_transactionQuantity.setText(String.format("%.8f",quantity));
        holder.tv_transactionAmount.setText(decimalFormat.format(round(price * quantity)));

    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_transactionStatus,tv_transactionMarket,tv_transactionTime
                ,tv_transactionPrice,tv_transactionQuantity,tv_transactionAmount;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_transactionStatus = itemView.findViewById(R.id.tv_transactionStatus);
            this.tv_transactionMarket = itemView.findViewById(R.id.tv_transactionMarket);
            this.tv_transactionTime = itemView.findViewById(R.id.tv_transactionTime);
            this.tv_transactionPrice = itemView.findViewById(R.id.tv_transactionPrice);
            this.tv_transactionQuantity = itemView.findViewById(R.id.tv_transactionQuantity);
            this.tv_transactionAmount = itemView.findViewById(R.id.tv_transactionAmount);

        }
    }

}
