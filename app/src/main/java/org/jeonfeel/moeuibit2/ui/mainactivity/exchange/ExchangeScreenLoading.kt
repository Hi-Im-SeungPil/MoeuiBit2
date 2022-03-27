package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.shimmer

@Composable
fun ExchangeScreenLoading() {
    Column(modifier = Modifier
        .fillMaxSize()
        .shimmer()) {

        Box(modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(45.dp)
            .background(Color.LightGray))
        Row(modifier = Modifier
            .fillMaxWidth()
            .height(30.dp)) {
            Text(text = "", modifier = Modifier.weight(1f))
            Text(text = "",
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .weight(1f)
                    .background(Color.LightGray))
            Text(text = "",
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .weight(1f)
                    .background(Color.LightGray))
            Text(text = "",
                modifier = Modifier
                    .padding(10.dp, 0.dp)
                    .weight(1f)
                    .background(Color.LightGray))
        }
        for (i in 0..14) {
            LoadingLazyColumnItem()
        }
    }
}

@Composable
fun LoadingLazyColumnItem() {
    Row(Modifier
        .fillMaxWidth()
        .height(50.dp)) {

        Column(Modifier
            .weight(1f)
            .align(Alignment.Bottom)) {
            Text(text = "",
                modifier = Modifier
                    .padding(10.dp, 10.dp, 10.dp, 2.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .wrapContentHeight(Alignment.Bottom)
                    .background(Color.LightGray),
                style = TextStyle(textAlign = TextAlign.Center)
            )
            Text(text = "",
                modifier = Modifier
                    .padding(10.dp, 2.dp, 10.dp, 10.dp)
                    .weight(1f)
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(Color.LightGray),
                style = TextStyle(textAlign = TextAlign.Center))
        }

        Text(text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp)
                .weight(1f)
                .fillMaxWidth()
                .fillMaxHeight()
                .wrapContentHeight()
                .background(Color.LightGray)
        )

        Text(text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp)
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
                .background(Color.LightGray)
        )

        Text(text = "",
            modifier = Modifier
                .padding(5.dp, 0.dp, 10.dp, 0.dp)
                .weight(1f)
                .fillMaxHeight()
                .wrapContentHeight()
                .background(Color.LightGray),
            style = TextStyle(textAlign = TextAlign.Center))
    }
}

@Composable
@Preview
fun LoadingLazyColumnItemp() {
    LoadingLazyColumnItem()
}