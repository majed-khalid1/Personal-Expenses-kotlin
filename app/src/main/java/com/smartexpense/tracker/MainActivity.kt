package com.smartexpense.tracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.smartexpense.tracker.ui.auth.AuthScreen
import com.smartexpense.tracker.ui.auth.HomePlaceholder
import com.smartexpense.tracker.ui.theme.SmartExpenseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Light icons on the dark green header.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        setContent {
            SmartExpenseTheme {
                // Signed-in email; null means show the Login / Sign up screen.
                var signedInAs by rememberSaveable { mutableStateOf<String?>(null) }
                val user = signedInAs
                if (user == null) {
                    AuthScreen(onAuthenticated = { email -> signedInAs = email })
                } else {
                    HomePlaceholder(email = user, onLogout = { signedInAs = null })
                }
            }
        }
    }
}
