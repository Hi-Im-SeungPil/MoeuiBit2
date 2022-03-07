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
//import org.jeonfeel.moeuibit2.view.activity.MainActivity
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
//import com.github.mikephil.charting.data.PieEntry
//import com.github.mikephil.charting.data.PieDataSet
//import com.github.mikephil.charting.data.PieData
//import android.widget.TextView
//
//class SelectedCoinModel(
//    @field:SerializedName("market") var market: String,
//    @field:SerializedName(
//        "trade_price") var currentPrice: Double,
//    @field:SerializedName("signed_change_rate") var dayToDay: Double,
//    @field:SerializedName(
//        "signed_change_price") var changePrice: Double
//)