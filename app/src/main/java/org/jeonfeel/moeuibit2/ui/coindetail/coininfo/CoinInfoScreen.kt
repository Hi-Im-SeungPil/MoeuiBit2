package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import app.dvkyun.flexhybridand.FlexWebView
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinLinkModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.LinkType
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonFallColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import kotlin.reflect.KFunction2

@Composable
fun CoinInfoScreen(
    uiState: ResultState<CoinInfoScreenUIState>,
    moveToWeb: (String, LinkType) -> Unit,
) {
    when (uiState) {
        is ResultState.Success -> {

        }

        is ResultState.Error -> {

        }

        is ResultState.Loading -> {
//            CommonLoadingDialog(
//                dialogState = viewModel.coinInfo.coinInfoLoading,
//                text = stringResource(id = R.string.coinInfoLoading),
//            )
        }
    }

//    if (coinInfoMap.isNotEmpty() && !viewModel.coinInfo.coinInfoLoading.value) {
//        CoinInfoContent(
//            selected = selected,
//            selectedButton = selectedButton,
//            coinInfoHashMap = coinInfoMap.toMap(),
//            flex = flex
//        )
//    } else if (coinInfoMap.isEmpty() && !viewModel.coinInfo.coinInfoLoading.value) {
//        CoinInfoEmptyScreen()
//    }
}

@Composable
fun CoinInfoContent(
    coinInfoModel: CoinInfoModel?,
    coinLinkList: List<CoinLinkModel>,
    moveToWeb: (String, LinkType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = commonBackground())
    ) {
        when {
            coinInfoModel == null && coinLinkList.isEmpty() -> {
                CoinInfoIsEmpty()
            }

            coinLinkList.isNotEmpty() -> {
                CoinLinkSection(coinLinkList = coinLinkList, moveToWeb = moveToWeb)
            }

            coinInfoModel != null -> {

            }
        }
    }
}

@Composable
fun CoinInfoIsEmpty() {
    Text(
        text = stringResource(id = R.string.noInfo),
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight(),
        style = TextStyle(
            color = commonTextColor(),
            fontWeight = FontWeight.W600,
            fontSize = DpToSp(23.dp),
            textAlign = TextAlign.Center
        )
    )
}

@Composable
fun CoinLinkSection(
    coinLinkList: List<CoinLinkModel>,
    moveToWeb: (String, LinkType) -> Unit,
) {
    Row {
        coinLinkList.forEach {
            MoveUrlText(text = it.title) {
                moveToWeb(it.url, it.linkType)
            }
        }
    }
}

@Composable
fun RowScope.MoveUrlText(text: String, clickAction: () -> Unit) {
    Text(
        text = text,
        fontSize = DpToSp(14.dp),
        style = TextStyle(
            color = commonFallColor(),
            textDecoration = TextDecoration.Underline,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .padding(0.dp, 4.dp)
            .weight(1f)
            .wrapContentHeight()
            .clickable {
                clickAction()
            }
            .padding(0.dp, 5.dp)
    )
}