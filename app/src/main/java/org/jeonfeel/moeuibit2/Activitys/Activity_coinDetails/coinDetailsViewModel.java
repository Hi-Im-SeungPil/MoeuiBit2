package org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails;

import static java.lang.Math.round;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import org.jeonfeel.moeuibit2.Database.Favorite;
import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.R;
import org.jeonfeel.moeuibit2.databinding.ActivityCoinDetailsBinding;

import java.text.DecimalFormat;
import android.os.Handler;

import com.bumptech.glide.Glide;


public class coinDetailsViewModel extends AndroidViewModel {

    private final DecimalFormat decimalFormat = new DecimalFormat("###,###");
    private MutableLiveData<SelectedCoinModel> selectedCoinModelLiveData;
    private final MoEuiBitDatabase db;
    private final Context context;

    public coinDetailsViewModel(@NonNull Application application) {
        super(application);
        this.context = application.getApplicationContext();
        db = MoEuiBitDatabase.getInstance(context);
    }

    public MutableLiveData<SelectedCoinModel> getSelectedCoinModelLiveData(){
        if(selectedCoinModelLiveData == null){
            selectedCoinModelLiveData = new MutableLiveData<>();
        }
        return selectedCoinModelLiveData;
    }

    public Double initCoinDetails(ActivityCoinDetailsBinding binding, String koreanName, String symbol){

        String imgUrl = "https://raw.githubusercontent.com/Hi-Im-SeungPil/moeuibitImg/main/coinlogo2/"+symbol+".png";
        Glide.with(context).load(imgUrl).error(R.drawable.img_not_yet).into(binding.ivCoinLogo);

        final Favorite[] favorite = new Favorite[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                favorite[0] = db.favoriteDAO().select("KRW-"+symbol);
            }
        }).start();

        if(favorite[0] == null){
            binding.btnBookMark.setBackgroundResource(R.drawable.favorite_off);
        }else{
            binding.btnBookMark.setBackgroundResource(R.drawable.favorite_on);
        }
        //---------------------------------------------------------------------------------
        Double currentPrice = 0.0;
        Double dayToDay = 0.0;
        Double changePrice = 0.0;
        SelectedCoinModel  model = selectedCoinModelLiveData.getValue();

        if (model != null) {
            currentPrice = model.getCurrentPrice();
            dayToDay = model.getDayToDay();
            changePrice = model.getChangePrice();
        }
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

    public Double updateCoinDetails(ActivityCoinDetailsBinding binding){

        Double currentPrice = 0.0;
        Double dayToDay = 0.0;
        Double changePrice = 0.0;
        SelectedCoinModel model = selectedCoinModelLiveData.getValue();

        if(model != null) {
            currentPrice = model.getCurrentPrice();
            dayToDay = model.getDayToDay();
            changePrice = model.getChangePrice();
        }

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

    public void updateBookMark(ActivityCoinDetailsBinding binding,String symbol){

        String market = "KRW-"+symbol;
        Handler handler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {
                Favorite favorite = db.favoriteDAO().select(market);

                if(favorite != null){
                    db.favoriteDAO().delete(market);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.btnBookMark.setBackgroundResource(R.drawable.favorite_off);
                            Toast.makeText(context, "관심코인에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    db.favoriteDAO().insert(market);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            binding.btnBookMark.setBackgroundResource(R.drawable.favorite_on);
                            Toast.makeText(context, "관심코인에 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }
}
