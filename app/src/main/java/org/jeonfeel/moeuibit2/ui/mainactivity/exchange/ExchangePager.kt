//package org.jeonfeel.moeuibit2.ui.mainactivity.exchange
//
//import android.content.Intent
//import android.util.Log
//import androidx.activity.result.ActivityResultLauncher
//import androidx.compose.runtime.Composable
//import com.google.accompanist.pager.ExperimentalPagerApi
//import com.google.accompanist.pager.HorizontalPager
//import com.google.accompanist.pager.PagerState
//import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
//import org.jeonfeel.moeuibit2.constant.SELECTED_BTC_MARKET
//import org.jeonfeel.moeuibit2.constant.SELECTED_FAVORITE
//import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
//import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
//
//@OptIn(ExperimentalPagerApi::class)
//@Composable
//fun ExchangePager(
//    tabTitleList: List<String>,
//    pagerState: PagerState,
//    mainViewModel: MainViewModel,
//    startForActivityResult: ActivityResultLauncher<Intent>,
//) {
//    HorizontalPager(
//        count = tabTitleList.size,
//        state = pagerState,
//    ) { page ->
//        Log.e("pageActive", page.toString())
//        when (page) {
////            SELECTED_KRW_MARKET -> {
////                if (pagerState.currentPage == SELECTED_KRW_MARKET) {
////                    mainViewModel.selectedMarketState.value = SELECTED_KRW_MARKET
////                    ExchangeScreenLazyColumn(mainViewModel, startForActivityResult)
////                    if (UpBitTickerWebSocket.currentMarket != SELECTED_KRW_MARKET) {
////                        UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
////                        UpBitTickerWebSocket.onPause()
////                        Log.e("selectedMarketState1", page.toString())
////                        mainViewModel.sortList(marketState = SELECTED_KRW_MARKET)
////                        mainViewModel.requestCoinListToWebSocket()
////                    }
////                }
////            }
////            SELECTED_BTC_MARKET -> {
////                if (pagerState.currentPage == SELECTED_BTC_MARKET) {
////                    mainViewModel.selectedMarketState.value = SELECTED_BTC_MARKET
////                    BtcExchangeScreenLazyColumn(mainViewModel, startForActivityResult)
////                    if (UpBitTickerWebSocket.currentMarket != SELECTED_BTC_MARKET) {
////                        UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
////                        UpBitTickerWebSocket.onPause()
////                        Log.e("selectedMarketState2", page.toString())
////                        mainViewModel.sortList(marketState = SELECTED_BTC_MARKET)
////                        mainViewModel.requestCoinListToWebSocket()
////                    }
////                }
////            }
////            SELECTED_FAVORITE -> {
////                if (pagerState.currentPage == SELECTED_FAVORITE) {
////                    mainViewModel.loadingFavorite.value = true
////                    mainViewModel.selectedMarketState.value = SELECTED_FAVORITE
////                    if (!mainViewModel.loadingFavorite.value) {
////                        FavoriteExchangeScreenLazyColumn(mainViewModel, startForActivityResult)
////                    }
////                    if (UpBitTickerWebSocket.currentMarket != SELECTED_FAVORITE) {
////                        mainViewModel.requestFavoriteData(SELECTED_FAVORITE)
////                        UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
////                        UpBitTickerWebSocket.onPause()
////                        Log.e("selectedMarketState3", page.toString())
////                        mainViewModel.requestCoinListToWebSocket()
////                    }
////                }
////            }
////        }
////    }
//}