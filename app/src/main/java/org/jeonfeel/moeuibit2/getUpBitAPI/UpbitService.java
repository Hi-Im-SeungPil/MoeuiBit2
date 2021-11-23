package org.jeonfeel.moeuibit2.getUpBitAPI;

import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.SelectedCoinModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UpbitService {

    @GET("https://api.upbit.com/v1/ticker")
    Call<List<SelectedCoinModel>> getCoin(@Query("markets") String market);

}
