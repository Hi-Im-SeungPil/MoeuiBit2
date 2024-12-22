package org.jeonfeel.moeuibit2.ui.main.portfolio.dialogs

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemoveCoinBottomSheet(removeCoinList: List<String>, hideSheet: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val checkedList = remember { removeCoinList.map { mutableStateOf(false) } }
    val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = {
            hideSheet()
        },
        modifier = Modifier.requiredHeightIn(max = 500.dp),
        dragHandle = null,
        shape = RoundedCornerShape(
            topStart = 20.dp, topEnd = 20.dp
        )
    ) {
        IconButton(onClick = {
            coroutineScope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    hideSheet()
                }
            }
        }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = null)
        }
        Text(text = "삭제할 코인을 선택해주세요.")
        Text(text = "전체선택")
        LazyColumn {
            itemsIndexed(removeCoinList) { index, it ->
                Row {
                    Checkbox(
                        checked = checkedList[index].value,
                        onCheckedChange = {
                            checkedList[index].value = it
                        })
                    Text(text = it, color = Color.Black)
                }
            }
        }
        Text(text = "삭제", modifier = Modifier.fillMaxWidth())
    }
}