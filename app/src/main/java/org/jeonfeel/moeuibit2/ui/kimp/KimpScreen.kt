package org.jeonfeel.moeuibit2.ui.kimp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.activity.kimp.viewmodel.KimpViewModel

@Composable
fun KimpScreen(kimpViewModel: KimpViewModel = viewModel()) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        KimpScreenSpinner()
        KimpScreenSearchBasicTextField()
        KimpMarketButtons(kimpViewModel)
        KimpScreenSortButtons()
        KimpScreenLazyColumn()
    }
}