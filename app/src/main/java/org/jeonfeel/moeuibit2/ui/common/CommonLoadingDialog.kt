package org.jeonfeel.moeuibit2.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import org.jeonfeel.moeuibit2.R

@Composable
fun CommonLoadingDialog(dialogState: MutableState<Boolean>, text: String) {
    if (dialogState.value) {
        Dialog(onDismissRequest = { dialogState.value = false },
            DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(colorResource(id = R.color.design_default_color_background))
            ) {
                Column {
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(0.dp, 20.dp, 0.dp, 0.dp),
                        color = colorResource(id = R.color.C0F0F5C)
                    )
                    Text(text = text, Modifier.padding(20.dp, 8.dp, 20.dp, 15.dp))
                }
            }
        }
    }
}