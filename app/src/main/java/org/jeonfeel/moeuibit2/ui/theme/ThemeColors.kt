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

val PrimaryLight = Color(0xFF0061a4)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFd1e4ff)
val OnPrimaryContainerLight = Color(0xFF001d36)

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

val BackgroundLight = Color(0xFFFFFFFF)
val OnBackgroundLight = Color(0xFF0F0F5C)
val SurfaceLight = Color(0xFFfdfcff)

val OutlineLight = Color(0xFF73777f)
val SurfaceVariantLight = Color(0xFFdfe2eb)
val OnSurfaceVariantLight = Color(0xFF43474e)

// Dark==============================================================================
val PrimaryDark = Color(0xFF2c9fff)
val OnPrimaryDark = Color(0xFF003258)
val PrimaryContainerDark = Color(0xFF00497d)
val OnPrimaryContainerDark = Color(0xFFd1e4ff)

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

val BackgroundDark = Color(0xFF1a1c1e)
val OnBackgroundDark = Color(0xFFe2e2e6)
val SurfaceDark = Color(0xFF1a1c1e)

val OutlineDark = Color(0xFF8d9199)
val SurfaceVariantDark = Color(0xFF43474e)
val OnSurfaceVariantDark = Color(0xFFc3c7cf)

private val DarkColorPalette = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    primaryContainer = PrimaryContainerDark,
    onPrimaryContainer = OnPrimaryContainerDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    secondaryContainer = SecondaryContainerDark,
    onSecondaryContainer = OnSecondaryContainerDark,
    tertiary = TertiaryDark,
    onTertiary = OnTertiaryDark,
    tertiaryContainer = TertiaryContainerDark,
    onTertiaryContainer = OnTertiaryContainerDark,
    error = ErrorDark,
    onError = OnErrorDark,
    errorContainer = ErrorContainerDark,
    onErrorContainer = OnErrorContainerDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    outline = OutlineDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
)

private val LightColorPalette = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    outline = OutlineLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
)

@Composable
fun JetpackComposeDarkThemeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )

    // Optional, this part helps you set the statusbar color
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.background.toArgb()

            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !darkTheme
        }
    }
}


//val md_theme_light_primary = Color(0xFFFFFFFF)
//val md_theme_light_onPrimary = Color(0xFF000000)
//
//val md_theme_dark_primary = Color(0xFF464646)
//val md_theme_dark_onPrimary = Color(0xFFFFFFFF)
//val md_theme_dark_primaryContainer = Color(0xFF646464)
//val md_theme_dark_onPrimaryContainer = Color(0xFFFFFFFF)
//
//val LightColorPalette = lightColors(
//    primary = md_theme_light_primary,
//    onPrimary = md_theme_light_onPrimary
//)
//
//val DarkColorPalette = darkColors(
//    primary = md_theme_dark_primary,
//    onPrimary = md_theme_dark_onPrimary,
//    primaryVariant = Color(0xffb4b4b4),
//    secondary = md_theme_dark_primaryContainer,
//    onSecondary = md_theme_dark_onPrimaryContainer,
//)
//
//@Composable
//fun MBitAppTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
//    content: @Composable () -> Unit
//) {
//    val context = LocalContext.current
//    val colors = if (darkTheme) DarkColorPalette else LightColorPalette
//    MaterialTheme(
//        colors = colors,
//        content = content
//    )
//}