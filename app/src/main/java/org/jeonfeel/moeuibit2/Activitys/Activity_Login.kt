//package org.jeonfeel.moeuibit2.Activitys
//
//import android.Manifest
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
//import android.util.Log
//import com.github.mikephil.charting.data.PieEntry
//import com.github.mikephil.charting.data.PieDataSet
//import com.github.mikephil.charting.data.PieData
//import android.widget.TextView
//import androidx.activity.result.ActivityResult
//import org.jeonfeel.moeuibit2.databinding.ActivityLoginBinding
//
//class Activity_Login : AppCompatActivity() {
//    private val TAG = "Activity_Login"
//    private var mAuth: FirebaseAuth? = null
//    private var mGoogleSignInClient: GoogleSignInClient? = null
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        val binding = ActivityLoginBinding.inflate(
//            layoutInflater)
//        setContentView(binding.root)
//        val actionBar = supportActionBar
//        actionBar!!.hide()
//        OnCheckPermission()
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(this.resources.getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
//        mAuth = FirebaseAuth.getInstance()
//        binding.constGoogleLogin.setOnClickListener { signIn() }
//    }
//
//    public override fun onStart() {
//        super.onStart()
//        val currentUser = mAuth!!.currentUser
//        alreadyLogin(currentUser)
//    }
//
//    var resultLauncher = registerForActivityResult(
//        StartActivityForResult()
//    ) { result: ActivityResult ->
//        if (result.resultCode == RESULT_OK) {
//            val data = result.data
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                // Google Sign In was successful, authenticate with Firebase
//                val account = task.getResult(ApiException::class.java)
//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
//                firebaseAuthWithGoogle(account.idToken)
//            } catch (e: ApiException) {
//                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e)
//            }
//        }
//    }
//
//    private fun firebaseAuthWithGoogle(idToken: String) {
//        val credential = GoogleAuthProvider.getCredential(idToken, null)
//        mAuth!!.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    // Sign in success, update UI with the signed-in user's information
//                    Log.d(TAG, "signInWithCredential:success")
//                    val user = mAuth!!.currentUser
//                    Toast.makeText(this@Activity_Login, "로그인 되었습니다.", Toast.LENGTH_SHORT).show()
//                    updateUI(user)
//                } else {
//                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
//                    Toast.makeText(this@Activity_Login, "로그인 실패", Toast.LENGTH_SHORT).show()
//                    updateUI(null)
//                }
//            }
//    }
//
//    private fun signIn() {
//        val signInIntent = mGoogleSignInClient!!.signInIntent
//        resultLauncher.launch(signInIntent)
//    }
//
//    private fun updateUI(user: FirebaseUser?) {
//        if (user != null) {
//            val intent = Intent(this@Activity_Login, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
//
//    private fun alreadyLogin(user: FirebaseUser?) {
//        if (user != null) {
//            val intent = Intent(this@Activity_Login, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
//
//    fun OnCheckPermission() {
//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//            || ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
//        ) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            ) {
//                Toast.makeText(this, "앱 실행을 위해서 반드시 설정해야 합니다", Toast.LENGTH_SHORT).show()
//            }
//            ActivityCompat.requestPermissions(this,
//                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    Manifest.permission.READ_EXTERNAL_STORAGE),
//                PERMISSIONS_REQUEST)
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == PERMISSIONS_REQUEST) {
//            if (grantResults.size > 0
//                && grantResults[0] == PackageManager.PERMISSION_GRANTED
//            ) {
//                Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this, "권한이 없습니다. 앱 설정에서 변경 해 주세요.", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    companion object {
//        const val PERMISSIONS_REQUEST = 0x0000001
//    }
//}