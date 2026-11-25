package com.smartexpense.tracker.ui.auth

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartexpense.tracker.R
import com.smartexpense.tracker.ui.theme.SoftBorder

@Composable
internal fun SignUpScreen(
    busy: Boolean,
    onSubmit: (name: String, email: String, password: String, aiOptIn: Boolean) -> Unit,
    onGoToLogin: () -> Unit,
) {
    val focus = LocalFocusManager.current
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var agreeTerms by rememberSaveable { mutableStateOf(false) }
    var aiConsent by rememberSaveable { mutableStateOf(false) }

    var nameError by rememberSaveable { mutableStateOf<Int?>(null) }
    var emailError by rememberSaveable { mutableStateOf<Int?>(null) }
    var passwordError by rememberSaveable { mutableStateOf<Int?>(null) }
    var termsError by rememberSaveable { mutableStateOf<Int?>(null) }

    fun submit() {
        if (busy) return
        nameError = if (name.isNotBlank()) null else R.string.err_name
        emailError = if (isValidEmail(email)) null else R.string.err_email
        passwordError = if (isStrongEnough(password)) null else R.string.err_password_rule
        termsError = if (agreeTerms) null else R.string.err_terms
        if (nameError == null && emailError == null && passwordError == null && termsError == null) {
            focus.clearFocus()
            onSubmit(name.trim(), email.trim(), password, aiConsent)
        }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        FormTitle(stringResource(R.string.signup_title), stringResource(R.string.signup_subtitle))

        AuthField(
            value = name,
            onValueChange = { name = it; nameError = null },
            label = stringResource(R.string.label_name),
            placeholder = stringResource(R.string.hint_name),
            leadingIcon = Icons.Outlined.Person,
            error = nameError?.let { stringResource(it) },
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        )
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
        AuthField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = stringResource(R.string.label_password),
            placeholder = stringResource(R.string.hint_new_password),
            leadingIcon = Icons.Outlined.Lock,
            error = passwordError?.let { stringResource(it) },
            supporting = stringResource(R.string.password_rule),
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            isPassword = true,
            onImeAction = { submit() },
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, SoftBorder, RoundedCornerShape(12.dp))
                .padding(vertical = 4.dp),
        ) {
            CheckRow(
                checked = agreeTerms,
                onCheckedChange = { agreeTerms = it; termsError = null },
                text = stringResource(R.string.agree_terms),
            )
            CheckRow(
                checked = aiConsent,
                onCheckedChange = { aiConsent = it },
                text = stringResource(R.string.ai_consent),
                subtext = stringResource(R.string.ai_consent_optional),
            )
        }
        termsError?.let {
            Text(stringResource(it), color = com.smartexpense.tracker.ui.theme.ErrorRed, fontSize = 13.sp)
        }

        PrimaryButton(stringResource(R.string.btn_create), enabled = !busy, onClick = { submit() })

        SwitchModeRow(
            prompt = stringResource(R.string.have_account),
            action = stringResource(R.string.login_link),
            onClick = onGoToLogin,
        )
    }
}
