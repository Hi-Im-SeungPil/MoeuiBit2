package org.jeonfeel.moeuibit2.view.activity

import androidx.fragment.app.FragmentActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.mainactivity.MainBottomNavigation
import org.jeonfeel.moeuibit2.ui.mainactivity.Navigation

class MainActivity : FragmentActivity() {

    private var backBtnTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen() {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { MainBottomNavigation(navController)}
        ) {
           Navigation(navController)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun MainScreenPreview() {
        MainScreen()
    }

//    @Composable
//    fun Greeting(msg: Message) {
//        Row {
//            Image(
//                painter = painterResource(id = R.drawable.img_kakao_login),
//                contentDescription = "Contact profile picture",
//                modifier = Modifier
//                    .width(80.dp)
//                    .height(60.dp)
//                    .clip(CircleShape)
//                    .border(1.5.dp, MaterialTheme.colors.secondary, CircleShape)
//            )
//            Spacer(modifier = Modifier.width(8.dp))
//
//            Column {
//                Text(text = "Hello ${msg.author}!",
//                    color = MaterialTheme.colors.secondaryVariant)
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(text = "Hello ${msg.body}")
//            }
//        }
//    }

//    @Composable
//    fun MainBottomNavigation(navController: NavController) {
//        val items = listOf(
//            MainBottomNavItem.Exchange,
//            MainBottomNavItem.CoinSite,
//            MainBottomNavItem.Portfolio,
//            MainBottomNavItem.Setting
//        )
//        BottomNavigation(
//            backgroundColor = colorResource(id = R.color.statusBar),
//            contentColor = colorResource(id = R.color.white)
//        ) {
//            val navBackStackEntry by navController.currentBackStackEntryAsState()
//            val currentRoute = navBackStackEntry?.destination?.route
//            items.forEach { item ->
//                BottomNavigationItem(
//                    icon = {
//                        Icon(painter = painterResource(id = item.icon),
//                            contentDescription = item.title)
//                    },
//                    label = { Text(text = item.title, fontSize = 9.sp) },
//                selectedContentColor = androidx.compose.ui.graphics.Color.Black,
//                unselectedContentColor = androidx.compose.ui.graphics.Color.Black.copy(0.4f),
//                    alwaysShowLabel = true,
//                    selected = currentRoute == item.screen_route,
//                    onClick = {
//                        navController.navigate(item.screen_route) {
//
//                            navController.graph.startDestinationRoute?.let { screen_route ->
//                                popUpTo(screen_route) {
//                                    saveState = true
//                                }
//                            }
////                            launchSingleTop = true
////                            restoreState = true
//                        }
//                    })
//            }
//        }
//    }

    override fun onBackPressed() {
        val curTime = System.currentTimeMillis()
        val gapTime = curTime - backBtnTime
        if (gapTime in 0..2000) {
            super.onBackPressed()
        } else {
            backBtnTime = curTime
            Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show()
        }
    }
}
