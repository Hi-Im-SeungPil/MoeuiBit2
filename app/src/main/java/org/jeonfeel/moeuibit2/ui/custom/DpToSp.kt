package org.jeonfeel.moeuibit2.ui.custom

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun DpToSp(dp: Int) = with(LocalDensity.current) { dp.toDp().toSp() }

@Composable
fun DpToSp(dp: Float) = with(LocalDensity.current) { dp.toDp().toSp() }