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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.tabs.TabLayout;
import org.jeonfeel.moeuibit2.CheckNetwork;
import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart;
import org.jeonfeel.moeuibit2.Fragment.Fragment_coinInfo;
import org.jeonfeel.moeuibit2.Fragment.coinOrder.Fragment_coinOrder;
import org.jeonfeel.moeuibit2.R;
import org.jeonfeel.moeuibit2.databinding.ActivityCoinDetailsBinding;
import org.jeonfeel.moeuibit2.getUpBitAPI.Retrofit_UpBit;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Activity_coinDetails extends FragmentActivity implements View.OnClickListener {

    private final String TAG = "Activity_coinInfo";

    private ActivityCoinDetailsBinding binding;
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
        binding = ActivityCoinDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        coinDetailsViewModel = new ViewModelProvider(this).get(coinDetailsViewModel.class);

        init();

        coinDetailsViewModel.getSelectedCoinModelLiveData().observe(this, new Observer<SelectedCoinModel>() {
            @Override
            public void onChanged(SelectedCoinModel selectedCoinModel) {
                globalCurrentPrice = coinDetailsViewModel.updateCoinDetails(binding);
            }
        });

        initAds();
        binding.btnBookMark.setOnClickListener(this);
        binding.btnCoinInfoBackSpace.setOnClickListener(this);

        setTabLayout();
    }
    private void init(){
        retrofit = new Retrofit_UpBit();

        Intent intent = getIntent();
        koreanName = intent.getStringExtra("koreanName");
        symbol = intent.getStringExtra("symbol");

        call = retrofit.getSelectedCoinCall("KRW-"+symbol);

        if(CheckNetwork.CheckNetwork(this) == 0){
            Toast.makeText(this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            call.enqueue(new Callback<List<SelectedCoinModel>>() {
                @Override
                public void onResponse(Call<List<SelectedCoinModel>> call, Response<List<SelectedCoinModel>> response) {

                    if(response.isSuccessful()) {
                        List<SelectedCoinModel> selectedCoinModels = response.body();
                        coinDetailsViewModel.getSelectedCoinModelLiveData().setValue(selectedCoinModels.get(0));
                        globalCurrentPrice = coinDetailsViewModel.initCoinDetails(binding, koreanName, symbol);
                    }else{
                        Toast.makeText(Activity_coinDetails.this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                }
                @Override
                public void onFailure(Call<List<SelectedCoinModel>> call, Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(Activity_coinDetails.this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
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
                                if (response.isSuccessful()) {
                                    List<SelectedCoinModel> selectedCoinModels = response.body();
                                    coinDetailsViewModel.getSelectedCoinModelLiveData().setValue(selectedCoinModels.get(0));
                                }else{
                                    Toast.makeText(Activity_coinDetails.this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<List<SelectedCoinModel>> call, Throwable t) {
                                t.printStackTrace();
                                Toast.makeText(Activity_coinDetails.this, "네트워크 상태를 확인해 주세요.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        if( view == binding.btnBookMark ){
            coinDetailsViewModel.updateBookMark(binding,symbol);
        }else if( view == binding.btnCoinInfoBackSpace){
            finish();
        }
    }

    public Double getGlobalCurrentPrice(){
        return this.globalCurrentPrice;
    }

    private void initAds(){

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
            }
        });
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView2.loadAd(adRequest);

    }
}
