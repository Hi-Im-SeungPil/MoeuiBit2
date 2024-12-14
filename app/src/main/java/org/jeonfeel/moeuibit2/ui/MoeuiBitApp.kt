package org.jeonfeel.moeuibit2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import org.jeonfeel.moeuibit2.ui.nav.MainBottomNavGraph
import org.jeonfeel.moeuibit2.ui.main.MainBottomNavigation

@Composable
fun MoeuiBitApp(
    networkErrorState: MutableIntState,
    appNavController: NavHostController,
    bottomNavController: NavHostController
) {
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { MainBottomNavigation(bottomNavController) },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            MainBottomNavGraph(appNavController = appNavController, bottomNavController = bottomNavController, networkErrorState = networkErrorState)
        }
    }
}