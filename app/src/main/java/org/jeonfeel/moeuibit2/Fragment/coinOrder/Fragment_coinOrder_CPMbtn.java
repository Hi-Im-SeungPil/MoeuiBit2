package org.jeonfeel.moeuibit2.Fragment.coinOrder;

import static java.lang.Math.abs;
import static java.lang.Math.round;

public class Fragment_coinOrder_CPMbtn {

//    Button btn_currentPricePlus,btn_currentPriceMinus,btn_currentPrice;
//    EditText et_price;
//    int plusPercent, minusPercent;
//    DecimalFormat decimalFormat = new DecimalFormat("###,###");
//    Context context;
//
//    public Fragment_coinOrder_CPMbtn(Button btn_currentPricePlus, Button btn_currentPriceMinus, Button btn_currentPrice,EditText et_price,Context context) {
//        this.btn_currentPricePlus = btn_currentPricePlus;
//        this.btn_currentPriceMinus = btn_currentPriceMinus;
//        this.btn_currentPrice = btn_currentPrice;
//        this.et_price = et_price;
//        this.plusPercent = 5;
//        this.minusPercent = -5;
//        this.context = context;
//        this.btn_currentPricePlus.setText(plusPercent + "%");
//        this.btn_currentPriceMinus.setText(minusPercent + "%");
//    }
//
//    public void setBtn_currentPricePlus(){
//        btn_currentPricePlus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Double currentPrice = Activity_coinInfo.globalCurrentPrice;
//
//                if (Activity_coinInfo.globalCurrentPrice != null) {
//
//                    Double price = plusPercent * 0.01 * currentPrice + currentPrice;
//
//                    if (plusPercent <= 100) {
//
//                        if (price >= 100) {
//                            et_price.setText(decimalFormat.format(round(price)));
//                        } else if(price >= 10 && price < 100){
//                            et_price.setText(String.format("%.1f", price));
//                        }else{
//                            et_price.setText(String.format("%.2f", price));
//                        }
//
//                        plusPercent += 5;
//                        minusPercent += 5;
//
//                        btn_currentPricePlus.setText(plusPercent + "%");
//                        btn_currentPriceMinus.setText(minusPercent + "%");
//                    } else {
//                        Toast.makeText(context, "100% 초과할 수 없습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//    }
//
//    public void setBtn_currentPriceMinus(){
//
//
//
//        btn_currentPriceMinus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Double currentPrice = Activity_coinInfo.globalCurrentPrice;
//
//                if (Activity_coinInfo.globalCurrentPrice != null) {
//
//                    Double price = minusPercent * 0.01 * currentPrice + currentPrice;
//
//                    if (minusPercent >= -95) {
//                        if (price >= 100) {
//                            et_price.setText(decimalFormat.format(round(price)));
//                        } else if(price >= 10 && price < 100){
//                            et_price.setText(String.format("%.1f", price));
//                        }else {
//                            et_price.setText(String.format("%.2f", price));
//                        }
//
//                        minusPercent -= 5;
//                        plusPercent -= 5;
//
//                        btn_currentPricePlus.setText(plusPercent + "%");
//                        btn_currentPriceMinus.setText(minusPercent + "%");
//
//                    } else {
//                        Toast.makeText(context, "-95% 초과할 수 없습니다.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//    }
//
//    public void setBtn_currentPrice(){
//
//
//
//        btn_currentPrice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Double currentPrice = Activity_coinInfo.globalCurrentPrice;
//
//                if (Activity_coinInfo.globalCurrentPrice != null) {
//
//                    plusPercent = 5;
//                    minusPercent = -5;
//
//                    if (currentPrice >= 100) {
//                        et_price.setText(decimalFormat.format(currentPrice));
//                    } else {
//                        et_price.setText(String.format("%.2f", currentPrice));
//                    }
//
//                    btn_currentPricePlus.setText(plusPercent + "%");
//                    btn_currentPriceMinus.setText(minusPercent + "%");
//
//                }
//            }
//        });
//    }
//
//    public void reset(){
//
//        this.plusPercent = 5;
//        this.minusPercent = -5;
//
//        btn_currentPricePlus.setText(plusPercent + "%");
//        btn_currentPriceMinus.setText(minusPercent + "%");
//
//    }

}
