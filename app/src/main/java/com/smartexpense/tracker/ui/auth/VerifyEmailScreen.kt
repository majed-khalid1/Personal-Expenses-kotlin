package com.smartexpense.tracker.ui.auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.smartexpense.tracker.R

@Composable
internal fun VerifyEmailScreen(
    email: String,
    busy: Boolean,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit,
) {
    var code by remember { mutableStateOf("") }

    fun submit() {
        if (!busy && code.isNotBlank()) onVerify(code)
    }

    FormTitle(
        stringResource(R.string.auth_verify_title),
        stringResource(R.string.auth_verify_hint, email),
    )
    AuthField(
        value = code,
        onValueChange = { code = it.filter { char -> char in '0'..'9' } },
        label = stringResource(R.string.auth_email_code),
        placeholder = "",
        leadingIcon = Icons.Outlined.Lock,
        error = null,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Done,
        onImeAction = ::submit,
    )
    PrimaryButton(
        stringResource(R.string.auth_verify_button),
        enabled = !busy && code.isNotBlank(),
        onClick = ::submit,
    )
    TextButton(onClick = onResend, enabled = !busy) {
        Text(stringResource(R.string.auth_request_another_code))
    }
    TextButton(onClick = onBack, enabled = !busy) { Text(stringResource(R.string.login_link)) }
}
