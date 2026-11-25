package com.smartexpense.tracker.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.smartexpense.tracker.R
import com.smartexpense.tracker.ui.theme.BrandGreen

@Composable
internal fun SignInScreen(
    busy: Boolean,
    onSubmit: (email: String, password: String) -> Unit,
    onGoToSignUp: () -> Unit,
    onForgotPassword: (email: String) -> Unit,
) {
    val focus = LocalFocusManager.current
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf<Int?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<Int?>(null) }

    fun submit() {
        if (busy) return
        emailError = if (isValidEmail(email)) null else R.string.err_email
        passwordError = if (password.isNotEmpty()) null else R.string.err_password_empty
        if (emailError == null && passwordError == null) {
            focus.clearFocus()
            onSubmit(email.trim(), password)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FormTitle(stringResource(R.string.login_title), stringResource(R.string.login_subtitle))

        AuthField(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = stringResource(R.string.label_email),
            placeholder = stringResource(R.string.hint_email),
            leadingIcon = Icons.Outlined.Email,
            error = emailError?.let { stringResource(it) },
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        )
        Column {
            AuthField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = stringResource(R.string.label_password),
                placeholder = stringResource(R.string.hint_password),
                leadingIcon = Icons.Outlined.Lock,
                error = passwordError?.let { stringResource(it) },
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
                isPassword = true,
                onImeAction = { submit() },
            )
            TextButton(
                onClick = { onForgotPassword(email.trim()) },
                enabled = !busy,
                modifier = Modifier.align(Alignment.End),
            ) {
                Text(stringResource(R.string.forgot_password), color = BrandGreen, fontWeight = FontWeight.Medium)
            }
        }

        PrimaryButton(stringResource(R.string.btn_login), enabled = !busy, onClick = { submit() })

        SwitchModeRow(
            prompt = stringResource(R.string.new_here),
            action = stringResource(R.string.create_account_link),
            onClick = onGoToSignUp,
        )
    }
}
