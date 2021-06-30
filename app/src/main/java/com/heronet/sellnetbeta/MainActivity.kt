package com.heronet.sellnetbeta

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.heronet.sellnetbeta.ui.screen.MainScreen
import com.heronet.sellnetbeta.ui.theme.SellnetBetaTheme
import com.heronet.sellnetbeta.viewmodel.ProductsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SellnetBetaTheme {
                Surface(color = MaterialTheme.colors.background) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}
