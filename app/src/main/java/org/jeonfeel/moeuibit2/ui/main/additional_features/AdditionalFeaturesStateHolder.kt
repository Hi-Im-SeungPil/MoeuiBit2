package org.jeonfeel.moeuibit2.ui.main.additional_features

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class AdditionalFeaturesUILogicManager() {
    fun averageCostCalculatorAction() {

    }

    fun checkExchangeAction() {

    }
}

class AdditionalFeaturesStateHolder() {
    private val uiLogicManager = AdditionalFeaturesUILogicManager()

    fun averageCostCalculatorAction() {
        uiLogicManager.averageCostCalculatorAction()
    }

    fun checkExchangeAction() {
        uiLogicManager.checkExchangeAction()
    }
}

@Composable
fun rememberAdditionalFeaturesStateHolder() = remember() {
    AdditionalFeaturesStateHolder()
}