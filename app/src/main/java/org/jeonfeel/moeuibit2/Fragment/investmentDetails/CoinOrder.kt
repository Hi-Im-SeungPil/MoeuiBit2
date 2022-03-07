//package org.jeonfeel.moeuibit2.Fragment.investmentDetails
//
//import org.jeonfeel.moeuibit2.R
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO
//import androidx.annotation.RequiresApi
//import android.os.Build
//import android.graphics.Color
//import android.view.View
//import android.widget.*
//import java.util.*
//
//class CoinOrder constructor(
//    private val btn_investmentDetailOrderByName: Button,
//    private val btn_investmentDetailOrderByYield: Button,
//    private val myCoinsDTO: ArrayList<MyCoinsDTO?>?,
//    currentPrices: ArrayList<Double>?
//) {
//    private var orderByName: Int = 0
//    private var orderByYield: Int = 0
//    val hashMap: HashMap<String, Int>
//    private val currentPrices: ArrayList<Double>?
//    @RequiresApi(api = Build.VERSION_CODES.N)
//    fun setButtons() {
//        if (orderByName == 0 && orderByYield == 0) {
//            btn_investmentDetailOrderByName.setText("이름 ↓↑")
//            btn_investmentDetailOrderByYield.setText("수익률 ↓↑")
//            MyCoinsDTO.orderStatus = "name"
//            Collections.sort(myCoinsDTO)
//            if (hashMap.isEmpty()) {
//                for (i in myCoinsDTO!!.indices) {
//                    val symbol: String = myCoinsDTO.get(i)!!.myCoinsSymbol
//                    hashMap.put("KRW-" + symbol, i)
//                }
//            } else {
//                for (i in myCoinsDTO!!.indices) {
//                    val symbol: String = myCoinsDTO.get(i)!!.myCoinsSymbol
//                    hashMap.replace("KRW-" + symbol, i)
//                }
//            }
//        } else {
//            if (orderByName == 0) {
//                btn_investmentDetailOrderByName.setText("이름 ↓↑")
//            } else if (orderByName == 1) {
//                btn_investmentDetailOrderByName.setText("이름 ↓")
//                MyCoinsDTO.orderStatus = "name"
//                Collections.sort(myCoinsDTO)
//                Collections.reverse(myCoinsDTO)
//                for (i in myCoinsDTO!!.indices) {
//                    val symbol: String = myCoinsDTO.get(i)!!.myCoinsSymbol
//                    hashMap.replace("KRW-" + symbol, i)
//                }
//            } else if (orderByName == 2) {
//                btn_investmentDetailOrderByName.setText("이름 ↑")
//                MyCoinsDTO.orderStatus = "name"
//                Collections.sort(myCoinsDTO)
//                for (i in myCoinsDTO!!.indices) {
//                    val symbol: String = myCoinsDTO.get(i)!!.myCoinsSymbol
//                    hashMap.replace("KRW-" + symbol, i)
//                }
//            }
//            if (orderByYield == 0) {
//                btn_investmentDetailOrderByYield.setText("수익률 ↓↑")
//            } else if (orderByYield == 1) {
//                btn_investmentDetailOrderByYield.setText("수익률 ↓")
//                for (i in myCoinsDTO!!.indices) {
//                    val single: MyCoinsDTO? = myCoinsDTO.get(i)
//                    val position: Int? = hashMap.get("KRW-" + single!!.myCoinsSymbol)
//                    if (position != null) {
//                        single.setCurrentPrice(currentPrices!!.get(position))
//                    }
//                }
//                MyCoinsDTO.orderStatus = "yield"
//                Collections.sort(myCoinsDTO)
//                Collections.reverse(myCoinsDTO)
//                for (i in myCoinsDTO.indices) {
//                    val symbol: String = myCoinsDTO.get(i)!!.myCoinsSymbol
//                    hashMap.replace("KRW-" + symbol, i)
//                }
//            } else if (orderByYield == 2) {
//                btn_investmentDetailOrderByYield.setText("수익률 ↑")
//                for (i in myCoinsDTO!!.indices) {
//                    val single: MyCoinsDTO? = myCoinsDTO.get(i)
//                    val position: Int? = hashMap.get("KRW-" + single!!.myCoinsSymbol)
//                    if (position != null) {
//                        single.setCurrentPrice(currentPrices!!.get(position))
//                    }
//                }
//                MyCoinsDTO.orderStatus = "yield"
//                Collections.sort(myCoinsDTO)
//                for (i in myCoinsDTO.indices) {
//                    val symbol: String = myCoinsDTO.get(i)!!.myCoinsSymbol
//                    hashMap.replace("KRW-" + symbol, i)
//                }
//            }
//        }
//        if (orderByName != 0) {
//            btn_investmentDetailOrderByName.setTextColor(Color.parseColor("#FFFFFFFF"))
//            btn_investmentDetailOrderByName.setBackgroundColor(Color.parseColor("#0F0F5C"))
//        } else if (orderByYield != 0) {
//            btn_investmentDetailOrderByYield.setTextColor(Color.parseColor("#FFFFFFFF"))
//            btn_investmentDetailOrderByYield.setBackgroundColor(Color.parseColor("#0F0F5C"))
//        }
//    }
//
//    internal inner class OrderWays constructor() : View.OnClickListener {
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        public override fun onClick(view: View) {
//            val btns: Array<Button> = arrayOf(
//                btn_investmentDetailOrderByName, btn_investmentDetailOrderByYield)
//            val selected: Int = view.getId()
//            for (i in 0..1) {
//                if (btns.get(i).getId() != selected) {
//                    if (btns.get(i).getId() == R.id.btn_investmentDetailOrderByName) {
//                        btn_investmentDetailOrderByName.setText("이름 ↓↑")
//                        orderByName = 0
//                    } else if (btns.get(i).getId() == R.id.btn_investmentDetailOrderByYield) {
//                        btn_investmentDetailOrderByYield.setText("수익률 ↓↑")
//                        orderByYield = 0
//                    }
//                    btns.get(i).setTextColor(Color.parseColor("#ACABAB"))
//                    btns.get(i).setBackgroundColor(Color.parseColor("#FAFAFA"))
//                } else {
//                    if (R.id.btn_investmentDetailOrderByName == selected) {
//                        if (orderByName != 2) {
//                            orderByName++
//                        } else {
//                            orderByName = 0
//                            btns.get(i).setTextColor(Color.parseColor("#ACABAB"))
//                            btns.get(i).setBackgroundColor(Color.parseColor("#FAFAFA"))
//                        }
//                    } else if (R.id.btn_investmentDetailOrderByYield == selected) {
//                        if (orderByYield != 2) {
//                            orderByYield++
//                        } else {
//                            orderByYield = 0
//                            btns.get(i).setTextColor(Color.parseColor("#ACABAB"))
//                            btns.get(i).setBackgroundColor(Color.parseColor("#FAFAFA"))
//                        }
//                    }
//                }
//            }
//            setButtons()
//        }
//    }
//
//    init {
//        val orderWays: OrderWays = OrderWays()
//        btn_investmentDetailOrderByName.setOnClickListener(orderWays)
//        btn_investmentDetailOrderByYield.setOnClickListener(orderWays)
//        this.currentPrices = currentPrices
//        hashMap = HashMap()
//        MyCoinsDTO.orderStatus = "name"
//        Collections.sort(myCoinsDTO)
//        for (i in myCoinsDTO!!.indices) {
//            val symbol: String = myCoinsDTO.get(i)!!.myCoinsSymbol
//            hashMap.put("KRW-" + symbol, i)
//        }
//    }
//}