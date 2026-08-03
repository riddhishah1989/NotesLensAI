package com.portfolio.notelensai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.portfolio.notelensai.presentation.NoteLensApp
import com.portfolio.notelensai.presentation.theme.NoteLensTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            NoteLensTheme {
                NoteLensApp()
            }
        }
    }
}