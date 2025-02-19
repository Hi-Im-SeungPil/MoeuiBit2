package org.jeonfeel.moeuibit2.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR

@Composable
fun CommonLoading(dismissRequest: () -> Unit = {}) {
    Dialog(
        onDismissRequest = { dismissRequest() },
        DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(Color.Transparent)
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .padding(0.dp, 20.dp, 0.dp, 0.dp),
                color = APP_PRIMARY_COLOR
            )
        }
    }
}