package org.jeonfeel.moeuibit2.ui.kimp

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import org.jeonfeel.moeuibit2.R

@Composable
fun KimpScreenSpinner() {
    Row(modifier = Modifier.fillMaxWidth()) {
        KimpScreenDropDown(suggestionList = listOf("업비트", "빗썸", "코인원"),
            imageResourceList = listOf(R.drawable.img_upbit,
                R.drawable.img_bithumb,
                R.drawable.img_coinone))
        Image(painter = painterResource(id = R.drawable.img_compare), contentDescription = null, modifier = Modifier.weight(0.15f).height(50.dp).wrapContentHeight())
        KimpScreenDropDown(suggestionList = listOf("바이낸스", "코인베이스", "크라켄", "게이트IO"),
            listOf(R.drawable.img_binance,
                R.drawable.img_coinness,
                R.drawable.img_kimpga,
                R.drawable.img_musk))
    }
}

@Composable
fun RowScope.KimpScreenDropDown(suggestionList: List<String>, imageResourceList: List<Int>) {
    val initText = suggestionList[0]
    val expanded = remember { mutableStateOf(false) }
    val suggestions = remember { suggestionList }
    val imageResources = remember { imageResourceList }
    val textButtonWidth = remember { mutableStateOf(0f) }
    val buttonText = remember { mutableStateOf(initText) }
    val imageVector = if (expanded.value) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    Box(modifier = Modifier
        .weight(1f)
        .wrapContentHeight()) {

        TextButton(
            onClick = { expanded.value = !expanded.value },
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    textButtonWidth.value = coordinates.size.toSize().width
                }
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Image(painter = painterResource(imageResources[suggestions.indexOf(buttonText.value)]),
                contentDescription = null,
                modifier = Modifier
                    .height(40.dp)
                    .width(45.dp))
            Text(buttonText.value,
                style = TextStyle(color = Color.Black),
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Icon(
                imageVector = imageVector,
                contentDescription = null,
            )
        }

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier.width(with(LocalDensity.current) { textButtonWidth.value.toDp() })
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    expanded.value = false
                    buttonText.value = label
                }) {
                    Text(text = label)
                }
            }
        }
    }
}