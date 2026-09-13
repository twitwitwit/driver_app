package com.example.security

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.example.ui.viewmodel.SwiftRideViewModel
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@Composable
fun BiometricGate(viewModel: SwiftRideViewModel, onFallback: @Composable () -> Unit, content: @Composable () -> Unit) {
    var unlocked by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val activity = context as? FragmentActivity
        val manager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
        if (activity == null || manager.canAuthenticate(authenticators) != BiometricManager.BIOMETRIC_SUCCESS) {
            failed = true
            return@LaunchedEffect
        }
        val executor: Executor = Executors.newSingleThreadExecutor()
        val prompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                if (viewModel.unlockWithBiometric()) unlocked = true else failed = true
            }
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) { failed = true }
        })
        prompt.authenticate(BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock SwiftRide")
            .setSubtitle("Confirm your identity to continue")
            .setAllowedAuthenticators(authenticators)
            .build())
    }

    when {
        unlocked -> content()
        failed -> onFallback()
        else -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Confirm your identity to unlock SwiftRide")
        }
    }
}
