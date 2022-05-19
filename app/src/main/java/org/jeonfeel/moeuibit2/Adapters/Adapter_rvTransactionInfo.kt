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
//import java.text.SimpleDateFormat
//import java.util.*
//
//class Adapter_rvTransactionInfo(var item: MutableList<TransactionInfo>, var context: Context) :
//    RecyclerView.Adapter<Adapter_rvTransactionInfo.CustomViewHolder>() {
//    var decimalFormat = DecimalFormat("###,###")
//    fun setItem(item: List<TransactionInfo>?) {
//        this.item = ArrayList()
//        this.item.addAll(item!!)
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): CustomViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.rv_transaction_info_item, parent, false)
//        return CustomViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        val transactionStatus: String
//        val price = item[position].price
//        val quantity = item[position].quantity
//        if (item[position].transactionStatus == "bid") {
//            transactionStatus = "매수"
//            holder.tv_transactionStatus.setTextColor(Color.parseColor("#B77300"))
//        } else {
//            transactionStatus = "매도"
//            holder.tv_transactionStatus.setTextColor(Color.parseColor("#0054FF"))
//        }
//        holder.tv_transactionStatus.text = transactionStatus
//        holder.tv_transactionMarket.text = item[position].market
//        val date = Date(item[position].transactionTime)
//        val datef = SimpleDateFormat("MM-dd hh:mm", Locale.getDefault())
//        val date22 = datef.format(date)
//        holder.tv_transactionTime.text = date22
//        if (price >= 100) {
//            holder.tv_transactionPrice.text = decimalFormat.format(Math.round(price))
//        } else if (price < 100 && price >= 1) {
//            holder.tv_transactionPrice.text = String.format("%.2f", price)
//        } else {
//            holder.tv_transactionPrice.text = String.format("%.4f", price)
//        }
//        holder.tv_transactionQuantity.text = String.format("%.8f", quantity)
//        holder.tv_transactionAmount.text = decimalFormat.format(Math.round(price * quantity))
//    }
//
//    override fun getItemCount(): Int {
//        return item.size
//    }
//
//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tv_transactionStatus: TextView
//        val tv_transactionMarket: TextView
//        val tv_transactionTime: TextView
//        val tv_transactionPrice: TextView
//        val tv_transactionQuantity: TextView
//        val tv_transactionAmount: TextView
//
//        init {
//            tv_transactionStatus = itemView.findViewById(R.id.tv_transactionStatus)
//            tv_transactionMarket = itemView.findViewById(R.id.tv_transactionMarket)
//            tv_transactionTime = itemView.findViewById(R.id.tv_transactionTime)
//            tv_transactionPrice = itemView.findViewById(R.id.tv_transactionPrice)
//            tv_transactionQuantity = itemView.findViewById(R.id.tv_transactionQuantity)
//            tv_transactionAmount = itemView.findViewById(R.id.tv_transactionAmount)
//        }
//    }
//}