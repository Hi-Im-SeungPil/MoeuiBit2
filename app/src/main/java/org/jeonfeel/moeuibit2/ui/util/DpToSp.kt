package org.jeonfeel.moeuibit2.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun DpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }