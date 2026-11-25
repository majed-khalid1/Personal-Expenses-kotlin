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
internal fun SetNewPasswordScreen(
    email: String,
    busy: Boolean,
    onReset: (code: String, password: String) -> Unit,
    onRequestAnotherCode: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }

    fun submit() {
        if (busy || code.isBlank()) return
        passwordError = !isStrongEnough(password)
        if (!passwordError) onReset(code, password)
    }

    FormTitle(
        stringResource(R.string.auth_set_password_title),
        stringResource(R.string.auth_set_password_hint, email),
    )
    AuthField(
        value = code,
        onValueChange = { code = it.filter { char -> char in '0'..'9' } },
        label = stringResource(R.string.auth_email_code),
        placeholder = "",
        leadingIcon = Icons.Outlined.Lock,
        error = null,
        keyboardType = KeyboardType.Number,
        imeAction = ImeAction.Next,
    )
    AuthField(
        value = password,
        onValueChange = { password = it; passwordError = false },
        label = stringResource(R.string.label_password),
        placeholder = stringResource(R.string.hint_new_password),
        leadingIcon = Icons.Outlined.Lock,
        error = if (passwordError) stringResource(R.string.err_password_rule) else null,
        keyboardType = KeyboardType.Password,
        imeAction = ImeAction.Done,
        supporting = stringResource(R.string.password_rule),
        isPassword = true,
        onImeAction = ::submit,
    )
    PrimaryButton(
        stringResource(R.string.auth_save_password),
        enabled = !busy && code.isNotBlank(),
        onClick = ::submit,
    )
    TextButton(onClick = onRequestAnotherCode, enabled = !busy) {
        Text(stringResource(R.string.auth_request_another_code))
    }
}
