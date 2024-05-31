package org.jeonfeel.moeuibit2.ui.common

sealed class MarketChangeState {
    object Rise: MarketChangeState()
    object Fall: MarketChangeState()
    object Even: MarketChangeState()
}