package org.jeonfeel.moeuibit2.ui.kimp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun KimpScreen() {
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {
        KimpScreenSpinner()
        KimpScreenSearchBasicTextField()
        Box(modifier = Modifier.fillMaxSize())
    }
}