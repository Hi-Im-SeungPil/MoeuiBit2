package org.jeonfeel.moeuibit2.ui.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun DpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }