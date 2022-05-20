package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio

import org.jeonfeel.moeuibit2.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.ui.mainactivity.MainBottomNavItem
import org.jeonfeel.moeuibit2.ui.mainactivity.MainNavigation

@Composable
fun PortfolioScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier
                    .fillMaxWidth(),
                backgroundColor = colorResource(id = R.color.design_default_color_background),
            ) {
                Text(
                    text = "투자 내역",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp, 0.dp, 0.dp, 0.dp)
                        .fillMaxHeight()
                        .wrapContentHeight(),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    ) { contentPadding ->
        Box(modifier = Modifier.padding(contentPadding)) {
            PortfolioMain()
        }
    }
}

@Composable
fun PortfolioMain() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "보유 자산",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp, 20.dp, 0.dp, 20.dp)
                    .wrapContentHeight(),
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                PortfolioMainItem(text1 = "보유 KRW",text2 = "0")
                PortfolioMainItem(text1 = "총 보유자산",text2 = "0")
            }
        }

    }
}

@Composable
fun ColumnScope.PortfolioMainItem(text1:String, text2: String) {
    Column() {
        Text(
            text = text1,
            modifier = Modifier
                .padding(15.dp, 20.dp, 0.dp, 20.dp)
                .wrapContentHeight(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
            )
        )
        Text(
            text = text2,
            modifier = Modifier
                .padding(15.dp, 20.dp, 0.dp, 20.dp)
                .wrapContentHeight(),
            style = TextStyle(
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}