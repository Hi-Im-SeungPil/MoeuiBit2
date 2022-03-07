package org.jeonfeel.moeuibit2.Fragment


import androidx.fragment.app.Fragment

class ExchangeFragment : Fragment() {

    //정렬방식 class
//    internal inner class OrderWays constructor() : View.OnClickListener {
//        public override fun onClick(view: View) {
//            val btns: Array<Button?> =
//                arrayOf(btn_orderByCurrentPrice, btn_orderByDayToDay, btn_orderByTransactionAmount)
//            val selected: Int = view.getId()
//            for (i in 0..2) {
//                if (btns.get(i)!!.getId() != selected) {
//                    if (btns.get(i)!!.getId() == R.id.btn_orderByCurrentPrice) {
//                        btn_orderByCurrentPrice!!.setText("현재가↓↑")
//                        orderByCurrentPrice = 0
//                    } else if (btns.get(i)!!.getId() == R.id.btn_orderByTransactionAmount) {
//                        btn_orderByTransactionAmount!!.setText("거래대금↓↑")
//                        orderByTransactionAmount = 0
//                    } else if (btns.get(i)!!.getId() == R.id.btn_orderByDayToDay) {
//                        btn_orderByDayToDay!!.setText("전일대비↓↑")
//                        orderByDayToDay = 0
//                    }
//                    btns.get(i)!!.setTextColor(Color.parseColor("#ACABAB"))
//                    btns.get(i)!!.setBackgroundColor(Color.parseColor("#FAFAFA"))
//                } else {
//                    if (R.id.btn_orderByCurrentPrice == selected) {
//                        if (orderByCurrentPrice != 2) {
//                            orderByCurrentPrice++
//                        } else {
//                            orderByCurrentPrice = 0
//                            btns.get(i)!!.setTextColor(Color.parseColor("#ACABAB"))
//                            btns.get(i)!!.setBackgroundColor(Color.parseColor("#FAFAFA"))
//                        }
//                    } else if (R.id.btn_orderByTransactionAmount == selected) {
//                        if (orderByTransactionAmount != 2) {
//                            orderByTransactionAmount++
//                        } else {
//                            orderByTransactionAmount = 0
//                            btns.get(i)!!.setTextColor(Color.parseColor("#ACABAB"))
//                            btns.get(i)!!.setBackgroundColor(Color.parseColor("#FAFAFA"))
//                        }
//                    } else if (R.id.btn_orderByDayToDay == selected) {
//                        if (orderByDayToDay != 2) {
//                            orderByDayToDay++
//                        } else {
//                            orderByDayToDay = 0
//                            btns.get(i)!!.setTextColor(Color.parseColor("#ACABAB"))
//                            btns.get(i)!!.setBackgroundColor(Color.parseColor("#FAFAFA"))
//                        }
//                    }
//                }
//            }
//            orderByCoins()
//            orderByFavoriteCoins()
//        }
//    }
}