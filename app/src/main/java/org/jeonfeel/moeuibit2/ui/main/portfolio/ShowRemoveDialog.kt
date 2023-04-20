package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.utils.showToast

@Composable
fun ShowRemoveDialog(key: MutableState<Int>) {
    val context = LocalContext.current
    LaunchedEffect(key1 = key.value == -1) {
        context.showToast(context.getString(R.string.notRemovedCoin))
    }
    LaunchedEffect(key1 = key.value != -1 && key.value != 0) {
        context.showToast(context.getString(R.string.removedCoin, key.value))
    }
}