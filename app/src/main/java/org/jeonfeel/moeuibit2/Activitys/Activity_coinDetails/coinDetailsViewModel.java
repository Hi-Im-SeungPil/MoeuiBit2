package org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails;

import static java.lang.Math.round;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jeonfeel.moeuibit2.databinding.ActivityCoinInfoBinding;

import java.text.DecimalFormat;

public class coinDetailsViewModel extends AndroidViewModel {

    private final DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private MutableLiveData<SelectedCoinModel> selectedCoinModelLiveData;
    private final Context context;

    public coinDetailsViewModel(@NonNull Application application) {
        super(application);

        this.context = application;

    }

    public MutableLiveData<SelectedCoinModel> getSelectedCoinModelLiveData(){
        if(selectedCoinModelLiveData == null){
            selectedCoinModelLiveData = new MutableLiveData<>();
        }
        return selectedCoinModelLiveData;
    }

    public Double initCoinDetails(ActivityCoinInfoBinding binding,String koreanName,String symbol){

        Double currentPrice = 0.0;

        SelectedCoinModel  model = selectedCoinModelLiveData.getValue();
        currentPrice = model.getCurrentPrice();

        Double dayToDay = model.getDayToDay();
        Double changePrice = model.getChangePrice();

        //--------------------------------------------------
        binding.tvCoinInfoCoinName.setText(koreanName + "( KRW / "+symbol+" )");
        //--------------------------------------------------

        if(currentPrice >= 100){ //만약 100원보다 가격이 높으면 천단위 콤마
            String currentPriceResult = decimalFormat.format(round(currentPrice));
            binding.tvCoinInfoCoinPrice.setText(currentPriceResult);
        }else if(currentPrice < 100 && currentPrice >= 1){
            binding.tvCoinInfoCoinPrice.setText(String.format("%.2f", currentPrice));
        }else if(currentPrice < 1){
            binding.tvCoinInfoCoinPrice.setText(String.format("%.4f", currentPrice));
        }
        //--------------------------------------------------
        binding.tvCoinInfoCoinDayToDay.setText(String.format("%.2f",dayToDay*100) + "%");
        //--------------------------------------------------
        if(changePrice >= 100){
            binding.tvCoinInfoChangePrice.setText("+"+ decimalFormat.format(round(changePrice)));
        }else if(changePrice <= -100){
            binding.tvCoinInfoChangePrice.setText(decimalFormat.format(round(changePrice)));
        }else if(changePrice < 100 && changePrice >= 1){
            binding.tvCoinInfoChangePrice.setText("+"+String.format("%.2f",changePrice));
        }else if(changePrice <= -1 && changePrice > -100){
            binding.tvCoinInfoChangePrice.setText(String.format("%.2f",changePrice));
        }else if(changePrice > 0 && changePrice < 1){
            binding.tvCoinInfoChangePrice.setText("+"+String.format("%.3f",changePrice));
        }else if(changePrice > -1 && changePrice < 0){
            binding.tvCoinInfoChangePrice.setText(String.format("%.3f",changePrice));
        }else if(changePrice == 0){
            binding.tvCoinInfoChangePrice.setText("0.00");
        }

        if(changePrice > 0){
            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#B77300"));
            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#B77300"));
            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#B77300"));
        }else if(changePrice < 0){
            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#0054FF"));
            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#0054FF"));
            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#0054FF"));
        }else if(changePrice == 0){
            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#000000"));
            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#000000"));
            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#000000"));
        }
        return currentPrice;
    }

    public Double updateCoinDetails(ActivityCoinInfoBinding binding){

        Double currentPrice;

        SelectedCoinModel model = selectedCoinModelLiveData.getValue();

        currentPrice = model.getCurrentPrice();
        Double dayToDay = model.getDayToDay();
        Double changePrice = model.getChangePrice();

        if (currentPrice >= 100) { //만약 100원보다 가격이 높으면 천단위 콤마
            String currentPriceResult = decimalFormat.format(round(currentPrice));
            binding.tvCoinInfoCoinPrice.setText(currentPriceResult);
        } else if (currentPrice < 100 && currentPrice >= 1) {
            binding.tvCoinInfoCoinPrice.setText(String.format("%.2f", currentPrice));
        } else if (currentPrice < 1) {
            binding.tvCoinInfoCoinPrice.setText(String.format("%.4f", currentPrice));
        }
        //--------------------------------------------------
        binding.tvCoinInfoCoinDayToDay.setText(String.format("%.2f", dayToDay * 100) + "%");
        //--------------------------------------------------
        if (changePrice >= 100) {
            binding.tvCoinInfoChangePrice.setText("+" + decimalFormat.format(round(changePrice)));
        } else if (changePrice <= -100) {
            binding.tvCoinInfoChangePrice.setText(decimalFormat.format(round(changePrice)) + "");
        } else if (changePrice < 100 && changePrice >= 1) {
            binding.tvCoinInfoChangePrice.setText("+" + String.format("%.2f", changePrice));
        } else if (changePrice <= -1 && changePrice > -100) {
            binding.tvCoinInfoChangePrice.setText(String.format("%.2f", changePrice));
        } else if (changePrice > 0 && changePrice < 1) {
            binding.tvCoinInfoChangePrice.setText("+" + String.format("%.3f", changePrice));
        } else if (changePrice > -1 && changePrice < 0) {
            binding.tvCoinInfoChangePrice.setText(String.format("%.3f", changePrice));
        } else if (changePrice == 0) {
            binding.tvCoinInfoChangePrice.setText("0.00");
        }

        if (changePrice > 0) {
            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#B77300"));
            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#B77300"));
            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#B77300"));
        } else if (changePrice < 0) {
            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#0054FF"));
            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#0054FF"));
            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#0054FF"));
        } else if (changePrice == 0) {
            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#000000"));
            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#000000"));
            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#000000"));
        }
        return currentPrice;
    }
}
