package org.jeonfeel.moeuibit2.ui.common

sealed class MarketChangeState {
    data object Rise: MarketChangeState()
    data object Fall: MarketChangeState()
    data object Even: MarketChangeState()
}