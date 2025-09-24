package com.kotlinonly.moprog.features.auth.presentation

import androidx.compose.material3.ElevatedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.kotlinonly.moprog.R
import com.kotlinonly.moprog.data.core.logD
import com.kotlinonly.moprog.data.core.logE
import kotlinx.coroutines.launch


@Composable
actual fun GoogleButton(modifier: Modifier, onGetCredential: suspend (idToken: String) -> Unit) {
    val context = LocalContext.current
    val defaultWebClientId = stringResource(R.string.default_web_client_id)
    val scope = rememberCoroutineScope()

    ElevatedButton(
        onClick = {
            logD("GoogleButton", "Google Sign In Clicked")
            val credentialManager = androidx.credentials.CredentialManager.create(context)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(defaultWebClientId)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            scope.launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = context
                    )

                    val credential = result.credential

                    if(credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        val idToken = googleIdTokenCredential.idToken
                        logD("GoogleButton", "Google Sign In Success: $idToken")

                        onGetCredential(idToken)
                    }
                } catch (e: Exception) {
                    logE("GoogleButton", "Error getting credential: ${e.message}")
                }
            }
        }
    ) {
        GoogleButtonContent()
    }
}
