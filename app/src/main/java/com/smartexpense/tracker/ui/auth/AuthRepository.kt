package com.smartexpense.tracker.ui.auth
import com.smartexpense.tracker.createRecoveryClient
import com.smartexpense.tracker.supabase
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


import com.smartexpense.tracker.createRecoveryClient
import com.smartexpense.tracker.supabase
internal class AuthRepository {
    private val auth = supabase.auth
    val sessionStatus = auth.sessionStatus

    suspend fun signIn(emailAddress: String, enteredPassword: String) {
        auth.signInWith(Email) {
            email = emailAddress
            password = enteredPassword
        }
    }

    suspend fun signUp(
        name: String,
        emailAddress: String,
        enteredPassword: String,
        aiOptIn: Boolean,
    ) {
        auth.signUpWith(Email) {
            email = emailAddress
            password = enteredPassword
            data = buildJsonObject {
                put("full_name", name)
                put("ai_opt_in", aiOptIn)
            }
        }
    }

    suspend fun verifySignUp(email: String, code: String) {
        auth.verifyEmailOtp(type = OtpType.Email.EMAIL, email = email, token = code)
    }

    suspend fun resendSignUpCode(email: String) {
        auth.resendEmail(type = OtpType.Email.SIGNUP, email = email)
    }

    suspend fun sendRecoveryCode(email: String) {
        auth.resetPasswordForEmail(email = email)
    }

    suspend fun resetPassword(email: String, code: String, newPassword: String) {
        val recoveryClient = createRecoveryClient()
        try {
            recoveryClient.auth.awaitInitialization()
            recoveryClient.auth.verifyEmailOtp(
                type = OtpType.Email.RECOVERY,
                email = email,
                token = code,
            )
            recoveryClient.auth.updateUser { password = newPassword }
        } finally {
            withContext(NonCancellable) { recoveryClient.close() }
        }
    }

    suspend fun signOut() {
        auth.signOut()
    }
}
