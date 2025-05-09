package org.jeonfeel.moeuibit2.ui.main.coinsite.item

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.common.noRippleClickable
import org.jeonfeel.moeuibit2.ui.theme.newtheme.APP_PRIMARY_COLOR
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coinsite.coinSiteUnSelectTabColor
import kotlin.reflect.KFunction1

@Composable
fun CommunityItem(
    updateIsOpen: KFunction1<String, Unit>,
    exchangeIsOpen: Boolean,
    context: Context
) {
    val communityImageUrl = getCommunityImageArray()
    val communityUrl = stringArrayResource(id = R.array.communityUrl)
    val packageMap = getCommunityPackageMap()
    val titles = packageMap.keys.toList()

    Column(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable { updateIsOpen("community") }) {
            Text(
                text = "커뮤니티",
                modifier = Modifier
                    .padding(35.dp, 0.dp, 10.dp, 0.dp)
                    .align(Alignment.CenterVertically),
                fontSize = DpToSp(18.dp),
                style = TextStyle(
                    fontWeight = FontWeight.W600,
                    color = if (exchangeIsOpen) APP_PRIMARY_COLOR else coinSiteUnSelectTabColor(),
                    textAlign = TextAlign.Center
                )
            )
            IconButton(onClick = { updateIsOpen("community") }) {
                Icon(
                    if (exchangeIsOpen) {
                        Icons.Filled.KeyboardArrowUp
                    } else {
                        Icons.Filled.KeyboardArrowDown
                    },
                    contentDescription = null,
                    tint = if (exchangeIsOpen) APP_PRIMARY_COLOR else coinSiteUnSelectTabColor(),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
        if (exchangeIsOpen) {
            Column(modifier = Modifier.fillMaxWidth()) {
                for (i in 0 until 9) {
                    CoinSiteCommonItem(
                        image = communityImageUrl[i],
                        title = titles[i],
                        url = communityUrl[i],
                        packageName = packageMap[titles[i]],
                        context = context
                    )
                }
            }
        }
    }
}

fun getCommunityImageArray(): Array<Int> {
    return arrayOf(
        R.drawable.img_coinpan,
        R.drawable.img_dcinside,
        R.drawable.img_ddanggle,
        R.drawable.img_cobak,
        R.drawable.img_moneynet,
        R.drawable.img_blockchainhub,
        R.drawable.img_fmkor,
        R.drawable.img_bitman,
        R.drawable.img_musk
    )
}

fun getCommunityPackageMap(): Map<String, String?> {
    return mapOf(
        "코인판" to "com.coinpan.coinpan",
        "비트코인 갤러리" to "com.dcinside.app",
        "땡글" to "com.ddengle.app",
        "코박" to "com.cobak.android",
        "머니넷" to "mnet7.mobile",
        "블록체인 허브" to "kr.blockchainhub.app",
        "가상화폐" to "com.fmkorea.m.fmk",
        "비트맨" to null,
        "머스크 트위터" to null,
    )
}