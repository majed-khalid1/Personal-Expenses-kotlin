package com.smartexpense.tracker.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartexpense.tracker.R
import com.smartexpense.tracker.ui.theme.BrandGreen
import com.smartexpense.tracker.ui.theme.Ground
import com.smartexpense.tracker.ui.theme.Ink
import com.smartexpense.tracker.ui.theme.Muted

@Composable
fun HomePlaceholder(email: String, onLogout: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Ground)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(stringResource(R.string.home_title), fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(stringResource(R.string.home_body, email), fontSize = 15.sp, color = Muted)
        OutlinedButton(onClick = onLogout) {
            Text(stringResource(R.string.logout), color = BrandGreen)
        }
    }
}
