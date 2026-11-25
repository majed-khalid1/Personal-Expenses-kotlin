package com.smartexpense.tracker.ui.auth

import android.util.Patterns
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.smartexpense.tracker.R
import com.smartexpense.tracker.ui.theme.BrandDark
import com.smartexpense.tracker.ui.theme.BrandGreen
import com.smartexpense.tracker.ui.theme.FieldBorder
import com.smartexpense.tracker.ui.theme.Ground
import com.smartexpense.tracker.ui.theme.Ink
import com.smartexpense.tracker.ui.theme.LabelInk
import com.smartexpense.tracker.ui.theme.Mint
import com.smartexpense.tracker.ui.theme.Muted
import com.smartexpense.tracker.ui.theme.TabTrack

@Composable
internal fun AuthLayout(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Ground)
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        Header()

        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .offset(y = (-28).dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                content()
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                Icons.Outlined.Shield,
                contentDescription = null,
                tint = Muted,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.size(8.dp))
            Text(stringResource(R.string.privacy_footer), fontSize = 13.sp, color = Muted)
        }
    }
}

@Composable
private fun Header() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(BrandDark)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 60.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Mint),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    Icons.Outlined.AccountBalanceWallet,
                    contentDescription = null,
                    tint = BrandDark,
                    modifier = Modifier.size(20.dp),
                )
            }
            Spacer(Modifier.size(10.dp))
            Text(
                stringResource(R.string.app_name),
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )
        }
        Text(
            stringResource(R.string.tagline),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 36.sp,
        )
    }
}

@Composable
internal fun FormTitle(title: String, subtitle: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Ink)
        Text(subtitle, fontSize = 15.sp, color = Muted)
    }
}

@Composable
internal fun AuthField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    error: String?,
    keyboardType: KeyboardType,
    imeAction: ImeAction,
    supporting: String? = null,
    isPassword: Boolean = false,
    onImeAction: () -> Unit = {},
) {
    var visible by remember { mutableStateOf(false) }
    val focus = LocalFocusManager.current

    val supportText = error ?: supporting
    val supportingContent: (@Composable () -> Unit)? =
        if (supportText != null) {
            { Text(supportText) }
        } else {
            null
        }

    val trailing: (@Composable () -> Unit)? =
        if (isPassword) {
            {
                IconButton(onClick = { visible = !visible }) {
                    Icon(
                        imageVector = if (visible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = stringResource(
                            if (visible) R.string.hide_password else R.string.show_password
                        ),
                    )
                }
            }
        } else {
            null
        }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = LabelInk)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder) },
            leadingIcon = { Icon(leadingIcon, contentDescription = null) },
            trailingIcon = trailing,
            supportingText = supportingContent,
            isError = error != null,
            singleLine = true,
            visualTransformation = if (isPassword && !visible) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
            keyboardActions = KeyboardActions(
                onNext = { focus.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) },
                onDone = { onImeAction() },
            ),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = FieldBorder,
                focusedBorderColor = BrandGreen,
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                errorContainerColor = Color.White,
                unfocusedLeadingIconColor = Muted,
                focusedLeadingIconColor = BrandGreen,
                unfocusedTrailingIconColor = Muted,
                focusedTrailingIconColor = Muted,
                unfocusedPlaceholderColor = Color(0xFF7A8783),
                focusedPlaceholderColor = Color(0xFF7A8783),
                unfocusedSupportingTextColor = Muted,
                focusedSupportingTextColor = Muted,
            ),
        )
    }
}

@Composable
internal fun CheckRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text: String,
    subtext: String? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .toggleable(value = checked, onValueChange = onCheckedChange, role = Role.Checkbox)
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(checkedColor = BrandGreen),
            modifier = Modifier.padding(12.dp),
        )
        Column(
            modifier = Modifier.padding(top = 10.dp, end = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text, fontSize = 14.sp, color = LabelInk, lineHeight = 20.sp)
            if (subtext != null) {
                Text(subtext, fontSize = 13.sp, color = Muted, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
internal fun PrimaryButton(text: String, enabled: Boolean = true, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = BrandGreen, contentColor = Color.White),
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun SwitchModeRow(prompt: String, action: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(prompt, fontSize = 14.sp, color = Muted)
        TextButton(onClick = onClick) {
            Text(action, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = BrandGreen)
        }
    }
}

internal fun isValidEmail(email: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

internal fun isStrongEnough(password: String): Boolean =
    password.length >= 8 && password.any { it.isDigit() }

@Composable
internal fun ModeTabs(mode: AuthPage, onSelect: (AuthPage) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(TabTrack)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TabItem(stringResource(R.string.tab_login), mode == AuthPage.SignIn, { onSelect(AuthPage.SignIn) }, Modifier.weight(1f))
        TabItem(stringResource(R.string.tab_signup), mode == AuthPage.SignUp, { onSelect(AuthPage.SignUp) }, Modifier.weight(1f))
    }
}

@Composable
private fun TabItem(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .heightIn(min = 44.dp)
            .clip(RoundedCornerShape(9.dp))
            .background(if (selected) Color.White else Color.Transparent)
            .selectable(selected = selected, onClick = onClick, role = Role.Tab),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) Ink else Muted,
        )
    }
}
