package org.jeonfeel.moeuibit2.ui.main.coinsite.secsions

import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.ads.nativetemplates.NativeTemplateStyle
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import org.jeonfeel.moeuibit2.constants.AD_ID_NATIVE
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground


@Composable
fun ADSection() {
    Column(
        modifier = Modifier
            .padding(top = 20.dp)
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 20.dp, horizontal = 20.dp),
    ) {
        Text(
            text = "AD",
            style = TextStyle(
                fontSize = DpToSp(14.dp),
                fontWeight = FontWeight.W500,
                color = commonTextColor()
            )
        )

        NativeAdTemplateView()
    }
}

@Composable
fun NativeAdTemplateView(
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val view = remember {
        LayoutInflater.from(context)
            .inflate(org.jeonfeel.moeuibit2.R.layout.view_ad, null) as LinearLayout
    }
    val adLoader = remember {
        AdLoader.Builder(context, AD_ID_NATIVE)
            .forNativeAd { nativeAd ->
                val styles =
                    NativeTemplateStyle.Builder().build()
                val template: TemplateView =
                    view.findViewById(org.jeonfeel.moeuibit2.R.id.my_template)
                template.setStyles(styles)
                template.setNativeAd(nativeAd)
            }.build()
    }

    AndroidView(
        factory = { ctx ->
            view
        },
        modifier = modifier.padding(top = 10.dp).fillMaxSize(),
        update = { templateView ->
            if (!adLoader.isLoading) {
                adLoader.loadAd(AdRequest.Builder().build())
            }
        }
    )
}