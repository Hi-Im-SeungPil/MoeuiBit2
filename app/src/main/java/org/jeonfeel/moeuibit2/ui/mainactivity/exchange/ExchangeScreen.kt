package org.jeonfeel.moeuibit2.ui.mainactivity.exchange

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel

@Composable
fun ExchangeScreen(exchangeViewModel: ExchangeViewModel = viewModel()) {
    Column(Modifier
        .fillMaxSize()) {
        SearchBasicTextFieldResult(exchangeViewModel)
        SortButtons(exchangeViewModel)
        ExchangeScreenLazyColumn(exchangeViewModel)
    }
}
