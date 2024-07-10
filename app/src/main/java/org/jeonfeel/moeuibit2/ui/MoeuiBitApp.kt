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
import androidx.navigation.compose.rememberNavController
import org.jeonfeel.moeuibit2.ui.main.MainBottomNavGraph
import org.jeonfeel.moeuibit2.ui.main.MainBottomNavigation

@Composable
fun MoeuiBitApp(networkErrorState: MutableIntState, appNavController: NavHostController) {
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
        bottomBar = { MainBottomNavigation(navController) },
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            MainBottomNavGraph(navController, networkErrorState, appNavController)
        }
    }
}