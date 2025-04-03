package org.jeonfeel.moeuibit2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.ui.nav.MainBottomNavGraph
import org.jeonfeel.moeuibit2.ui.nav.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground

@Composable
fun MoeuiBitApp(
    appNavController: NavHostController,
    bottomNavController: NavHostController,
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { MainBottomNavigation(bottomNavController) },
        modifier = Modifier
            .background(commonBackground())
            .windowInsetsPadding(WindowInsets.systemBars)
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .consumeWindowInsets(contentPadding)
                .imePadding()
                .fillMaxSize()
                .background(commonBackground())
        ) {
            MainBottomNavGraph(
                appNavController = appNavController,
                bottomNavController = bottomNavController
            )
        }
    }
}