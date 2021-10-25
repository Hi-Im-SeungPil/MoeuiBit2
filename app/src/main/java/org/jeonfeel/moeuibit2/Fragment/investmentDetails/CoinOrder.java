package org.jeonfeel.moeuibit2.Fragment.investmentDetails;

import android.graphics.Color;
import android.view.View;
import android.widget.Button;

import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO;
import org.jeonfeel.moeuibit2.R;

import java.util.ArrayList;
import java.util.Collections;


public class CoinOrder {
    Button btn_investmentDetailOrderByName;
    Button btn_investmentDetailOrderByYield;
    ArrayList<MyCoinsDTO> myCoinsDTO;
    int orderByName;
    int orderByYield;

    public CoinOrder(Button btn_investmentDetailOrderByName, Button btn_investmentDetailOrderByYield,ArrayList<MyCoinsDTO> myCoinsDTO) {
        this.btn_investmentDetailOrderByName = btn_investmentDetailOrderByName;
        this.btn_investmentDetailOrderByYield = btn_investmentDetailOrderByYield;
        this.myCoinsDTO = myCoinsDTO;
        OrderWays orderWays = new OrderWays();
        this.btn_investmentDetailOrderByName.setOnClickListener(orderWays);
        this.btn_investmentDetailOrderByYield.setOnClickListener(orderWays);

    }
    public void setButtons(){

            if(orderByName == 0 && orderByYield == 0) {
                btn_investmentDetailOrderByName.setText("이름 ↓↑");
                btn_investmentDetailOrderByYield.setText("수익률 ↓↑");
            }else {
                if (orderByName == 0){
                    btn_investmentDetailOrderByName.setText("이름 ↓↑");
                }else if (orderByName == 1) {
                    btn_investmentDetailOrderByName.setText("이름 ↓");
                    MyCoinsDTO.orderStatus = "name";
                    Collections.sort(myCoinsDTO);
                    Collections.reverse(myCoinsDTO);
                }else if(orderByName == 2){
                    btn_investmentDetailOrderByName.setText("이름 ↑");
                    MyCoinsDTO.orderStatus = "name";
                    Collections.sort(myCoinsDTO);
                }

                if (orderByYield == 0){
                    btn_investmentDetailOrderByYield.setText("수익률 ↓↑");
                }else if (orderByYield == 1) {
                    btn_investmentDetailOrderByYield.setText("수익률 ↓");
                    MyCoinsDTO.orderStatus = "yield";
                    Collections.sort(myCoinsDTO);
                    Collections.reverse(myCoinsDTO);
                }else if(orderByYield == 2){
                    btn_investmentDetailOrderByYield.setText("수익률 ↑");
                    MyCoinsDTO.orderStatus = "yield";
                    Collections.sort(myCoinsDTO);
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

    class OrderWays implements View.OnClickListener {

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
