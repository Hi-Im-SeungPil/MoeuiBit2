//package org.jeonfeel.moeuibit2.Adapters
//
//import android.content.Context
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.Activity_coinDetails.globalCurrentPrice
//import org.jeonfeel.moeuibit2.DTOS.CoinDTO
//import androidx.recyclerview.widget.RecyclerView
//import org.jeonfeel.moeuibit2.CustomLodingDialog
//import android.view.ViewGroup
//import android.view.LayoutInflater
//import org.jeonfeel.moeuibit2.R
//import org.jeonfeel.moeuibit2.CheckNetwork
//import android.content.Intent
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.Activity_coinDetails
//import android.widget.Toast
//import android.widget.Filter.FilterResults
//import android.widget.LinearLayout
//import android.widget.TextView
//import org.jeonfeel.moeuibit2.dtos.MyCoinsDTO
//import android.content.DialogInterface
//import android.graphics.Color
//import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO
//import android.view.Gravity
//import android.view.View
//import org.jeonfeel.moeuibit2.Database.TransactionInfo
//import java.text.DecimalFormat
//import java.util.ArrayList
//
//class Adapter_rvCoinArcade(
//    private var item: ArrayList<CoinArcadeDTO>,
//    private val context: Context,
//    private val openingPrice: Double,
//    private val customLodingDialog: CustomLodingDialog?
//) : RecyclerView.Adapter<Adapter_rvCoinArcade.CustomViewHolder>() {
//    var decimalFormat = DecimalFormat("###,###")
//    fun setItem(item: ArrayList<CoinArcadeDTO>) {
//        this.item = item
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): CustomViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.rv_arcade_item, parent, false)
//        return CustomViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        val integerArcadePrice = Math.round(item[position].coinArcadePrice)
//            .toInt()
//        val integerOpenPrice = Math.round(openingPrice).toInt()
//        val arcadePrice = item[position].coinArcadePrice
//        var dayToDay = 0.0
//        if (integerArcadePrice > 1000000) {
//            holder.tv_coinArcadeDayToDay.visibility = View.GONE
//            holder.tv_coinArcadePrice.gravity = Gravity.RIGHT
//            holder.tv_coinArcadePrice.setPadding(0, 0, 10, 0)
//        }
//
//        //-------------------------------------------------------------------------------------------------
//        if (integerArcadePrice > 100) {
//            holder.tv_coinArcadePrice.text = decimalFormat.format(integerArcadePrice.toLong())
//            dayToDay = ((integerArcadePrice - integerOpenPrice) / openingPrice * 100)
//            holder.tv_coinArcadeDayToDay.text = String.format("%.2f", dayToDay) + "%"
//        } else if (arcadePrice < 100 && arcadePrice >= 1) {
//            holder.tv_coinArcadePrice.text = String.format("%.2f", arcadePrice)
//            dayToDay = ((arcadePrice - openingPrice) / openingPrice * 100)
//            holder.tv_coinArcadeDayToDay.text = String.format("%.2f", dayToDay) + "%"
//        } else if (arcadePrice == 100.0) {
//            holder.tv_coinArcadePrice.text = integerArcadePrice.toString() + ""
//            dayToDay = ((integerArcadePrice - integerOpenPrice) / openingPrice * 100)
//            holder.tv_coinArcadeDayToDay.text = String.format("%.2f", dayToDay) + "%"
//        } else if (arcadePrice < 1) {
//            holder.tv_coinArcadePrice.text = String.format("%.4f", arcadePrice)
//            dayToDay = ((arcadePrice - openingPrice) / openingPrice * 100)
//            holder.tv_coinArcadeDayToDay.text = String.format("%.2f", dayToDay) + "%"
//        }
//        if ((context as Activity_coinDetails).globalCurrentPrice != null) {
//            val currentPrice = context.globalCurrentPrice
//            var intCurrentPrice = 0
//            if (currentPrice!! > 100) {
//                intCurrentPrice = Math.round(currentPrice).toInt()
//                if (intCurrentPrice == integerArcadePrice) {
//                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_border2)
//                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_border3)
//                } else {
//                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_item_border)
//                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_item_border)
//                }
//            } else if (currentPrice >= 1 && currentPrice <= 100) {
//                val price = String.format("%.2f", arcadePrice)
//                val Price2 = String.format("%.2f", currentPrice)
//                if (Price2 == price) {
//                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_border2)
//                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_border3)
//                } else {
//                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_item_border)
//                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_item_border)
//                }
//            } else if (currentPrice < 1) {
//                val price = String.format("%.4f", arcadePrice)
//                val Price2 = String.format("%.4f", currentPrice)
//                if (Price2 == price) {
//                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_border2)
//                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_border3)
//                } else {
//                    holder.linear_arcade1.setBackgroundResource(R.drawable.rv_arcade_item_border)
//                    holder.tv_coinArcadeAmount.setBackgroundResource(R.drawable.rv_arcade_item_border)
//                }
//            }
//        }
//
//        //-------------------------------------------------------------------------------------------------
//        holder.tv_coinArcadeAmount.text = String.format("%.3f", item[position].coinArcadeSize)
//        //-------------------------------------------------------------------------------------------------
//        if (item[position].arcadeStatus == "ask") {
//            holder.linear_wholeItem.setBackgroundColor(Color.parseColor("#330100FF"))
//        } else {
//            holder.linear_wholeItem.setBackgroundColor(Color.parseColor("#33FF0000"))
//        }
//        if (dayToDay > 0) {
//            holder.tv_coinArcadePrice.setTextColor(Color.parseColor("#B77300"))
//            holder.tv_coinArcadeDayToDay.setTextColor(Color.parseColor("#B77300"))
//        } else if (dayToDay < 0) {
//            holder.tv_coinArcadePrice.setTextColor(Color.parseColor("#0054FF"))
//            holder.tv_coinArcadeDayToDay.setTextColor(Color.parseColor("#0054FF"))
//        } else if (String.format("%.2f", dayToDay) == "0.00") {
//            holder.tv_coinArcadePrice.setTextColor(Color.parseColor("#000000"))
//            holder.tv_coinArcadeDayToDay.setTextColor(Color.parseColor("#000000"))
//        }
//        if (customLodingDialog != null && customLodingDialog.isShowing && context.globalCurrentPrice != null) {
//            customLodingDialog.dismiss()
//        }
//
////        holder.linear_wholeItem.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                String arcadePrice = holder.tv_coinArcadePrice.getText().toString();
////
////                if(linear_coinOrder.getVisibility() == View.VISIBLE) {
////
////                    et_orderCoinPrice.setText(arcadePrice);
////                    InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
////                    if (et_orderCoinPrice.isFocused()) {
////                        et_orderCoinPrice.clearFocus();
////                        imm.hideSoftInputFromWindow(et_orderCoinPrice.getWindowToken(), 0);
////                    } else if (et_orderCoinQuantity.isFocused()) {
////                        et_orderCoinQuantity.clearFocus();
////                        imm.hideSoftInputFromWindow(et_orderCoinQuantity.getWindowToken(), 0);
////                    }
////                }else if(linear_coinSell.getVisibility() == View.VISIBLE){
////
////                    et_sellCoinPrice.setText(arcadePrice);
////                    InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
////
////                    if (et_sellCoinPrice.isFocused()) {
////                        et_sellCoinPrice.clearFocus();
////                        imm.hideSoftInputFromWindow(et_sellCoinPrice.getWindowToken(), 0);
////                    } else if (et_sellCoinQuantity.isFocused()) {
////                        et_sellCoinQuantity.clearFocus();
////                        imm.hideSoftInputFromWindow(et_sellCoinQuantity.getWindowToken(), 0);
////                    }
////                }
////            }
////        });
//    }
//
//    override fun getItemCount(): Int {
//        return item.size
//    }
//
//    override fun getItemViewType(position: Int): Int {
//        return position
//    }
//
//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var tv_coinArcadePrice: TextView
//        var tv_coinArcadeAmount: TextView
//        var tv_coinArcadeDayToDay: TextView
//        var linear_wholeItem: LinearLayout
//        var linear_arcade1: LinearLayout
//        protected var linear_wholeItem2: LinearLayout
//
//        init {
//            linear_arcade1 = itemView.findViewById(R.id.linear_arcade1)
//            tv_coinArcadePrice = itemView.findViewById(R.id.tv_coinArcadePrice)
//            tv_coinArcadeAmount = itemView.findViewById(R.id.tv_coinArcadeAmount)
//            tv_coinArcadeDayToDay = itemView.findViewById(R.id.tv_coinArcadeDayToDay)
//            linear_wholeItem = itemView.findViewById(R.id.linear_wholeItem)
//            linear_wholeItem2 = itemView.findViewById(R.id.linear_wholeItem2)
//        }
//    }
//}