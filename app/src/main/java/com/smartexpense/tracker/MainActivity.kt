package com.smartexpense.tracker

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.smartexpense.tracker.ui.auth.ExpenseAuthGate
import com.smartexpense.tracker.ui.theme.SmartExpenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            SmartExpenseTheme {
                ExpenseAuthGate()
            }
        }
    }
}