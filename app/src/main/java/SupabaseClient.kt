package com.smartexpense.tracker
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

private const val SUPABASE_URL = "https://ipcgdmjpekzyzmwnrcnr.supabase.co"
private const val SUPABASE_PUBLISHABLE_KEY = "sb_publishable_Uk28uZb8bskAUyJz-s_BSA__O1OKF7L"

val supabase = createSupabaseClient(SUPABASE_URL, SUPABASE_PUBLISHABLE_KEY) {
    install(Auth)
    install(Postgrest)
}

internal fun createRecoveryClient() =
    createSupabaseClient(SUPABASE_URL, SUPABASE_PUBLISHABLE_KEY) {
        install(Auth) {
            autoLoadFromStorage = false
            autoSaveToStorage = false
            alwaysAutoRefresh = false
        }
    }
