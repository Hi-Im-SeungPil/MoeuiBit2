package org.jeonfeel.moeuibit2.view.activity.coindetail

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import org.jeonfeel.moeuibit2.R


class CoinDetailActivity : AppCompatActivity() {

    lateinit var coinKoreanName: String
    lateinit var coinSymbol: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initActivity()

        setContent {
            CoinDetailScreen()
        }
    }

    private fun initActivity() {
        coinKoreanName = intent.getStringExtra("coinKoreanName") ?: ""
        coinSymbol = intent.getStringExtra("coinSymbol") ?: ""
    }

    @Composable
    fun CoinDetailScreen() {
        Scaffold(
            topBar = {
                TopAppBar(
                    backgroundColor = colorResource(id = R.color.C0F0F5C),
                    title = {
                        Text(text = "${coinKoreanName}(${coinSymbol}/KRW)",
                            style = TextStyle(color = Color.White,
                                fontSize = 17.sp,
                                textAlign = TextAlign.Center),
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1)
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            finish()
                            overridePendingTransition(R.anim.none,R.anim.lazy_column_item_slide_right)
                        }) {
                            Icon(Icons.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(Icons.Outlined.Call, contentDescription = null, tint = Color.White)
                        }
                        IconButton(onClick = { /* doSomething() */ }) {
                            Icon(Icons.Outlined.Star, contentDescription = null, tint = Color.White)
                        }
                    }
                )
            },
        ) { contentPadding ->
            Box(modifier = Modifier.padding(contentPadding)) {

            }
            BackHandler(true) {
                finish()
                overridePendingTransition(R.anim.none,R.anim.lazy_column_item_slide_right)
            }
        }
    }
}