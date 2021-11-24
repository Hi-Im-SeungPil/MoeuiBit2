package org.jeonfeel.moeuibit2.getUpBitAPI;

import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.SelectedCoinModel;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Retrofit_UpBit {

    private Retrofit retrofit;

    public Retrofit_UpBit() {

        this.retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl("https://api.upbit.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public retrofit2.Retrofit getRetrofit_upBit() {

        return retrofit;
    }

    public retrofit2.Call<List<SelectedCoinModel>> getSelectedCoinCall(String market) {

        UpbitService upbitService = retrofit.create(UpbitService.class);

        retrofit2.Call<List<SelectedCoinModel>> call = upbitService.getCoin(market);

        return call;
    }
}
