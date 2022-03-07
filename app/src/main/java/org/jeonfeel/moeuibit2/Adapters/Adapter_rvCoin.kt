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
//import android.widget.Filter.FilterResults
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO
//import android.content.DialogInterface
//import android.graphics.Color
//import org.jeonfeel.moeuibit2.DTOS.CoinArcadeDTO
//import android.view.Gravity
//import android.view.View
//import android.widget.*
//import org.jeonfeel.moeuibit2.Database.TransactionInfo
//import java.text.DecimalFormat
//import java.util.ArrayList
//
//class Adapter_rvCoin(private var item: ArrayList<CoinDTO>, context: Context) :
//    RecyclerView.Adapter<Adapter_rvCoin.CustomViewHolder>(), Filterable {
//    private var filteredItem: ArrayList<CoinDTO>
//    private var secondFilteredItem: ArrayList<CoinDTO>? = null
//    private val context: Context
//    private val decimalFormat = DecimalFormat("###,###")
//    private var marketPosition: ArrayList<Int>? = null
//    private var favoriteStatus = false
//    private var customLodingDialog: CustomLodingDialog? = null
//    fun setItem(item: ArrayList<CoinDTO>) {
//        this.item = item
//    }
//
//    override fun onCreateViewHolder(
//        parent: ViewGroup,
//        viewType: Int
//    ): CustomViewHolder {
//        val view = LayoutInflater.from(parent.context)
//            .inflate(R.layout.rv_coin_item, parent, false)
//        return CustomViewHolder(view)
//    }
//
//    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
//        if (filteredItem.size != 0) {
//            val dayToDay = filteredItem[position].dayToDay * 100
//            val currentPrice = filteredItem[position].currentPrice
//            //---------------------------------
//            holder.tv_coinName.text = filteredItem[position].koreanName
//            holder.tv_coinMarket.text = filteredItem[position].symbol + " / KRW"
//            //---------------------------------
//            if (currentPrice >= 100) { //만약 100원보다 가격이 높으면 천단위 콤마
//                val currentPrice1 = Math.round(filteredItem[position].currentPrice)
//                    .toInt()
//                val currentPriceResult = decimalFormat.format(currentPrice1.toLong())
//                holder.tv_currentPrice.text = currentPriceResult + ""
//            } else if (currentPrice >= 1 && currentPrice < 100) {
//                holder.tv_currentPrice.text = String.format("%.2f", currentPrice)
//            } else {
//                holder.tv_currentPrice.text = String.format("%.4f", currentPrice)
//            }
//            //---------------------------------
//            holder.tv_dayToDay.text = String.format("%.2f", dayToDay) + " %" //스트링 포맷으로 소수점 2자리 반올림
//            if (dayToDay > 0) { //어제보다 높으면 주황 낮으면 파랑
//                holder.tv_dayToDay.setTextColor(Color.parseColor("#B77300"))
//                holder.tv_currentPrice.setTextColor(Color.parseColor("#B77300"))
//            } else if (dayToDay < 0) {
//                holder.tv_dayToDay.setTextColor(Color.parseColor("#0054FF"))
//                holder.tv_currentPrice.setTextColor(Color.parseColor("#0054FF"))
//            } else if (String.format("%.2f", dayToDay) == "0.00") { //같으면 검정
//                holder.tv_dayToDay.setTextColor(Color.parseColor("#000000"))
//                holder.tv_currentPrice.setTextColor(Color.parseColor("#000000"))
//            }
//            //---------------------------------
//            val transactionAmount = Math.round(filteredItem[position].transactionAmount * 0.000001)
//                .toInt() //백만단위로 끊기
//            val transactionAmountResult = decimalFormat.format(transactionAmount.toLong())
//            holder.tv_transactionAmount.text = "$transactionAmountResult 백만"
//            //---------------------------------
//            holder.linear_coin.setOnClickListener {
//                val networkStatus = CheckNetwork.CheckNetwork(context)
//                if (networkStatus != 0) {
//                    customLodingDialog = CustomLodingDialog(context)
//                    customLodingDialog!!.show()
//                    val intent = Intent(context, Activity_coinDetails::class.java)
//                    intent.putExtra("market", filteredItem[position].market)
//                    intent.putExtra("symbol", filteredItem[position].symbol)
//                    intent.putExtra("koreanName", filteredItem[position].koreanName)
//                    context.startActivity(intent)
//                    if (customLodingDialog != null && customLodingDialog!!.isShowing) {
//                        customLodingDialog!!.dismiss()
//                    }
//                } else {
//                    Toast.makeText(context, "네트워크 상태를 확인해주세요.", Toast.LENGTH_SHORT).show()
//                }
//            }
//        }
//    }
//
//    fun setMarkets(marketPosition: ArrayList<Int>?) {
//        this.marketPosition = null
//        this.marketPosition = ArrayList()
//        this.marketPosition!!.addAll(marketPosition!!)
//    }
//
//    fun setFavoriteStatus(favoriteStatus: Boolean) {
//        this.favoriteStatus = favoriteStatus
//    }
//
//    override fun getItemCount(): Int {
//        return filteredItem.size
//    }
//
//    //검색을 위한 필터.
//    override fun getFilter(): Filter {
//        return object : Filter() {
//            override fun performFiltering(charSequence: CharSequence): FilterResults {
//                val str = charSequence.toString().toLowerCase()
//                if (str.isEmpty() && !favoriteStatus) {
//                    secondFilteredItem = null
//                    secondFilteredItem = item
//                } else if (favoriteStatus) {
//                    val filteringItem = ArrayList<CoinDTO>()
//                    val sampleItem = ArrayList<CoinDTO>()
//                    for (i in marketPosition!!.indices) {
//                        filteringItem.add(item[marketPosition!![i]])
//                    }
//                    if (!str.isEmpty()) {
//                        for (i in filteringItem) {
//                            if (i.koreanName.contains(str) || i.englishName.toLowerCase()
//                                    .contains(str) || i.symbol.toLowerCase().contains(str)
//                            ) {
//                                sampleItem.add(i)
//                            }
//                        }
//                        secondFilteredItem = null
//                        secondFilteredItem = ArrayList()
//                        secondFilteredItem!!.addAll(sampleItem)
//                    } else {
//                        secondFilteredItem = null
//                        secondFilteredItem = ArrayList()
//                        secondFilteredItem!!.addAll(filteringItem)
//                    }
//                } else {
//                    val filteringItem = ArrayList<CoinDTO>()
//                    for (i in item) {
//                        if (i.koreanName.contains(str) || i.englishName.toLowerCase()
//                                .contains(str) || i.symbol.toLowerCase().contains(str)
//                        ) {
//                            filteringItem.add(i)
//                        }
//                    }
//                    secondFilteredItem = null
//                    secondFilteredItem = ArrayList()
//                    secondFilteredItem!!.addAll(filteringItem)
//                }
//                val filterResults = FilterResults()
//                filterResults.values = secondFilteredItem
//                return filterResults
//            }
//
//            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
//                filteredItem = filterResults.values as ArrayList<CoinDTO>
//                notifyDataSetChanged()
//            }
//        }
//    }
//
//    inner class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var linear_coin: LinearLayout
//        var tv_coinName: TextView
//        var tv_currentPrice: TextView
//        var tv_dayToDay: TextView
//        var tv_transactionAmount: TextView
//        var tv_coinMarket: TextView
//
//        init {
//            linear_coin = itemView.findViewById(R.id.linear_coin)
//            tv_coinName = itemView.findViewById(R.id.tv_coinName)
//            tv_currentPrice = itemView.findViewById(R.id.tv_currentPrice)
//            tv_dayToDay = itemView.findViewById(R.id.tv_dayToDay)
//            tv_transactionAmount = itemView.findViewById(R.id.tv_transactionAmount)
//            tv_coinMarket = itemView.findViewById(R.id.tv_coinMarket)
//        }
//    }
//
//    init {
//        filteredItem = item
//        this.context = context
//    }
//}