package org.jeonfeel.moeuibit2.util

import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter :
    ValueFormatter() {
    private var dateHashMap = HashMap<Int, String>()
    override fun getFormattedValue(value: Float): String {
        if (dateHashMap[value.toInt()] == null) {
            return ""
        } else if (dateHashMap[value.toInt()] != null) {
            val fullyDate = dateHashMap[value.toInt()]!!.split("T").toTypedArray()
            val date = fullyDate[0].split("-").toTypedArray()
            val time = fullyDate[1].split(":").toTypedArray()
            return date[1] + "-" + date[2] + " " + time[0] + ":" + time[1]
        }
        return ""
    }
    fun setItem(newDateHashMap: HashMap<Int, String>) {
        this.dateHashMap = newDateHashMap
    }
    fun addItem(newDateString: String, position: Int) {
        this.dateHashMap[position] = newDateString
    }
}