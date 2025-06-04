package org.jeonfeel.moeuibit2.ui.main.additional_features

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.UrlConst
import org.jeonfeel.moeuibit2.utils.ext.showToast

enum class FeatureScreenState {
    FEATURE_SCREEN,
    PURCHASE_PRICE_AVERAGE_CALCULATOR,
}

data class AdditionalFeaturesUIState(
    val featureScreenState: State<FeatureScreenState> = mutableStateOf(FeatureScreenState.FEATURE_SCREEN),
    val topAppBarText: State<String> = mutableStateOf("부가 기능"),
    val features: List<Triple<Int, String, () -> Unit>> = listOf(),
)

class AdditionalFeaturesStateHolder(
    private val navigateToMiningInfo: ((type: String) -> Unit) = {},
    private val context: Context,
) {
    private val _featuresUIState = mutableStateOf(AdditionalFeaturesUIState())
    val featuresUIState: State<AdditionalFeaturesUIState> = _featuresUIState

    private val _featureScreenState = mutableStateOf(FeatureScreenState.FEATURE_SCREEN)

    private val _topAppBarText = mutableStateOf("부가 기능")

    private val features = listOf(
        Triple(
            R.drawable.img_grass,
            "DePIN 채굴 정보"
        ) {
            val intent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(UrlConst.DEPIN_MINING_COLLECTION_URL)
            }
            context.startActivity(intent)
        },
        Triple(
            R.drawable.img_iphone,
            "App 채굴 정보"
        ) {
            navigateToMiningInfo("App")
        },
//        Triple(
//            R.drawable.img_telegram,
//            "텔레그램 채굴 정보"
//        ) {
//            navigateToMiningInfo("tg")
//        },
        Triple(
            R.drawable.img_calculator,
            "평단가 계산기"
        ) {
            _featureScreenState.value = FeatureScreenState.PURCHASE_PRICE_AVERAGE_CALCULATOR
            _topAppBarText.value = "평단가 계산기"
        }
    )

    fun createUIState() {
        _featuresUIState.value = AdditionalFeaturesUIState(
            featureScreenState = _featureScreenState,
            topAppBarText = _topAppBarText,
            features = features
        )
    }

    fun updateScreenState(state: FeatureScreenState) {
        _featureScreenState.value = state
    }

    fun resetTopAppBarText() {
        _topAppBarText.value = "부가 기능"
    }

    fun dePINShare() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                UrlConst.DEPIN_MINING_COLLECTION_URL
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(intent, null)
        context.startActivity(shareIntent)
    }

    fun copyDePINURL() {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", UrlConst.DEPIN_MINING_COLLECTION_URL)
        clipboard.setPrimaryClip(clip)

        context.showToast("주소가 클립보드에 복사되었습니다.")
    }
}

@Composable
fun rememberAdditionalFeaturesStateHolder(
    navigateToMiningInfo: ((type: String) -> Unit) = {},
    context: Context = LocalContext.current,
) =
    remember {
        AdditionalFeaturesStateHolder(
            navigateToMiningInfo = navigateToMiningInfo,
            context = context
        )
    }