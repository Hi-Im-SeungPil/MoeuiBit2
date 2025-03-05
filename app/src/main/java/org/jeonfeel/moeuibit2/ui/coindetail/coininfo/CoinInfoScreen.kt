package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinLinkModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.LinkType
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonFallColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun CoinInfoScreen(
    moveToWeb: (String, LinkType) -> Unit,
    coinInfoModel: CoinInfoModel?,
    coinLinkList: List<CoinLinkModel>,
) {
    CoinInfoContent(
        coinInfoModel = coinInfoModel,
        coinLinkList = coinLinkList,
        moveToWeb = moveToWeb
    )
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
        CoinLinkSection(coinLinkList = coinLinkList, moveToWeb = moveToWeb)
//        CoinInfoSection(coinInfoModel)
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
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 15.dp, horizontal = 15.dp)
    ) {
        if (coinLinkList.isNotEmpty()) {
            Text(
                text = "바로가기",
                modifier = Modifier.padding(start = 10.dp),
                style = TextStyle(color = commonTextColor(), fontSize = DpToSp(15.dp))
            )
            Row(modifier = Modifier.padding(top = 15.dp)) {
                coinLinkList.forEach {
                    MoveUrlText(text = it.title) {
                        moveToWeb(it.url, it.linkType)
                    }
                }
            }
        } else {
            Text(
                "바로가기 정보가 등록되있지 않습니다.",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 15.dp),
                style = TextStyle(fontSize = DpToSp(17.dp), color = commonTextColor())
            )
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

// 시총 ,총 발행량, 현재 유통량,
@Composable
fun CoinInfoSection(coinInfoModel: CoinInfoModel?) {
    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp)
    ) {
        if (coinInfoModel != null) {
            Text(
                text = "(${coinInfoModel.timeString})",
                modifier = Modifier.padding( start = 15.dp),
                style = TextStyle(color = commonHintTextColor(), fontSize = DpToSp(14.dp))
            )
            CoinInfoColumnItem("시가총액", "${coinInfoModel.marketCapKRW} ${coinInfoModel.unit}")
            CoinInfoColumnItem("시가총액 순위", "${coinInfoModel.rank}위")
            CoinInfoColumnItem("총 발행량", coinInfoModel.maxSupply)
            CoinInfoColumnItem("현재 유통량", coinInfoModel.supply)
        } else {
            Text(
                "코인정보가 등록되있지 않습니다.",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 15.dp),
                style = TextStyle(fontSize = DpToSp(17.dp), color = commonTextColor())
            )
        }
    }
}

@Composable
fun CoinInfoColumnItem(title: String, value: String) {
    Row(
        modifier = Modifier
            .padding(vertical = 15.dp, horizontal = 15.dp)
    ) {
        Text(
            text = title,
            style = TextStyle(color = commonTextColor(), fontSize = DpToSp(15.dp))
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            style = TextStyle(color = commonTextColor(), fontSize = DpToSp(15.dp))
        )
    }
}