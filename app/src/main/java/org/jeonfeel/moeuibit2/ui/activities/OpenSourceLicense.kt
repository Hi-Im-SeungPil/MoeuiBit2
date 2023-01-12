package org.jeonfeel.moeuibit2.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.main.setting.OpenSourceLicenseLazyColumn

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
                            text = stringResource(id = R.string.openSourceLicense),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(5.dp, 0.dp, 0.dp, 0.dp)
                                .fillMaxHeight()
                                .wrapContentHeight(),
                            style = TextStyle(
                                color = Color.Black,
                                fontSize = DpToSp(dp = 25),
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