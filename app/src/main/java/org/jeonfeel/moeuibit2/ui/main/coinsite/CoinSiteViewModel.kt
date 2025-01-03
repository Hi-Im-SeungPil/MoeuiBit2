package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import javax.inject.Inject

@HiltViewModel
class CoinSiteViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : BaseViewModel(preferencesManager) {
    private val _coinSiteKoreaExchangeIsOpen = mutableStateOf(false)
    val coinSiteKoreaExchangeIsOpen: State<Boolean> = _coinSiteKoreaExchangeIsOpen

    private val _coinSiteGlobalExchangeIsOpen = mutableStateOf(false)
    val coinSiteGlobalExchangeIsOpen: State<Boolean> = _coinSiteGlobalExchangeIsOpen

    private val _coinSiteCommunityIsOpen = mutableStateOf(false)
    val coinSiteCommunityIsOpen: State<Boolean> = _coinSiteCommunityIsOpen

    private val _coinSiteInfoIsOpen = mutableStateOf(false)
    val coinSiteInfoIsOpen: State<Boolean> = _coinSiteInfoIsOpen

    private val _coinSiteKimpIsOpen = mutableStateOf(false)
    val coinSiteKimpIsOpen: State<Boolean> = _coinSiteKimpIsOpen

    private val _coinSiteNewsIsOpen = mutableStateOf(false)
    val coinSiteNewsIsOpen: State<Boolean> = _coinSiteNewsIsOpen

    fun updateIsOpen(name: String) {
        when (name) {
            "koreaExchange" -> {
                _coinSiteKoreaExchangeIsOpen.value = !_coinSiteKoreaExchangeIsOpen.value
            }

            "globalExchange" -> {
                _coinSiteGlobalExchangeIsOpen.value = !_coinSiteGlobalExchangeIsOpen.value
            }

            "community" -> {
                _coinSiteCommunityIsOpen.value = !_coinSiteCommunityIsOpen.value
            }

            "info" -> {
                _coinSiteInfoIsOpen.value = !_coinSiteInfoIsOpen.value
            }

            "kimp" -> {
                _coinSiteKimpIsOpen.value = !_coinSiteKimpIsOpen.value
            }

            "news" -> {
                _coinSiteNewsIsOpen.value = !_coinSiteNewsIsOpen.value
            }
        }
    }

    fun saveIsOpen() {
        listOf(
            KeyConst.PREF_KEY_COIN_SITE_KOREA_EXCHANGE_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_GLOBAL_EXCHANGE_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_INFO_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_KIMP_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_NEWS_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_COMMUNITY_IS_OPEN
        ).forEach {
            viewModelScope.launch {
                val value = when (it) {
                    KeyConst.PREF_KEY_COIN_SITE_KOREA_EXCHANGE_IS_OPEN -> {
                        Logger.e("save $it ${_coinSiteKoreaExchangeIsOpen.value}")
                        _coinSiteKoreaExchangeIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_GLOBAL_EXCHANGE_IS_OPEN -> {
                        Logger.e("save $it ${_coinSiteGlobalExchangeIsOpen.value}")

                        _coinSiteGlobalExchangeIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_INFO_IS_OPEN -> {
                        Logger.e("save $it ${_coinSiteInfoIsOpen.value}")

                        _coinSiteInfoIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_KIMP_IS_OPEN -> {
                        Logger.e("save $it ${_coinSiteKimpIsOpen.value}")

                        _coinSiteKimpIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_NEWS_IS_OPEN -> {
                        Logger.e("save $it ${_coinSiteNewsIsOpen.value}")

                        _coinSiteNewsIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_COMMUNITY_IS_OPEN -> {
                        Logger.e("save $it ${_coinSiteCommunityIsOpen.value}")

                        _coinSiteCommunityIsOpen.value
                    }

                    else -> false
                }

                preferencesManager.setValue(it, value)
            }
        }
    }

    fun getIsOpen() {
        listOf(
            KeyConst.PREF_KEY_COIN_SITE_KOREA_EXCHANGE_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_GLOBAL_EXCHANGE_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_INFO_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_KIMP_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_NEWS_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_COMMUNITY_IS_OPEN
        ).forEach {
            viewModelScope.launch {
                preferencesManager.getBoolean(it)
                    .collect { isOpen ->
                        Logger.e("get $it $isOpen")
                        when (it) {
                            KeyConst.PREF_KEY_COIN_SITE_KOREA_EXCHANGE_IS_OPEN -> {
                                _coinSiteKoreaExchangeIsOpen.value = isOpen
                            }

                            KeyConst.PREF_KEY_COIN_SITE_GLOBAL_EXCHANGE_IS_OPEN -> {
                                _coinSiteGlobalExchangeIsOpen.value = isOpen
                            }

                            KeyConst.PREF_KEY_COIN_SITE_COMMUNITY_IS_OPEN -> {
                                _coinSiteCommunityIsOpen.value = isOpen
                            }

                            KeyConst.PREF_KEY_COIN_SITE_INFO_IS_OPEN -> {
                                _coinSiteInfoIsOpen.value = isOpen
                            }

                            KeyConst.PREF_KEY_COIN_SITE_KIMP_IS_OPEN -> {
                                _coinSiteKimpIsOpen.value = isOpen
                            }

                            KeyConst.PREF_KEY_COIN_SITE_NEWS_IS_OPEN -> {
                                _coinSiteNewsIsOpen.value = isOpen
                            }
                        }
                    }
            }
        }
    }
}