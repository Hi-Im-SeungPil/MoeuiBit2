//package org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails
//
//import androidx.lifecycle.ViewModelProvider.get
//import androidx.appcompat.app.AppCompatActivity
//import com.google.firebase.auth.FirebaseAuth
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import android.os.Bundle
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import org.jeonfeel.moeuibit2.R
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.firebase.auth.FirebaseUser
//import androidx.activity.result.ActivityResultLauncher
//import android.content.Intent
//import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
//import androidx.activity.result.ActivityResultCallback
//import android.app.Activity
//import com.google.android.gms.auth.api.signin.GoogleSignInAccount
//import com.google.android.gms.common.api.ApiException
//import com.google.firebase.auth.AuthCredential
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.auth.AuthResult
//import android.widget.Toast
//import org.jeonfeel.moeuibit2.view.activity.main.MainActivity
//import androidx.core.app.ActivityCompat
//import android.content.pm.PackageManager
//import org.jeonfeel.moeuibit2.Activitys.Activity_Login
//import com.google.gson.annotations.SerializedName
//import androidx.fragment.app.FragmentActivity
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.coinDetailsViewModel
//import org.jeonfeel.moeuibit2.getUpBitAPI.Retrofit_UpBit
//import org.jeonfeel.moeuibit2.Activitys.Activity_coinDetails.SelectedCoinModel
//import androidx.lifecycle.ViewModelProvider
//import org.jeonfeel.moeuibit2.CheckNetwork
//import org.jeonfeel.moeuibit2.Fragment.coinOrder.Fragment_coinOrder
//import org.jeonfeel.moeuibit2.Fragment.Chart.Fragment_chart
//import org.jeonfeel.moeuibit2.Fragment.Fragment_coinInfo
//import com.google.android.material.tabs.TabLayout
//import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
//import com.google.android.gms.ads.MobileAds
//import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
//import com.google.android.gms.ads.initialization.InitializationStatus
//import android.app.Application
//import android.content.Context
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.MutableLiveData
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase
//import com.bumptech.glide.Glide
//import org.jeonfeel.moeuibit2.Database.Favorite
//import android.os.Looper
//import com.github.mikephil.charting.charts.PieChart
//import android.widget.LinearLayout
//import org.jeonfeel.moeuibit2.Database.MyCoin
//import android.content.res.TypedArray
//import android.graphics.Color
//import android.os.Handler
//import com.github.mikephil.charting.data.PieEntry
//import com.github.mikephil.charting.data.PieDataSet
//import com.github.mikephil.charting.data.PieData
//import android.widget.TextView
//import org.jeonfeel.moeuibit2.databinding.ActivityCoinDetailsBinding
//import java.text.DecimalFormat
//
//class coinDetailsViewModel constructor(application: Application) : AndroidViewModel(application) {
//    private val decimalFormat: DecimalFormat = DecimalFormat("###,###")
//    private var selectedCoinModelLiveData: MutableLiveData<SelectedCoinModel?>? = null
//    private val db: MoEuiBitDatabase
//    private val context: Context
//    fun getSelectedCoinModelLiveData(): MutableLiveData<SelectedCoinModel?> {
//        if (selectedCoinModelLiveData == null) {
//            selectedCoinModelLiveData = MutableLiveData()
//        }
//        return selectedCoinModelLiveData!!
//    }
//
//    fun initCoinDetails(
//        binding: ActivityCoinDetailsBinding?,
//        koreanName: String?,
//        symbol: String?
//    ): Double? {
//        val imgUrl: String =
//            "https://raw.githubusercontent.com/Hi-Im-SeungPil/moeuibitImg/main/coinlogo2/" + symbol + ".png"
//        Glide.with(context).load(imgUrl).error(R.drawable.img_not_yet).into(binding!!.ivCoinLogo)
//        val favorite: Array<Favorite?> = arrayOfNulls(1)
//        Thread(object : Runnable {
//            public override fun run() {
//                favorite.get(0) = db.favoriteDAO().select("KRW-" + symbol)
//            }
//        }).start()
//        if (favorite.get(0) == null) {
//            binding.btnBookMark.setBackgroundResource(R.drawable.favorite_off)
//        } else {
//            binding.btnBookMark.setBackgroundResource(R.drawable.favorite_on)
//        }
//        //---------------------------------------------------------------------------------
//        var currentPrice: Double? = 0.0
//        var dayToDay: Double? = 0.0
//        var changePrice: Double? = 0.0
//        val model: SelectedCoinModel? = selectedCoinModelLiveData!!.getValue()
//        if (model != null) {
//            currentPrice = model.getCurrentPrice()
//            dayToDay = model.getDayToDay()
//            changePrice = model.getChangePrice()
//        }
//        //--------------------------------------------------
//        binding.tvCoinInfoCoinName.setText(koreanName + "( KRW / " + symbol + " )")
//        //--------------------------------------------------
//        if (currentPrice!! >= 100) { //만약 100원보다 가격이 높으면 천단위 콤마
//            val currentPriceResult: String = decimalFormat.format(Math.round((currentPrice)))
//            binding.tvCoinInfoCoinPrice.setText(currentPriceResult)
//        } else if (currentPrice < 100 && currentPrice >= 1) {
//            binding.tvCoinInfoCoinPrice.setText(String.format("%.2f", currentPrice))
//        } else if (currentPrice < 1) {
//            binding.tvCoinInfoCoinPrice.setText(String.format("%.4f", currentPrice))
//        }
//        //--------------------------------------------------
//        binding.tvCoinInfoCoinDayToDay.setText(String.format("%.2f", dayToDay!! * 100) + "%")
//        //--------------------------------------------------
//        if (changePrice!! >= 100) {
//            binding.tvCoinInfoChangePrice.setText("+" + decimalFormat.format(Math.round(
//                (changePrice))))
//        } else if (changePrice <= -100) {
//            binding.tvCoinInfoChangePrice.setText(decimalFormat.format(Math.round((changePrice))))
//        } else if (changePrice < 100 && changePrice >= 1) {
//            binding.tvCoinInfoChangePrice.setText("+" + String.format("%.2f", changePrice))
//        } else if (changePrice <= -1 && changePrice > -100) {
//            binding.tvCoinInfoChangePrice.setText(String.format("%.2f", changePrice))
//        } else if (changePrice > 0 && changePrice < 1) {
//            binding.tvCoinInfoChangePrice.setText("+" + String.format("%.3f", changePrice))
//        } else if (changePrice > -1 && changePrice < 0) {
//            binding.tvCoinInfoChangePrice.setText(String.format("%.3f", changePrice))
//        } else if (changePrice == 0.0) {
//            binding.tvCoinInfoChangePrice.setText("0.00")
//        }
//        if (changePrice > 0) {
//            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#B77300"))
//            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#B77300"))
//            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#B77300"))
//        } else if (changePrice < 0) {
//            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#0054FF"))
//            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#0054FF"))
//            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#0054FF"))
//        } else if (changePrice == 0.0) {
//            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#000000"))
//            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#000000"))
//            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#000000"))
//        }
//        return currentPrice
//    }
//
//    fun updateCoinDetails(binding: ActivityCoinDetailsBinding): Double? {
//        var currentPrice: Double? = 0.0
//        var dayToDay: Double? = 0.0
//        var changePrice: Double? = 0.0
//        val model: SelectedCoinModel? = selectedCoinModelLiveData!!.getValue()
//        if (model != null) {
//            currentPrice = model.getCurrentPrice()
//            dayToDay = model.getDayToDay()
//            changePrice = model.getChangePrice()
//        }
//        if (currentPrice!! >= 100) { //만약 100원보다 가격이 높으면 천단위 콤마
//            val currentPriceResult: String = decimalFormat.format(Math.round((currentPrice)))
//            binding.tvCoinInfoCoinPrice.setText(currentPriceResult)
//        } else if (currentPrice < 100 && currentPrice >= 1) {
//            binding.tvCoinInfoCoinPrice.setText(String.format("%.2f", currentPrice))
//        } else if (currentPrice < 1) {
//            binding.tvCoinInfoCoinPrice.setText(String.format("%.4f", currentPrice))
//        }
//        //--------------------------------------------------
//        binding.tvCoinInfoCoinDayToDay.setText(String.format("%.2f", dayToDay!! * 100) + "%")
//        //--------------------------------------------------
//        if (changePrice!! >= 100) {
//            binding.tvCoinInfoChangePrice.setText("+" + decimalFormat.format(Math.round((changePrice))))
//        } else if (changePrice <= -100) {
//            binding.tvCoinInfoChangePrice.setText(decimalFormat.format(Math.round((changePrice))) + "")
//        } else if (changePrice < 100 && changePrice >= 1) {
//            binding.tvCoinInfoChangePrice.setText("+" + String.format("%.2f", changePrice))
//        } else if (changePrice <= -1 && changePrice > -100) {
//            binding.tvCoinInfoChangePrice.setText(String.format("%.2f", changePrice))
//        } else if (changePrice > 0 && changePrice < 1) {
//            binding.tvCoinInfoChangePrice.setText("+" + String.format("%.3f", changePrice))
//        } else if (changePrice > -1 && changePrice < 0) {
//            binding.tvCoinInfoChangePrice.setText(String.format("%.3f", changePrice))
//        } else if (changePrice == 0.0) {
//            binding.tvCoinInfoChangePrice.setText("0.00")
//        }
//        if (changePrice > 0) {
//            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#B77300"))
//            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#B77300"))
//            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#B77300"))
//        } else if (changePrice < 0) {
//            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#0054FF"))
//            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#0054FF"))
//            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#0054FF"))
//        } else if (changePrice == 0.0) {
//            binding.tvCoinInfoCoinPrice.setTextColor(Color.parseColor("#000000"))
//            binding.tvCoinInfoCoinDayToDay.setTextColor(Color.parseColor("#000000"))
//            binding.tvCoinInfoChangePrice.setTextColor(Color.parseColor("#000000"))
//        }
//        return currentPrice
//    }
//
//    fun updateBookMark(binding: ActivityCoinDetailsBinding?, symbol: String?) {
//        val market: String = "KRW-" + symbol
//        val handler: Handler = Handler(Looper.getMainLooper())
//        Thread(object : Runnable {
//            public override fun run() {
//                val favorite: Favorite? = db.favoriteDAO().select(market)
//                if (favorite != null) {
//                    db.favoriteDAO().delete(market)
//                    handler.post(object : Runnable {
//                        public override fun run() {
//                            binding!!.btnBookMark.setBackgroundResource(R.drawable.favorite_off)
//                            Toast.makeText(context, "관심코인에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    })
//                } else {
//                    db.favoriteDAO().insert(market)
//                    handler.post(object : Runnable {
//                        public override fun run() {
//                            binding!!.btnBookMark.setBackgroundResource(R.drawable.favorite_on)
//                            Toast.makeText(context, "관심코인에 등록되었습니다.", Toast.LENGTH_SHORT).show()
//                        }
//                    })
//                }
//            }
//        }).start()
//    }
//
//    init {
//        context = application.getApplicationContext()
//        db = MoEuiBitDatabase.getInstance(context)
//    }
//}