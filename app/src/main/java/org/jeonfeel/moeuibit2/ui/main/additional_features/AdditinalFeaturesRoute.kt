package org.jeonfeel.moeuibit2.ui.main.additional_features

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun AdditionalFeaturesRoute(
    viewModel: AdditionalFeaturesViewModel = hiltViewModel(),
    popBackState: () -> Unit
) {
    val dispatcher = LocalOnBackPressedDispatcherOwner.current!!.onBackPressedDispatcher
    val state = rememberAdditionalFeaturesStateHolder()

    BackHandler {
        if (state.featuresUIState.value.featureScreenState.value == FeatureScreenState.FEATURE_SCREEN) {
            popBackState()
        }

        state.updateScreenState(state = FeatureScreenState.FEATURE_SCREEN)
    }

    AddLifecycleEvent(
        onCreateAction = {
            state.createUIState()
        }
    )

    AdditionalFeaturesScreen(
        featuresUIState = state.featuresUIState.value,
        backToFeaturesMain = { state.updateScreenState(FeatureScreenState.FEATURE_SCREEN) }
    )
}