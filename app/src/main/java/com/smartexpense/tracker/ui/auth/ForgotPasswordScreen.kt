package com.smartexpense.tracker.ui.auth

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.smartexpense.tracker.R

@Composable
internal fun ForgotPasswordScreen(
    initialEmail: String,
    busy: Boolean,
    onSend: (String) -> Unit,
    onBack: () -> Unit,
) {
    var email by rememberSaveable(initialEmail) { mutableStateOf(initialEmail) }
    var invalidEmail by rememberSaveable { mutableStateOf(false) }

    fun submit() {
        if (busy) return
        invalidEmail = !isValidEmail(email)
        if (!invalidEmail) onSend(email.trim())
    }

    FormTitle(stringResource(R.string.forgot_password), stringResource(R.string.auth_recovery_hint))
    AuthField(
        value = email,
        onValueChange = { email = it; invalidEmail = false },
        label = stringResource(R.string.label_email),
        placeholder = stringResource(R.string.hint_email),
        leadingIcon = Icons.Outlined.Email,
        error = if (invalidEmail) stringResource(R.string.err_email) else null,
        keyboardType = KeyboardType.Email,
        imeAction = ImeAction.Done,
        onImeAction = ::submit,
    )
    PrimaryButton(stringResource(R.string.auth_send_code), enabled = !busy, onClick = ::submit)
    TextButton(onClick = onBack, enabled = !busy) { Text(stringResource(R.string.login_link)) }
}
