package org.jeonfeel.moeuibit2.ui.mining

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.ui.common.UIState
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun MiningScreenRoute(
    viewModel: MiningViewModel = hiltViewModel(),
    type: String,
    appNavController: NavHostController,
) {
    AddLifecycleEvent(
        onCreateAction = {
            viewModel.init(type)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = commonBackground())
    ) {
        when (viewModel.uiState.value.uiState) {
            UIState.SUCCESS -> {
                MiningScreen(miningInfoList = viewModel.uiState.value.miningInfo)
            }

            UIState.ERROR -> {

            }

            UIState.LOADING -> {

            }

            else -> {

            }
        }
    }
}