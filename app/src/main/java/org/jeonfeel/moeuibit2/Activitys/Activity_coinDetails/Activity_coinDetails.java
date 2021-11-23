package org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import org.jeonfeel.moeuibit2.CheckNetwork;
import org.jeonfeel.moeuibit2.Database.Favorite;
import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart;
import org.jeonfeel.moeuibit2.Fragment.Fragment_coinInfo;
import org.jeonfeel.moeuibit2.Fragment.coinOrder.Fragment_coinOrder;
import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase;
import org.jeonfeel.moeuibit2.R;
import org.jeonfeel.moeuibit2.databinding.ActivityCoinInfoBinding;
import org.jeonfeel.moeuibit2.getUpBitAPI.Retrofit_UpBit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_coinDetails extends FragmentActivity {

    private final String TAG = "Activity_coinInfo";

    private ActivityCoinInfoBinding binding;
    private MoEuiBitDatabase db;
    private coinDetailsViewModel coinDetailsViewModel;
    private String market;
    private Retrofit_UpBit retrofit;
    private retrofit2.Call<List<SelectedCoinModel>> call;
    private String koreanName,symbol;
    private Timer timer;
    private TimerTask TT;
    private Double globalCurrentPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCoinInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        coinDetailsViewModel = new ViewModelProvider(this).get(coinDetailsViewModel.class);
        init();
        coinDetailsViewModel.getSelectedCoinModelLiveData().observe(this, new Observer<SelectedCoinModel>() {
            @Override
            public void onChanged(SelectedCoinModel selectedCoinModel) {
                globalCurrentPrice = coinDetailsViewModel.updateCoinDetails(binding);
            }
        });

        db = MoEuiBitDatabase.getInstance(Activity_coinDetails.this);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView2.loadAd(adRequest);

        setTabLayout();
        setCoinSymbol();
        favoriteInit();
        setBtn_bookMark();

        binding.btnCoinInfoBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
    private void init(){
        retrofit = new Retrofit_UpBit();
        Intent intent = getIntent();
        koreanName = intent.getStringExtra("koreanName");
        symbol = intent.getStringExtra("symbol");
        call = retrofit.getSelectedCoin("KRW-"+symbol);
        if(CheckNetwork.CheckNetwork(this) == 0){
            Toast.makeText(this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            call.enqueue(new Callback<List<SelectedCoinModel>>() {
                @Override
                public void onResponse(Call<List<SelectedCoinModel>> call, Response<List<SelectedCoinModel>> response) {
                    List<SelectedCoinModel> selectedCoinModels = response.body();
                    coinDetailsViewModel.getSelectedCoinModelLiveData().setValue(selectedCoinModels.get(0));
                    globalCurrentPrice = coinDetailsViewModel.initCoinDetails(binding, koreanName, symbol);
                }
                @Override
                public void onFailure(Call<List<SelectedCoinModel>> call, Throwable t) {

                }
            });
        }
    }

    private void setBtn_bookMark(){
        binding.btnBookMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Favorite favorite = db.favoriteDAO().select(market);

                if(favorite != null){
                    db.favoriteDAO().delete(market);
                    binding.btnBookMark.setBackgroundResource(R.drawable.favorite_off);
                    Toast.makeText(Activity_coinDetails.this, "관심코인에서 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    db.favoriteDAO().insert(market);
                    binding.btnBookMark.setBackgroundResource(R.drawable.favorite_on);
                    Toast.makeText(Activity_coinDetails.this, "관심코인에 등록되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void favoriteInit(){
        Favorite favorite = db.favoriteDAO().select(market);

        if(favorite != null){
            binding.btnBookMark.setBackgroundResource(R.drawable.favorite_on);
        }else{
            binding.btnBookMark.setBackgroundResource(R.drawable.favorite_off);
        }
    }

    private void setTabLayout(){

        Intent intent = getIntent();
        market = intent.getStringExtra("market");

        Fragment_coinOrder fragment_coinOrder = new Fragment_coinOrder(market,koreanName,symbol);
        Fragment_chart fragment_chart = new Fragment_chart(market);
        Fragment_coinInfo fragment_coinInfo = new Fragment_coinInfo(market);
        TabLayout tab_coinInfo = findViewById(R.id.tab_coinInfo);

        getSupportFragmentManager().beginTransaction().replace(R.id.coinInfo_fragment_container,fragment_coinOrder).commit();
        tab_coinInfo.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Fragment selected = null;
                int networkStatus = CheckNetwork.CheckNetwork(Activity_coinDetails.this);

                if(position == 0){
                    if(networkStatus != 0) {
                        selected = fragment_coinOrder;
                    }else{
                        Toast.makeText(Activity_coinDetails.this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else if(position == 1){
                    if(networkStatus != 0) {
                        selected = fragment_chart;
                    }else{
                        Toast.makeText(Activity_coinDetails.this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }else if(position == 2){
                    selected = fragment_coinInfo;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.coinInfo_fragment_container,selected).commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setCoinSymbol(){
        String imgUrl = "https://raw.githubusercontent.com/Hi-Im-SeungPil/moeuibitImg/main/coinlogo2/"+symbol+".png";
        Glide.with(Activity_coinDetails.this).load(imgUrl).error(R.drawable.img_not_yet).into(binding.ivCoinLogo);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (timer == null && TT == null) {
            timer = new Timer();

            int networkStatus = CheckNetwork.CheckNetwork(Activity_coinDetails.this);

            if (networkStatus != 0) {

                TT = new TimerTask() {
                    @Override
                    public void run() {
                        call.clone().enqueue(new Callback<List<SelectedCoinModel>>() {
                            @Override
                            public void onResponse(Call<List<SelectedCoinModel>> call, Response<List<SelectedCoinModel>> response) {
                                List<SelectedCoinModel> selectedCoinModels = response.body();
                                coinDetailsViewModel.getSelectedCoinModelLiveData().setValue(selectedCoinModels.get(0));
                            }
                            @Override
                            public void onFailure(Call<List<SelectedCoinModel>> call, Throwable t) {

                            }
                        });
                    }
                };
                timer.schedule(TT,0,1000);
            }else{
                Toast.makeText(Activity_coinDetails.this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onPause() { //사용자와 상호작용 하고 있지 않을 때 api 받아오는거 멈춤
        super.onPause();
        if(timer != null && TT != null){
            timer.cancel();
            timer = null;
            TT = null;
        }
    }

    public Double getGlobalCurrentPrice(){
        return this.globalCurrentPrice;
    }

}
