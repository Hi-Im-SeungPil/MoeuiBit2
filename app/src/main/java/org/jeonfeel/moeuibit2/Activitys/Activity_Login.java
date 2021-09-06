package org.jeonfeel.moeuibit2.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kakao.sdk.user.UserApiClient;

import org.jeonfeel.moeuibit2.MainActivity;
import org.jeonfeel.moeuibit2.R;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Activity_Login extends AppCompatActivity {
    private final String TAG = "Activity_Login";
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mAuth = FirebaseAuth.getInstance();

        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                setBtn_kakaoLogin();
            } else {
                Log.i(TAG, "사용자 정보 요청 성공");
                Intent intent = new Intent(Activity_Login.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
            return null;
        });

    }

    //카카오 로그인 매서드
    private void setBtn_kakaoLogin() {
        Button btn_kakaoLogin = findViewById(R.id.btn_kakaoLogin);

        btn_kakaoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(Activity_Login.this)) {
                    UserApiClient.getInstance().loginWithKakaoTalk(Activity_Login.this, (oAuthToken, error) -> {
                        if (error != null) {
                            Log.e(TAG, "로그인 실패", error);
                        } else if (oAuthToken != null) {
                            Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                            setUserInfo();
                        }
                        return null;
                    });
                } else {
                    UserApiClient.getInstance().loginWithKakaoAccount(Activity_Login.this, (oAuthToken, error) -> {
                        if (error != null) {
                            Log.e(TAG, "로그인 실패", error);
                        } else if (oAuthToken != null) {
                            Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                            setUserInfo();
                        }
                        return null;
                    });
                }
            }
        });
    }

    // 유저 정보 firebase에 저장
    private void setUserInfo() {
        UserApiClient.getInstance().me((user, meError) -> {

            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else {
                Log.i(TAG, "사용자 정보 요청 성공");
                long userId = user.getId();
                String userEmail = user.getKakaoAccount().getEmail();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference defaultRef = database.getReference();

                defaultRef.child("user").child(String.valueOf(userId)).setValue(userEmail).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) { // 정보저장 성공한다면
                        Toast.makeText(Activity_Login.this, "유저 정보 저장 성공!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Activity_Login.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() { // 정보저장 실패 한다면
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Activity_Login.this, "유저 정보 저장 실패", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        });
    }

}
