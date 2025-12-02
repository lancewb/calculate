package com.card.calculate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.card.calculate.ui.screens.GameTableScreen
import com.card.calculate.ui.theme.CalculateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculateTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GameTableScreen()
                }
            }
        }
    }
}
