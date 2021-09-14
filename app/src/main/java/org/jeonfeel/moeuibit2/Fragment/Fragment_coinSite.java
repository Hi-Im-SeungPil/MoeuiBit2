package org.jeonfeel.moeuibit2.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.jeonfeel.moeuibit2.R;

public class Fragment_coinSite extends Fragment {

    LinearLayout linear_ddangle, linear_coinPan, linear_moneyNet, linear_cobak, linear_blockChanHub,
            linear_dcInside, linear_bitMan, linear_FMkorea, linear_coinMarketCap, linear_coinGeko,
            linear_kimpga, linear_cryPrice, linear_coDal, linear_coinNess, linear_coinMarketCal,linear_xangle,
            linear_binance,linear_gopax,linear_bybit,linear_upBit,linear_bithumb,linear_coinOne,linear_musk;
    LinearLayout linears_group1,linears_group2,linears_group3,linears_group4,linears_group5,linears_group6,linears_group7,linears_group8;
    Button btn_hide1,btn_hide2,btn_hide3,btn_hide4,btn_hide5;
    Context context;

    public Fragment_coinSite(Context context) {
        this.context = context;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_coin_site, container, false);

        FindViewById(rootView);
        setIv_exchanges();
        setLinear();
        setBtns();

        return rootView;
    }

    private void FindViewById(View rootView) {

        linear_upBit = rootView.findViewById(R.id.linear_upBit);
        linear_bithumb = rootView.findViewById(R.id.linear_bithumb);
        linear_coinOne = rootView.findViewById(R.id.linear_coinOne);
        linear_binance = rootView.findViewById(R.id.linear_binance);
        linear_gopax = rootView.findViewById(R.id.linear_gopax);
        linear_bybit = rootView.findViewById(R.id.linear_bybit);
        linear_ddangle = rootView.findViewById(R.id.linear_ddangle);
        linear_coinPan = rootView.findViewById(R.id.linear_coinPan);
        linear_moneyNet = rootView.findViewById(R.id.linear_moneyNet);
        linear_cobak = rootView.findViewById(R.id.linear_cobak);
        linear_blockChanHub = rootView.findViewById(R.id.linear_blockChanHub);
        linear_dcInside = rootView.findViewById(R.id.linear_dcInside);
        linear_bitMan = rootView.findViewById(R.id.linear_bitMan);
        linear_FMkorea = rootView.findViewById(R.id.linear_FMkorea);
        linear_coinMarketCap = rootView.findViewById(R.id.linear_coinMarketCap);
        linear_coinGeko = rootView.findViewById(R.id.linear_coinGeko);
        linear_xangle = rootView.findViewById(R.id.linear_xangle);
        linear_kimpga = rootView.findViewById(R.id.linear_kimpga);
        linear_cryPrice = rootView.findViewById(R.id.linear_cryPrice);
        linear_coDal = rootView.findViewById(R.id.linear_coDal);
        linear_coinNess = rootView.findViewById(R.id.linear_coinNess);
        linear_coinMarketCal = rootView.findViewById(R.id.linear_coinMarketCal);
        linear_musk = rootView.findViewById(R.id.linear_musk);
        linears_group1 = rootView.findViewById(R.id.linears_group1);
        linears_group2 = rootView.findViewById(R.id.linears_group2);
        linears_group3 = rootView.findViewById(R.id.linears_group3);
        linears_group4 = rootView.findViewById(R.id.linears_group4);
        linears_group5 = rootView.findViewById(R.id.linears_group5);
        linears_group6 = rootView.findViewById(R.id.linears_group6);
        linears_group7 = rootView.findViewById(R.id.linears_group7);
        linears_group8 = rootView.findViewById(R.id.linears_group8);
        btn_hide1 = rootView.findViewById(R.id.btn_hide1);
        btn_hide2 = rootView.findViewById(R.id.btn_hide2);
        btn_hide3 = rootView.findViewById(R.id.btn_hide3);
        btn_hide4 = rootView.findViewById(R.id.btn_hide4);
        btn_hide5 = rootView.findViewById(R.id.btn_hide5);
    }

    private void setIv_exchanges(){
        linear_upBit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchangesIsInstall("com.dunamu.exchange","https://www.upbit.com");
            }
        });

        linear_bithumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchangesIsInstall("com.btckorea.bithumb","https://www.bithumb.com/");
            }
        });
        linear_coinOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchangesIsInstall("coinone.co.kr.official","https://coinone.co.kr/");
            }
        });
        linear_binance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchangesIsInstall("com.binance.dev","https://www.binance.com/en/");
            }
        });
        linear_gopax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchangesIsInstall("kr.co.gopax","https://www.gopax.co.kr/");
            }
        });
        linear_bybit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exchangesIsInstall("com.bybit.app","https://www.bybit.com/en-US/");
            }
        });
    }
    private void exchangesIsInstall(String pakageName, String uri){

        Intent intent = context.getPackageManager().getLaunchIntentForPackage(pakageName);

        if(intent == null) {
            //미설치
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        } else {
            //설치
            startActivity(intent);
        }
    }

    private void setLinear(){
        LinearLayout[] linearLayouts = {linear_ddangle,linear_coinPan,linear_moneyNet,linear_cobak,linear_blockChanHub,
                linear_dcInside,linear_bitMan,linear_FMkorea,linear_FMkorea,linear_coinMarketCap,linear_coinGeko,
                linear_xangle,linear_kimpga,linear_cryPrice,linear_coDal,linear_coinNess,linear_coinMarketCal,linear_musk};

        SetLinears setLinears = new SetLinears();

        for(int i = 0; i < linearLayouts.length; i++){
            linearLayouts[i].setOnClickListener(setLinears);
        }
    }
    private void setBtns(){
        btn_hide1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linears_group1.getVisibility() == View.VISIBLE){
                    btn_hide1.setText("▼");
                    linears_group1.setVisibility(View.GONE);
                    linears_group8.setVisibility(View.GONE);
                }else{
                    btn_hide1.setText("▲");
                    linears_group1.setVisibility(View.VISIBLE);
                    linears_group8.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_hide2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linears_group2.getVisibility() == View.VISIBLE){
                    btn_hide2.setText("▼");
                    linears_group2.setVisibility(View.GONE);
                    linears_group3.setVisibility(View.GONE);
                    linears_group4.setVisibility(View.GONE);
                }else{
                    btn_hide2.setText("▲");
                    linears_group2.setVisibility(View.VISIBLE);
                    linears_group3.setVisibility(View.VISIBLE);
                    linears_group4.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_hide3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linears_group5.getVisibility() == View.VISIBLE){
                    btn_hide3.setText("▼");
                    linears_group5.setVisibility(View.GONE);
                }else{
                    btn_hide3.setText("▲");
                    linears_group5.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_hide4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linears_group6.getVisibility() == View.VISIBLE){
                    btn_hide4.setText("▼");
                    linears_group6.setVisibility(View.GONE);
                }else{
                    btn_hide4.setText("▲");
                    linears_group6.setVisibility(View.VISIBLE);
                }
            }
        });
        btn_hide5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(linears_group7.getVisibility() == View.VISIBLE){
                    btn_hide5.setText("▼");
                    linears_group7.setVisibility(View.GONE);
                }else{
                    btn_hide5.setText("▲");
                    linears_group7.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    class SetLinears implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            String uri = "";
            switch (view.getId()){
                case R.id.linear_ddangle:
                    uri = "https://www.ddengle.com/";
                    break;
                case R.id.linear_coinPan:
                    uri = "https://coinpan.com/";
                    break;
                case R.id.linear_moneyNet:
                    uri = "https://www.moneynet.co.kr/";
                    break;
                case R.id.linear_cobak:
                    uri = "https://cobak.co.kr/";
                    break;
                case R.id.linear_blockChanHub:
                    uri = "https://blockchainhub.kr/";
                    break;
                case R.id.linear_dcInside:
                    uri = "https://gall.dcinside.com/board/lists/?id=bitcoins";
                    break;
                case R.id.linear_bitMan:
                    uri = "https://cafe.naver.com/nexontv";
                    break;
                case R.id.linear_FMkorea:
                    uri = "https://www.fmkorea.com/coin";
                    break;
                case R.id.linear_musk:
                    uri = "https://twitter.com/elonmusk";
                    break;
                case R.id.linear_coinMarketCap:
                    uri = "https://coinmarketcap.com/ko/";
                    break;
                case R.id.linear_coinGeko:
                    uri = "https://www.coingecko.com/ko";
                    break;
                case R.id.linear_xangle:
                    uri = "https://xangle.io/";
                    break;
                case R.id.linear_kimpga:
                    uri = "https://kimpga.com/";
                    break;
                case R.id.linear_cryPrice:
                    uri = "https://scolkg.com/";
                    break;
                case R.id.linear_coDal:
                    uri = "https://www.coindalin.com/";
                    break;
                case R.id.linear_coinNess:
                    uri = "https://kr.coinness.com/";
                    break;
                case R.id.linear_coinMarketCal:
                    uri = "https://coinmarketcal.com/en/";
                    break;
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        }
    }
}