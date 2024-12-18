package org.jeonfeel.moeuibit2.ui.main.coinsite

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.input.key.Key
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import javax.inject.Inject

@HiltViewModel
class CoinSiteViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
) : BaseViewModel(preferencesManager) {
    private val _coinSiteExchangeIsOpen = mutableStateOf(false)
    val coinSiteExchangeIsOpen: State<Boolean> = _coinSiteExchangeIsOpen

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
            "exchange" -> {
                _coinSiteExchangeIsOpen.value = !_coinSiteExchangeIsOpen.value
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

            "community" -> {
                _coinSiteCommunityIsOpen.value = !_coinSiteCommunityIsOpen.value
            }
        }
    }

    fun saveIsOpen() {
        listOf(
            KeyConst.PREF_KEY_COIN_SITE_EXCHANGE_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_INFO_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_KIMP_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_NEWS_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_COMMUNITY_IS_OPEN
        ).forEach {
            viewModelScope.launch {
                val value = when (it) {
                    KeyConst.PREF_KEY_COIN_SITE_EXCHANGE_IS_OPEN -> {
                        _coinSiteExchangeIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_INFO_IS_OPEN -> {
                        _coinSiteInfoIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_KIMP_IS_OPEN -> {
                        _coinSiteKimpIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_NEWS_IS_OPEN -> {
                        _coinSiteNewsIsOpen.value
                    }

                    KeyConst.PREF_KEY_COIN_SITE_COMMUNITY_IS_OPEN -> {
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
            KeyConst.PREF_KEY_COIN_SITE_EXCHANGE_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_INFO_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_KIMP_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_NEWS_IS_OPEN,
            KeyConst.PREF_KEY_COIN_SITE_COMMUNITY_IS_OPEN
        ).forEach {
            viewModelScope.launch {
                preferencesManager.getBoolean(it)
                    .collect { isOpen ->
                        when (it) {
                            KeyConst.PREF_KEY_COIN_SITE_EXCHANGE_IS_OPEN -> {
                                _coinSiteExchangeIsOpen.value = isOpen
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

                            KeyConst.PREF_KEY_COIN_SITE_COMMUNITY_IS_OPEN -> {
                                _coinSiteCommunityIsOpen.value = isOpen
                            }
                        }
                    }
            }
        }
    }
}