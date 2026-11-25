package com.smartexpense.tracker.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.smartexpense.tracker.R
internal enum class AuthPage { SignIn, SignUp, VerifyEmail, ForgotPassword, SetNewPassword }

internal data class AuthUiState(
    val page: AuthPage = AuthPage.SignIn,
    val email: String = "",
    val busy: Boolean = false,
    val error: Int? = null,
    val notice: Int? = null,
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    val sessionStatus = repository.sessionStatus

    private val mutableUi = MutableStateFlow(AuthUiState())
    internal val ui = mutableUi.asStateFlow()

    internal fun goTo(page: AuthPage, email: String = "") {
        if (ui.value.busy) return
        mutableUi.value = AuthUiState(page = page, email = email)
    }

    internal fun signIn(email: String, password: String) = request(R.string.auth_signin_failed) {
        repository.signIn(email, password)
    }

    internal fun signUp(name: String, email: String, password: String, aiOptIn: Boolean) =
        request(R.string.auth_signup_failed) {
            repository.signUp(name, email, password, aiOptIn)
            if (sessionStatus.value !is SessionStatus.Authenticated) {
                mutableUi.update { it.copy(page = AuthPage.VerifyEmail, email = email) }
            }
        }

    internal fun verifyEmail(code: String) = request(R.string.auth_verify_failed) {
        repository.verifySignUp(ui.value.email, code)
    }

    internal fun resendSignUpCode() = request(R.string.auth_send_recovery_failed) {
        repository.resendSignUpCode(ui.value.email)
        mutableUi.update { it.copy(notice = R.string.auth_code_requested) }
    }

    internal fun sendRecoveryCode(email: String) = request(R.string.auth_send_recovery_failed) {
        repository.sendRecoveryCode(email)
        mutableUi.update { it.copy(page = AuthPage.SetNewPassword, email = email) }
    }

    internal fun resetPassword(code: String, password: String) = request(R.string.auth_reset_failed) {
        repository.resetPassword(ui.value.email, code, password)
        mutableUi.update {
            it.copy(page = AuthPage.SignIn, email = "", notice = R.string.auth_password_updated)
        }
    }

    fun signOut() = request(R.string.auth_signout_failed) {
        repository.signOut()
        mutableUi.value = AuthUiState()
    }

    private fun request(errorMessage: Int, action: suspend () -> Unit) {
        if (ui.value.busy) return
        mutableUi.update { it.copy(busy = true, error = null, notice = null) }
        viewModelScope.launch {
            try {
                action()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableUi.update { it.copy(error = errorMessage) }
            } finally {
                mutableUi.update { it.copy(busy = false) }
            }
        }
    }
}
