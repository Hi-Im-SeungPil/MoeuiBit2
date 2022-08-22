package org.jeonfeel.moeuibit2.ui.kimp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun KimpScreenLazyColumn() {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(10) {
            KimpScreenLazyColumnItem()
        }
    }
}