package org.jeonfeel.moeuibit2.activity.opensource

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.CoinDetailActivity
import org.jeonfeel.moeuibit2.ui.mainactivity.setting.OpenSourceLicenseLazyColumn

class OpenSourceLicense : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize(),
                topBar = {
                    TopAppBar(
                        modifier = Modifier
                            .fillMaxWidth(),
                        backgroundColor = colorResource(id = R.color.design_default_color_background),
                    ) {
                        Text(
                            text = "오픈소스 라이선스",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp, 0.dp, 0.dp, 0.dp)
                                .fillMaxHeight()
                                .wrapContentHeight(),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }){ contentPadding ->
                Box(modifier = Modifier.padding(contentPadding)){
                    OpenSourceLicenseLazyColumn()
                }
            }
        }
    }
}