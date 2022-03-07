//package org.jeonfeel.moeuibit2.Adapters
//
//import android.app.AlertDialog
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
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO
//import android.content.DialogInterface
//import android.graphics.Color
//import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO
//import android.view.Gravity
//import android.view.View
//import org.jeonfeel.moeuibit2.Database.TransactionInfo
//import java.text.DecimalFormat
//import java.util.ArrayList
//
//class Adapter_rvMyCoins(private var item: ArrayList<MyCoinsDTO>, private val context: Context) :
//    RecyclerView.Adapter<Adapter_rvMyCoins.CustomViewHolder>() {
//    private var currentPrices: ArrayList<Double>? = null
//    private val decimalFormat = DecimalFormat("###,###")
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): CustomViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.rv_my_coins_item, parent, false)
//        return CustomViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        val symbol = item[position].myCoinsSymbol
//        val koreanName = item[position].myCoinsKoreanName
//        val buyingAverage = item[position].myCoinsBuyingAverage
//        val quantity = item[position].myCoinsQuantity
//        val purchaseAmount = Math.round(quantity * buyingAverage)
//        holder.tv_myCoinsSymbol.text = symbol
//        holder.tv_myCoinsSymbol2.text = symbol
//        holder.tv_myCoinsKoreanName.text = koreanName
//        holder.tv_myCoinsQuantity.text = quantity.toString()
//        if (buyingAverage >= 100) {
//            val buyingAverage1 = Math.round(buyingAverage)
//            holder.tv_myCoinsBuyingAverage.text = decimalFormat.format(buyingAverage1)
//        } else if (buyingAverage < 100 && buyingAverage >= 1) {
//            holder.tv_myCoinsBuyingAverage.text = String.format("%.2f", buyingAverage)
//        } else {
//            holder.tv_myCoinsBuyingAverage.text = String.format("%.4f", buyingAverage)
//        }
//        holder.tv_myCoinsPurchaseAmount.text = decimalFormat.format(purchaseAmount)
//        if (currentPrices != null) {
//            val currentPrice = currentPrices!![position]
//            val evaluationAmount = Math.round(quantity * currentPrice)
//            val lossGainAmount = evaluationAmount - purchaseAmount
//            val lossGainCoin = currentPrice - buyingAverage
//            val earningRate = lossGainCoin / buyingAverage * 100
//            if (lossGainAmount > 0) {
//                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#B77300"))
//                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#B77300"))
//                holder.tv_myCoinsDifference.setTextColor(Color.parseColor("#B77300"))
//            } else if (lossGainAmount < 0) {
//                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#0054FF"))
//                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#0054FF"))
//                holder.tv_myCoinsDifference.setTextColor(Color.parseColor("#0054FF"))
//            } else {
//                holder.tv_myCoinsEvaluation.setTextColor(Color.parseColor("#000000"))
//                holder.tv_myCoinsEarningsRate.setTextColor(Color.parseColor("#000000"))
//                holder.tv_myCoinsDifference.setTextColor(Color.parseColor("#000000"))
//            }
//            if (currentPrice >= 100) {
//                holder.tv_myCoinsCurrentPrice.text = decimalFormat.format(Math.round(currentPrice))
//            } else if (buyingAverage < 100 && buyingAverage >= 1) {
//                holder.tv_myCoinsCurrentPrice.text = String.format("%.2f", currentPrice)
//            } else {
//                holder.tv_myCoinsCurrentPrice.text = String.format("%.4f", currentPrice)
//            }
//            if (lossGainCoin >= 100 || lossGainCoin <= -100) {
//                val text = Math.round(lossGainCoin)
//                holder.tv_myCoinsDifference.text = decimalFormat.format(text)
//            } else if (lossGainCoin < 100 && lossGainCoin >= 1) {
//                holder.tv_myCoinsDifference.text = String.format("%.2f", lossGainCoin)
//            } else if (lossGainCoin > -100 && lossGainCoin <= -1) {
//                holder.tv_myCoinsDifference.text = String.format("%.2f", lossGainCoin)
//            } else {
//                holder.tv_myCoinsDifference.text = String.format("%.4f", lossGainCoin)
//            }
//            holder.tv_myCoinsEvaluationAmount.text = decimalFormat.format(evaluationAmount)
//            holder.tv_myCoinsEvaluation.text = decimalFormat.format(lossGainAmount)
//            holder.tv_myCoinsEarningsRate.text = String.format("%.2f", earningRate) + "%"
//        }
//        holder.linear_myCoinsWholeItem.setOnClickListener {
//            val builder = AlertDialog.Builder(
//                context)
//            builder.setTitle("거래").setMessage(item[position].myCoinsKoreanName + " 거래하기")
//                .setPositiveButton("이동") { dialogInterface, i ->
//                    if (CheckNetwork.CheckNetwork(context) != 0) {
//                        val intent = Intent(context, Activity_coinDetails::class.java)
//                        intent.putExtra("koreanName", koreanName)
//                        intent.putExtra("symbol", symbol)
//                        intent.putExtra("market", "KRW-$symbol")
//                        context.startActivity(intent)
//                    } else {
//                        Toast.makeText(context, "네트워크 연결을 확인해 주세요.", Toast.LENGTH_SHORT).show()
//                    }
//                }
//                .setNegativeButton("취소") { dialogInterface, i -> }
//            val alertDialog = builder.create()
//            alertDialog.show()
//        }
//    }
//
//    fun setCurrentPrices(currentPrices: ArrayList<Double>?) {
//        this.currentPrices = currentPrices
//    }
//
//    fun setItem(item: ArrayList<MyCoinsDTO>) {
//        this.item = item
//    }
//
//    override fun getItemCount(): Int {
//        return item.size
//    }
//
//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        val tv_myCoinsKoreanName: TextView
//        val tv_myCoinsSymbol: TextView
//        val tv_myCoinsEvaluation: TextView
//        val tv_myCoinsEarningsRate: TextView
//        val tv_myCoinsQuantity: TextView
//        val tv_myCoinsSymbol2: TextView
//        val tv_myCoinsBuyingAverage: TextView
//        val tv_myCoinsEvaluationAmount: TextView
//        val tv_myCoinsPurchaseAmount: TextView
//        val tv_myCoinsCurrentPrice: TextView
//        val tv_myCoinsDifference: TextView
//        val linear_myCoinsWholeItem: LinearLayout
//
//        init {
//            tv_myCoinsKoreanName = itemView.findViewById(R.id.tv_myCoinsKoreanName)
//            tv_myCoinsSymbol = itemView.findViewById(R.id.tv_myCoinsSymbol)
//            tv_myCoinsEvaluation = itemView.findViewById(R.id.tv_myCoinsEvaluation)
//            tv_myCoinsEarningsRate = itemView.findViewById(R.id.tv_myCoinsEarningsRate)
//            tv_myCoinsQuantity = itemView.findViewById(R.id.tv_myCoinsQuantity)
//            tv_myCoinsSymbol2 = itemView.findViewById(R.id.tv_myCoinsSymbol2)
//            tv_myCoinsBuyingAverage = itemView.findViewById(R.id.tv_myCoinsBuyingAverage)
//            tv_myCoinsEvaluationAmount = itemView.findViewById(R.id.tv_myCoinsEvaluationAmount)
//            tv_myCoinsPurchaseAmount = itemView.findViewById(R.id.tv_myCoinsPurchaseAmount)
//            tv_myCoinsCurrentPrice = itemView.findViewById(R.id.tv_myCoinsCurrentPrice)
//            tv_myCoinsDifference = itemView.findViewById(R.id.tv_myCoinsDifference)
//            linear_myCoinsWholeItem = itemView.findViewById(R.id.linear_myCoinsWholeItem)
//        }
//    }
//}