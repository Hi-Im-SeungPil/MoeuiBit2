package org.jeonfeel.moeuibit2.ui.mining

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent

@Composable
fun MiningScreenRoute(
    viewModel: MiningViewModel = hiltViewModel(),
    type: String,
    appNavController: NavHostController,
) {
    AddLifecycleEvent(
        onCreateAction = {
            viewModel.fetchAppMiningInfo()
        }
    )

    MiningScreen()
}