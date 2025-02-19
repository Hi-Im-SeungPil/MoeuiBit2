package org.jeonfeel.moeuibit2.ui.main.exchange.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.valentinilk.shimmer.LocalShimmerTheme
import com.valentinilk.shimmer.shimmer
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonSkeletonColor

@Composable
fun ExchangeLoadingScreen() {

    CompositionLocalProvider(
        LocalShimmerTheme provides org.jeonfeel.moeuibit2.ui.main.portfolio.creditCardTheme
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(commonBackground())
                .shimmer()
        ) {
            Box(
                modifier = Modifier
                    .padding(horizontal = 15.dp)
                    .fillMaxWidth()
                    .height(45.dp)
                    .background(commonSkeletonColor(), shape = RoundedCornerShape(5.dp))
            )

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .weight(3f)
                        .background(commonSkeletonColor(), shape = RoundedCornerShape(5.dp))
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Row(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .padding(horizontal = 15.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Box(
                    modifier = Modifier
                        .height(25.dp)
                        .weight(3f)
                        .background(commonSkeletonColor(), shape = RoundedCornerShape(5.dp))
                )
            }

            repeat(20) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .padding(horizontal = 15.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(commonSkeletonColor(), shape = RoundedCornerShape(5.dp))
                )
            }
        }
    }
}