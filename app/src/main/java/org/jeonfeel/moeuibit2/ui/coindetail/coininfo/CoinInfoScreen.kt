package org.jeonfeel.moeuibit2.ui.coindetail.coininfo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.glide.GlideImage
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinInfoModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.CoinLinkModel
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.model.LinkType
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonDividerColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonFallColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.portfolioMainBackground

@Composable
fun CoinInfoScreen(
    moveToWeb: (String, LinkType) -> Unit,
    coinInfoModel: CoinInfoModel?,
) {
    CoinInfoContent(
        coinInfoModel = coinInfoModel,
        moveToWeb = moveToWeb
    )
}

@Composable
fun CoinInfoContent(
    coinInfoModel: CoinInfoModel?,
    moveToWeb: (String, LinkType) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = commonBackground())
    ) {
        if (coinInfoModel == null) {
            CoinInfoIsEmpty()
        } else {
            CoinLinkSection(coinInfoModel = coinInfoModel, moveToWeb = moveToWeb)
            CoinInfoText(
                title = "총 발행량",
                text = coinInfoModel.maxSupply ?: "",
                symbol = coinInfoModel.symbol
            )
            CoinInfoText(
                title = "현재 유통량",
                text = coinInfoModel.circulatingSupply,
                symbol = coinInfoModel.symbol
            )
            CoinInfoText(title = "시가총액 순위", text = coinInfoModel.marketCapRank + " 등")
            CoinInfoText(title = "시가총액", text = coinInfoModel.marketCap ?: "")
            CoinInfoText(title = "희석된 시가총액", text = coinInfoModel.fullyDilutedValuation ?: "")
        }
    }
}

@Composable
fun CoinInfoText(
    title: String,
    text: String,
    symbol: String = ""
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 15.dp)
    ) {

        Row(
            modifier = Modifier.padding(bottom = 3.dp)
        ) {
            Text(
                text = title,
                style = TextStyle(
                    color = commonHintTextColor(),
                    fontSize = DpToSp(15.dp),
                    fontWeight = FontWeight.W500
                )
            )

            Text(
                text = if (text == "0" || text.isEmpty()) "-" else text,
                modifier = Modifier.weight(1f),
                style = TextStyle(
                    color = commonTextColor(),
                    fontSize = DpToSp(15.dp),
                    textAlign = TextAlign.End
                )
            )

            if (symbol.isNotEmpty() && !(text == "0" || text.isEmpty())) {
                Text(
                    text = " $symbol",
                    style = TextStyle(
                        color = commonTextColor(),
                        fontSize = DpToSp(15.dp),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.W600
                    )
                )
            }
        }

        Divider(
            modifier = Modifier.fillMaxWidth(),
            color = commonHintTextColor(),
            thickness = 1.5.dp
        )
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
    moveToWeb: (String, LinkType) -> Unit,
    coinInfoModel: CoinInfoModel?,
) {
    val links = remember {
        listOf(
            coinInfoModel?.homePage,
            coinInfoModel?.whitePaper,
            coinInfoModel?.blockchainSite,
        )
    }

    Row(modifier = Modifier.padding(top = 15.dp)) {
        GlideImage(
            imageModel = coinInfoModel?.image,
            modifier = Modifier
                .padding(top = 15.dp)
                .padding(start = 20.dp)
                .size(60.dp)
                .background(color = Color.White, shape = RoundedCornerShape(999.dp))
                .clip(RoundedCornerShape(999.dp))
                .border(1.dp, commonDividerColor(), RoundedCornerShape(999.dp))
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
                .padding(vertical = 15.dp, horizontal = 15.dp)
        ) {
            Row(modifier = Modifier) {
                if (!links[1].isNullOrEmpty()) {
                    MoveUrlText(text = "백서") {
                        moveToWeb(links[1] ?: "", LinkType.IN_APP)
                    }
                }

                if (!links[0].isNullOrEmpty()) {
                    MoveUrlText(text = "홈페이지") {
                        moveToWeb(links[0] ?: "", LinkType.IN_APP)
                    }
                }

                if (!links[2].isNullOrEmpty()) {
                    MoveUrlText(text = "익스플로러") {
                        moveToWeb(links[2] ?: "", LinkType.IN_APP)
                    }
                }
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
            color = if (text.isNotEmpty()) commonFallColor() else commonDividerColor(),
            textDecoration = if (text.isNotEmpty()) TextDecoration.Underline else TextDecoration.None,
            textAlign = TextAlign.Center
        ),
        modifier = Modifier
            .padding(0.dp, 4.dp)
            .weight(1f)
            .wrapContentHeight()
            .clickable {
                if (text.isNotEmpty()) {
                    clickAction()
                }
            }
            .padding(0.dp, 5.dp)
    )
}

// 시총 ,총 발행량, 현재 유통량,
//@Composable
//fun CoinInfoSection(coinInfoModel: CoinInfoModel?) {
//    Column(
//        modifier = Modifier
//            .padding(top = 15.dp)
//            .padding(horizontal = 20.dp)
//            .fillMaxWidth()
//            .background(color = portfolioMainBackground(), shape = RoundedCornerShape(10.dp))
//            .padding(vertical = 10.dp)
//    ) {
//        if (coinInfoModel != null) {
//            Text(
//                text = "(${coinInfoModel.timeString})",
//                modifier = Modifier.padding(start = 15.dp),
//                style = TextStyle(color = commonHintTextColor(), fontSize = DpToSp(14.dp))
//            )
//            CoinInfoColumnItem("시가총액", "${coinInfoModel.marketCapKRW} ${coinInfoModel.unit}")
//            CoinInfoColumnItem("시가총액 순위", "${coinInfoModel.rank}위")
//            CoinInfoColumnItem("총 발행량", coinInfoModel.maxSupply)
//            CoinInfoColumnItem("현재 유통량", coinInfoModel.supply)
//        } else {
//            Text(
//                "코인정보가 등록되있지 않습니다.",
//                modifier = Modifier
//                    .align(Alignment.CenterHorizontally)
//                    .padding(vertical = 15.dp),
//                style = TextStyle(fontSize = DpToSp(17.dp), color = commonTextColor())
//            )
//        }
//    }
//}

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