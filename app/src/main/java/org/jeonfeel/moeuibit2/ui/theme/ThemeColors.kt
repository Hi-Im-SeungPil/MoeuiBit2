package org.jeonfeel.moeuibit2.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val PrimaryLight = Color(0xFF0F0F5C)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFFFFFFF) // 위에 타이틀 바
val OnPrimaryContainerLight = Color(0xFF000000)

val SecondaryLight = Color(0xFF535f70)
val OnSecondaryLight = Color(0xFFffffff)
val SecondaryContainerLight = Color(0xFFd7e3f7)
val OnSecondaryContainerLight = Color(0xFF101c2b)

val TertiaryLight = Color(0xFF6b5778)
val OnTertiaryLight = Color(0xFFffffff)
val TertiaryContainerLight = Color(0xFFf2daff)
val OnTertiaryContainerLight = Color(0xFF251431)

val ErrorLight = Color(0xFFba1a1a)
val OnErrorLight = Color(0xFFffffff)
val ErrorContainerLight = Color(0xFFffdad6)
val OnErrorContainerLight = Color(0xFF410002)

val BackgroundLight = Color(0xFFFFFFFF) // 배경
val OnBackgroundLight = Color(0xFF000000) // 배경 위 글씨
val SurfaceLight = Color(0xFFfdfcff)

val OutlineLight = Color(0xFF444444)
val SurfaceVariantLight = Color(0xFFdfe2eb)
val OnSurfaceVariantLight = Color(0xFF43474e)

// Dark==============================================================================
val PrimaryDark = Color(0xFFFFFFFF)
val OnPrimaryDark = Color(0xFF003258)
val PrimaryContainerDark = Color(0xFF35363A) // 위에 타이틀 바
val OnPrimaryContainerDark = Color(0xFFFFFFFF)

val SecondaryDark = Color(0xFFbbc7db)
val OnSecondaryDark = Color(0xFF253140)
val SecondaryContainerDark = Color(0xFF3b4858)
val OnSecondaryContainerDark = Color(0xFFd7e3f7)

val TertiaryDark = Color(0xFFd7bee4)
val OnTertiaryDark = Color(0xFF3b2948)
val TertiaryContainerDark = Color(0xFF523f5f)
val OnTertiaryContainerDark = Color(0xFFf2daff)

val ErrorDark = Color(0xFFffb4ab)
val OnErrorDark = Color(0xFF690005)
val ErrorContainerDark = Color(0xFF93000a)
val OnErrorContainerDark = Color(0xFFffdad6)

val BackgroundDark = Color(0xFF1a1c1e) // 배경
val OnBackgroundDark = Color(0xFFe2e2e6) // 배경 위 글씨
val SurfaceDark = Color(0xFF1a1c1e)

val OutlineDark = Color(0xFFCFCFCF)
val SurfaceVariantDark = Color(0xFF43474e)
val OnSurfaceVariantDark = Color(0xFFc3c7cf)