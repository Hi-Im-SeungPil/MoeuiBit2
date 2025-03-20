package org.jeonfeel.moeuibit2.ui.main.additional_features

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AdditionalFeaturesRoute(viewModel: AdditionalFeaturesViewModel = hiltViewModel()) {
    AdditionalFeaturesScreen()
}