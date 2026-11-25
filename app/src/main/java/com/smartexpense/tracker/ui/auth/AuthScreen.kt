package com.smartexpense.tracker.ui.auth

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import io.github.jan.supabase.auth.status.SessionStatus

@Composable
fun ExpenseAuthGate(
    authViewModel: AuthViewModel = viewModel(),
    signedInContent: @Composable (String, () -> Unit) -> Unit =
        { email, onLogout ->
            HomePlaceholder(email = email, onLogout = onLogout)
        },
) {
    val session by authViewModel.sessionStatus.collectAsState()
    val ui by authViewModel.ui.collectAsState()

    when (val status = session) {
        SessionStatus.Initializing -> LinearProgressIndicator()

        is SessionStatus.Authenticated -> {
            Column {
                if (ui.busy) {
                    LinearProgressIndicator()
                }

                ui.error?.let { messageId ->
                    Text(
                        text = stringResource(messageId),
                        color = MaterialTheme.colorScheme.error,
                    )
                }

                signedInContent(
                    status.session.user?.email.orEmpty(),
                    authViewModel::signOut,
                )
            }
        }

        is SessionStatus.NotAuthenticated -> {
            AuthPages(model = authViewModel, ui = ui)
        }

        is SessionStatus.RefreshFailure -> SessionErrorMessage()
    }
}

@Composable
fun AuthScreen(
    onAuthenticated: (email: String) -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = viewModel(),
) {
    val session by authViewModel.sessionStatus.collectAsState()
    val ui by authViewModel.ui.collectAsState()
    val notifyAuthenticated by rememberUpdatedState(onAuthenticated)

    when (val status = session) {
        SessionStatus.Initializing -> LinearProgressIndicator()

        is SessionStatus.Authenticated -> {
            val user = status.session.user
            LaunchedEffect(user?.id) {
                if (user != null) {
                    notifyAuthenticated(user.email.orEmpty())
                }
            }
        }

        is SessionStatus.NotAuthenticated -> {
            AuthPages(model = authViewModel, ui = ui, modifier = modifier)
        }

        is SessionStatus.RefreshFailure -> SessionErrorMessage()
    }
}

@Composable
private fun AuthPages(
    model: AuthViewModel,
    ui: AuthUiState,
    modifier: Modifier = Modifier,
) {
    val goToSignIn = { model.goTo(AuthPage.SignIn) }

    BackHandler(enabled = ui.page != AuthPage.SignIn) {
        if (ui.page == AuthPage.SetNewPassword) {
            model.goTo(AuthPage.ForgotPassword, ui.email)
        } else {
            goToSignIn()
        }
    }

    AuthLayout(modifier = modifier) {
        if (ui.page == AuthPage.SignIn || ui.page == AuthPage.SignUp) {
            ModeTabs(mode = ui.page) { page ->
                model.goTo(page)
            }
        }

        when (ui.page) {
            AuthPage.SignIn -> SignInScreen(
                busy = ui.busy,
                onSubmit = model::signIn,
                onGoToSignUp = { model.goTo(AuthPage.SignUp) },
                onForgotPassword = { email ->
                    model.goTo(AuthPage.ForgotPassword, email)
                },
            )

            AuthPage.SignUp -> SignUpScreen(
                busy = ui.busy,
                onSubmit = model::signUp,
                onGoToLogin = goToSignIn,
            )

            AuthPage.VerifyEmail -> VerifyEmailScreen(
                email = ui.email,
                busy = ui.busy,
                onVerify = model::verifyEmail,
                onResend = model::resendSignUpCode,
                onBack = goToSignIn,
            )

            AuthPage.ForgotPassword -> ForgotPasswordScreen(
                initialEmail = ui.email,
                busy = ui.busy,
                onSend = model::sendRecoveryCode,
                onBack = goToSignIn,
            )

            AuthPage.SetNewPassword -> SetNewPasswordScreen(
                email = ui.email,
                busy = ui.busy,
                onReset = model::resetPassword,
            ) {
                model.goTo(AuthPage.ForgotPassword, ui.email)
            }
        }

        if (ui.busy) {
            LinearProgressIndicator()
        }

        ui.error?.let { messageId ->
            Text(
                text = stringResource(messageId),
                color = MaterialTheme.colorScheme.error,
            )
        }

        ui.notice?.let { messageId ->
            Text(text = stringResource(messageId))
        }
    }
}

@Composable
private fun SessionErrorMessage() {
    Text(
        text = "Your session could not be restored. Check your internet connection.",
        color = MaterialTheme.colorScheme.error,
    )
}