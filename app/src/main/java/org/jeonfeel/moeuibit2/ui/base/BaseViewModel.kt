package org.jeonfeel.moeuibit2.ui.base

import androidx.lifecycle.ViewModel
import com.google.gson.Gson

abstract class BaseViewModel: ViewModel() {
    protected val gson = Gson()
}