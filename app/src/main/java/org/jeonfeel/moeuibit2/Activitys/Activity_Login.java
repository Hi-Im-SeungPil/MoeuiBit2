package org.jeonfeel.moeuibit2.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jeonfeel.moeuibit2.CustomLodingDialog;
import org.jeonfeel.moeuibit2.MainActivity;
import org.jeonfeel.moeuibit2.R;


public class Activity_Login extends AppCompatActivity {
    private final String TAG = "Activity_Login";
    private FirebaseAuth mAuth;
    private ConstraintLayout const_googleLogin;
    private GoogleSignInClient mGoogleSignInClient;
    static final int PERMISSIONS_REQUEST = 0x0000001;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        OnCheckPermission();
        FindViewById();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("601646266812-jjnt1dc0o5dfhfdv98o540q2n3hac9c0.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        const_googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

//        UserApiClient.getInstance().me((user, meError) -> {
//            if (meError != null) {
//                setBtn_kakaoLogin();
//            } else {
//                Log.i(TAG, "사용자 정보 요청 성공");
//                Intent intent = new Intent(Activity_Login.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//            return null;
//        });
    }

    private void FindViewById(){
        const_googleLogin = findViewById(R.id.const_googleLogin);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        alreadyLogin(currentUser);
    }
    // [END on_start_check_user]

    // [START onactivityresult]
    public ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if(result.getResultCode() == Activity_Login.RESULT_OK){

                    Intent data = result.getData();

                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    try {
                        // Google Sign In was successful, authenticate with Firebase
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                        firebaseAuthWithGoogle(account.getIdToken());
                    } catch (ApiException e) {
                        // Google Sign In failed, update UI appropriately
                        Log.w(TAG, "Google sign in failed", e);
                    }
                }
            }
    );

    // [END onactivityresult]

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(Activity_Login.this, "로그인 되었습니다.", Toast.LENGTH_SHORT).show();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(Activity_Login.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }
    // [END auth_with_google]

    // [START signin]
    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();

        resultLauncher.launch(signInIntent);
    }

    // [END signin]

    private void updateUI(FirebaseUser user) {

        if(user != null) {

            Intent intent = new Intent(Activity_Login.this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }

    private void alreadyLogin(FirebaseUser user){
        if(user != null) {
            Intent intent = new Intent(Activity_Login.this, MainActivity.class);
            startActivity(intent);

            finish();
        }
    }

    public void OnCheckPermission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                Toast.makeText(this, "앱 실행을 위해서 반드시 설정해야 합니다", Toast.LENGTH_SHORT).show();

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},

                        PERMISSIONS_REQUEST);

            } else {

                ActivityCompat.requestPermissions(this,

                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},

                        PERMISSIONS_REQUEST);

            }
        }
    }


    @Override

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {

            case PERMISSIONS_REQUEST:

                if (grantResults.length > 0

                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "앱 실행을 위한 권한이 설정 되었습니다", Toast.LENGTH_SHORT).show();


                } else {

                    Toast.makeText(this, "권한이 없습니다. 앱 설정에서 변경 해 주세요.", Toast.LENGTH_SHORT).show();

                }

                break;

        }
    }

    //카카오 로그인 매서드
//    private void setBtn_kakaoLogin() {
//        Button btn_kakaoLogin = findViewById(R.id.btn_kakaoLogin);
//
//        btn_kakaoLogin.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(Activity_Login.this)) {
//                    UserApiClient.getInstance().loginWithKakaoTalk(Activity_Login.this, (oAuthToken, error) -> {
//                        if (error != null) {
//                            Log.e(TAG, "로그인 실패", error);
//                        } else if (oAuthToken != null) {
//                            Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
//                            setUserInfo();
//                        }
//                        return null;
//                    });
//                } else {
//                    UserApiClient.getInstance().loginWithKakaoAccount(Activity_Login.this, (oAuthToken, error) -> {
//                        if (error != null) {
//                            Log.e(TAG, "로그인 실패", error);
//                        } else if (oAuthToken != null) {
//                            Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
//                            setUserInfo();
//                        }
//                        return null;
//                    });
//                }
//            }
//        });
//    }
//
//    // 유저 정보 firebase에 저장
//    private void setUserInfo() {
//        UserApiClient.getInstance().me((user, meError) -> {
//
//            if (meError != null) {
//                Log.e(TAG, "사용자 정보 요청 실패", meError);
//            } else {
//                Log.i(TAG, "사용자 정보 요청 성공");
//                long userId = user.getId();
//                String userEmail = user.getKakaoAccount().getEmail();
//
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                DatabaseReference defaultRef = database.getReference();
//
//                defaultRef.child("user").child(String.valueOf(userId)).setValue(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) { // 정보저장 성공한다면
//                        Toast.makeText(Activity_Login.this, "유저 정보 저장 성공!", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(Activity_Login.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                }).addOnFailureListener(new OnFailureListener() { // 정보저장 실패 한다면
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        Toast.makeText(Activity_Login.this, "유저 정보 저장 실패", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//            return null;
//        });
//    }
}
