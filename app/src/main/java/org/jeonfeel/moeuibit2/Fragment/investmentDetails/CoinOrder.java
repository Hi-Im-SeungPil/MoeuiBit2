package org.jeonfeel.moeuibit2.Fragment.investmentDetails;

import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import org.jeonfeel.moeuibit2.Adapters.Adapter_rvMyCoins;
import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO;
import org.jeonfeel.moeuibit2.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class CoinOrder {
    private final Button btn_investmentDetailOrderByName;
    private final Button btn_investmentDetailOrderByYield;
    private final ArrayList<MyCoinsDTO> myCoinsDTO;
    private int orderByName;
    private int orderByYield;
    private final HashMap<String,Integer> hashMap;
    private final ArrayList<Double> currentPrices;

    public CoinOrder(Button btn_investmentDetailOrderByName, Button btn_investmentDetailOrderByYield,
                     ArrayList<MyCoinsDTO> myCoinsDTO,ArrayList<Double> currentPrices ) {
        this.btn_investmentDetailOrderByName = btn_investmentDetailOrderByName;
        this.btn_investmentDetailOrderByYield = btn_investmentDetailOrderByYield;
        this.myCoinsDTO = myCoinsDTO;

        OrderWays orderWays = new OrderWays();
        this.btn_investmentDetailOrderByName.setOnClickListener(orderWays);
        this.btn_investmentDetailOrderByYield.setOnClickListener(orderWays);
        this.currentPrices = currentPrices;

        hashMap = new HashMap<>();

        MyCoinsDTO.orderStatus = "name";
        Collections.sort(myCoinsDTO);

        for (int i = 0; i < myCoinsDTO.size(); i++) {
            String symbol = myCoinsDTO.get(i).getMyCoinsSymbol();
            hashMap.put("KRW-" + symbol, i);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setButtons(){

            if(orderByName == 0 && orderByYield == 0) {
                btn_investmentDetailOrderByName.setText("이름 ↓↑");
                btn_investmentDetailOrderByYield.setText("수익률 ↓↑");
                MyCoinsDTO.orderStatus = "name";
                Collections.sort(myCoinsDTO);

                if(hashMap.isEmpty()) {
                    for (int i = 0; i < myCoinsDTO.size(); i++) {
                        String symbol = myCoinsDTO.get(i).getMyCoinsSymbol();
                        hashMap.put("KRW-" + symbol, i);
                    }
                }else{
                    for (int i = 0; i < myCoinsDTO.size(); i++) {
                        String symbol = myCoinsDTO.get(i).getMyCoinsSymbol();
                        hashMap.replace("KRW-" + symbol, i);
                    }
                }

            }else {
                if (orderByName == 0){
                    btn_investmentDetailOrderByName.setText("이름 ↓↑");
                }else if (orderByName == 1) {
                    btn_investmentDetailOrderByName.setText("이름 ↓");
                    MyCoinsDTO.orderStatus = "name";
                    Collections.sort(myCoinsDTO);
                    Collections.reverse(myCoinsDTO);

                    for(int i = 0; i < myCoinsDTO.size(); i++){
                        String symbol = myCoinsDTO.get(i).getMyCoinsSymbol();
                        hashMap.replace("KRW-"+symbol,i);
                    }
                }else if(orderByName == 2){
                    btn_investmentDetailOrderByName.setText("이름 ↑");
                    MyCoinsDTO.orderStatus = "name";
                    Collections.sort(myCoinsDTO);

                    for(int i = 0; i < myCoinsDTO.size(); i++){
                        String symbol = myCoinsDTO.get(i).getMyCoinsSymbol();
                        hashMap.replace("KRW-"+symbol,i);
                    }
                }

                if (orderByYield == 0){
                    btn_investmentDetailOrderByYield.setText("수익률 ↓↑");
                }else if (orderByYield == 1) {
                    btn_investmentDetailOrderByYield.setText("수익률 ↓");

                    for (int i = 0; i < myCoinsDTO.size(); i++){
                        MyCoinsDTO single = myCoinsDTO.get(i);
                        Integer position = hashMap.get("KRW-"+single.getMyCoinsSymbol());

                        if(position != null) {
                            single.setCurrentPrice(currentPrices.get(position));
                        }
                    }

                    MyCoinsDTO.orderStatus = "yield";
                    Collections.sort(myCoinsDTO);
                    Collections.reverse(myCoinsDTO);

                    for(int i = 0; i < myCoinsDTO.size(); i++){
                        String symbol = myCoinsDTO.get(i).getMyCoinsSymbol();
                        hashMap.replace("KRW-"+symbol,i);
                    }
                }else if(orderByYield == 2){
                    btn_investmentDetailOrderByYield.setText("수익률 ↑");

                    for (int i = 0; i < myCoinsDTO.size(); i++){
                        MyCoinsDTO single = myCoinsDTO.get(i);
                        Integer position = hashMap.get("KRW-"+single.getMyCoinsSymbol());

                        if(position != null) {
                            single.setCurrentPrice(currentPrices.get(position));
                        }
                    }

                    MyCoinsDTO.orderStatus = "yield";
                    Collections.sort(myCoinsDTO);

                    for(int i = 0; i < myCoinsDTO.size(); i++){
                        String symbol = myCoinsDTO.get(i).getMyCoinsSymbol();
                        hashMap.replace("KRW-"+symbol,i);
                    }
                }
            }

            if(orderByName != 0){
                btn_investmentDetailOrderByName.setTextColor(Color.parseColor("#FFFFFFFF"));
                btn_investmentDetailOrderByName.setBackgroundColor(Color.parseColor("#0F0F5C"));
            }else if(orderByYield != 0){
                btn_investmentDetailOrderByYield.setTextColor(Color.parseColor("#FFFFFFFF"));
                btn_investmentDetailOrderByYield.setBackgroundColor(Color.parseColor("#0F0F5C"));
            }
    }

    public HashMap<String,Integer> getHashMap(){
        return this.hashMap;
    }

    class OrderWays implements View.OnClickListener {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onClick(View view) {
            Button[] btns = {btn_investmentDetailOrderByName,btn_investmentDetailOrderByYield};

            int selected = view.getId();

            for(int i = 0; i < 2; i++){
                if(btns[i].getId() != selected) {
                    if (btns[i].getId() == R.id.btn_investmentDetailOrderByName) {
                        btn_investmentDetailOrderByName.setText("이름 ↓↑");
                        orderByName = 0;
                    }else if (btns[i].getId() == R.id.btn_investmentDetailOrderByYield) {
                        btn_investmentDetailOrderByYield.setText("수익률 ↓↑");
                        orderByYield = 0;
                    }
                    btns[i].setTextColor(Color.parseColor("#ACABAB"));
                    btns[i].setBackgroundColor(Color.parseColor("#FAFAFA"));
                }else{
                    if(R.id.btn_investmentDetailOrderByName == selected){
                        if(orderByName != 2) {
                            orderByName++;
                        }else{
                            orderByName = 0;
                            btns[i].setTextColor(Color.parseColor("#ACABAB"));
                            btns[i].setBackgroundColor(Color.parseColor("#FAFAFA"));
                        }
                    }else if(R.id.btn_investmentDetailOrderByYield == selected){
                        if(orderByYield != 2) {
                            orderByYield++;
                        }else{
                            orderByYield = 0;
                            btns[i].setTextColor(Color.parseColor("#ACABAB"));
                            btns[i].setBackgroundColor(Color.parseColor("#FAFAFA"));
                        }
                    }
                }
            }
            setButtons();
        }
    }
}
