package org.jeonfeel.moeuibit2.ui.main.additional_features

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.main.additional_features.secsions.AveragePurchaseCalculatorSection
import org.jeonfeel.moeuibit2.ui.main.additional_features.secsions.FeaturesSection
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor

@Composable
fun AdditionalFeaturesScreen(
    featuresUIState: AdditionalFeaturesUIState,
    backToFeaturesMain: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            featuresScreenState = featuresUIState.featureScreenState.value,
            topAppBarText = featuresUIState.topAppBarText.value,
            backToFeaturesMain = backToFeaturesMain
        )

        when (featuresUIState.featureScreenState.value) {
            FeatureScreenState.FEATURE_SCREEN -> {
                FeaturesSection(features = featuresUIState.features)
            }

            FeatureScreenState.PURCHASE_PRICE_AVERAGE_CALCULATOR -> {
                AveragePurchaseCalculatorSection()
            }
        }
    }
}

@Composable
private fun TopAppBar(
    featuresScreenState: FeatureScreenState,
    topAppBarText: String,
    backToFeaturesMain: () -> Unit
) {
    Row(modifier = Modifier.background(commonBackground())) {
        if (featuresScreenState != FeatureScreenState.FEATURE_SCREEN) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "",
                modifier = Modifier
                    .padding(start = 15.dp, end = 15.dp)
                    .size(25.dp)
                    .align(Alignment.CenterVertically)
                    .noRippleClickable { backToFeaturesMain() }
            )
        }

        Text(
            text = topAppBarText,
            modifier = Modifier
                .padding(10.dp, 0.dp, 0.dp, 0.dp)
                .weight(1f, true)
                .align(Alignment.CenterVertically),
            style = TextStyle(
                color = commonTextColor(),
                fontSize = DpToSp(20.dp),
                fontWeight = FontWeight.W600
            )
        )

        Text(
            text = "",
            modifier = Modifier
                .padding(21.dp)
                .wrapContentWidth(),
            style = TextStyle(
                color = commonTextColor(),
                fontSize = DpToSp(dp = 13.dp)
            )
        )
    }
}