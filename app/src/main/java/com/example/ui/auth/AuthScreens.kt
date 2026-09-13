package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.components.SwiftRideLogoBadge
import com.example.ui.theme.SwiftBorder
import com.example.ui.theme.SwiftDark
import com.example.ui.theme.SwiftGold
import com.example.ui.theme.SwiftGoldDark
import com.example.ui.theme.SwiftGoldLight
import com.example.ui.theme.SwiftGreen
import com.example.ui.theme.SwiftTextMuted
import com.example.ui.theme.SwiftTextPrimary
import com.example.ui.theme.SwiftTextSecondary
import kotlinx.coroutines.delay
import com.example.ui.theme.SwiftWhite
import com.example.ui.viewmodel.SwiftRideViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

@Composable
fun AuthScreen(
    viewModel: SwiftRideViewModel,
    lockedRole: UserRole? = null,
    onLoginSuccess: () -> Unit
) {
    var isSignUp by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(lockedRole ?: UserRole.PASSENGER) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = SwiftWhite
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            SwiftRideLogoBadge(showSubtitle = true)
            Spacer(modifier = Modifier.height(20.dp))

            // Only show Role Switcher if not locked
            if (lockedRole == null) {
                RoleSwitcher(
                    selectedRole = selectedRole,
                    onRoleSelected = {
                        selectedRole = it
                        viewModel.setUserRole(it)
                    }
                )
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                // Ensure ViewModel is in sync with the locked role
                viewModel.setUserRole(lockedRole)
            }

            if (!isSignUp) {
                LoginView(
                    selectedRole = selectedRole,
                    viewModel = viewModel,
                    onLoginSuccess = onLoginSuccess,
                    onSwitchToSignUp = { isSignUp = true }
                )
            } else {
                SignUpView(
                    selectedRole = selectedRole,
                    viewModel = viewModel,
                    onSignUpSuccess = onLoginSuccess,
                    onSwitchToLogin = { isSignUp = false }
                )
            }
        }
    }
}

@Composable
fun RoleSwitcher(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFF2F4F7))
            .border(1.dp, SwiftBorder, RoundedCornerShape(24.dp))
            .padding(4.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selectedRole == UserRole.PASSENGER) SwiftGold else Color.Transparent)
                    .clickable { onRoleSelected(UserRole.PASSENGER) }
                    .padding(vertical = 8.dp)
                    .testTag("role_passenger_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "👤 PASSENGER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedRole == UserRole.PASSENGER) SwiftDark else SwiftTextSecondary
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selectedRole == UserRole.DRIVER) SwiftGold else Color.Transparent)
                    .clickable { onRoleSelected(UserRole.DRIVER) }
                    .padding(vertical = 8.dp)
                    .testTag("role_driver_btn"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🚗 DRIVER",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (selectedRole == UserRole.DRIVER) SwiftDark else SwiftTextSecondary
                )
            }
        }
    }
}

@Composable
private fun LoginView(
    selectedRole: UserRole,
    viewModel: SwiftRideViewModel,
    onLoginSuccess: () -> Unit,
    onSwitchToSignUp: () -> Unit
) {
    val context = LocalContext.current as android.app.Activity
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome Back!",
            fontFamily = com.example.ui.theme.PoppinsFontFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = SwiftDark,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Enter your details to log in",
            fontSize = 13.sp,
            color = SwiftTextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (errorText != null) {
            Text(
                text = errorText!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Address") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = SwiftGoldDark)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = SwiftGoldDark)
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = {
                if (email.isBlank() || password.isBlank()) {
                    errorText = "Please fill in all fields"
                } else {
                    errorText = null
                    viewModel.loginWithEmail(email, password) { success, msg ->
                        if (success) {
                            onLoginSuccess()
                        } else {
                            errorText = msg
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SwiftGold,
                contentColor = SwiftDark
            )
        ) {
            Text(
                text = "Log In",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        SocialLoginButton(
            title = "Sign In with Google",
            icon = "G",
            onClick = {
                errorText = null
                handleGoogleSignIn(
                    context = context,
                    scope = scope,
                    viewModel = viewModel,
                    onSuccess = onLoginSuccess,
                    onError = { errorText = it }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Don't have an account? ",
                fontSize = 13.sp,
                color = SwiftTextSecondary
            )
            Text(
                text = "Sign Up",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SwiftGoldDark,
                modifier = Modifier
                    .clickable { onSwitchToSignUp() }
                    .testTag("switch_to_signup")
            )
        }
    }
}

@Composable
private fun SignUpView(
    selectedRole: UserRole,
    viewModel: SwiftRideViewModel,
    onSignUpSuccess: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    val context = LocalContext.current as android.app.Activity
    val name by viewModel.registrationName.collectAsState()
    val email by viewModel.registrationEmail.collectAsState()
    val password by viewModel.registrationPassword.collectAsState()
    val confirmPassword by viewModel.registrationConfirmPassword.collectAsState()
    val vehicleDetails by viewModel.registrationVehicleDetails.collectAsState()
    
    var errorText by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Account",
            fontFamily = com.example.ui.theme.PoppinsFontFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = SwiftDark,
            modifier = Modifier.align(Alignment.Start)
        )
        Text(
            text = "Enter your details to register",
            fontSize = 13.sp,
            color = SwiftTextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorText != null) {
            Text(
                text = errorText!!,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        OutlinedTextField(
            value = name,
            onValueChange = { viewModel.updateRegistrationName(it) },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { viewModel.updateRegistrationEmail(it) },
            label = { Text("Email Address") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { viewModel.updateRegistrationPassword(it) },
            label = { Text("Password") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, null)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        
        // Strength Indicator
        val criteria = viewModel.getPasswordStrengthCriteria(password)
        val metCount = criteria.values.count { it }
        val strengthColor = when (metCount) {
            4 -> SwiftGreen
            3 -> Color(0xFFFF9800) // Orange
            else -> Color.Red
        }

        LinearProgressIndicator(
            progress = { metCount / 4f },
            modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
            color = strengthColor,
            trackColor = SwiftBorder
        )
        
        Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            criteria.forEach { (critName, met) ->
                Text(
                    text = critName,
                    fontSize = 10.sp,
                    color = if (met) SwiftGreen else SwiftTextMuted
                )
            }
        }
        
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { viewModel.updateRegistrationConfirmPassword(it) },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        if (selectedRole == UserRole.DRIVER) {
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value = vehicleDetails,
                onValueChange = { viewModel.updateRegistrationVehicleDetails(it) },
                label = { Text("Vehicle Details") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                if (name.isBlank() || email.isBlank() || password.isBlank()) {
                    errorText = "Please fill in all required fields"
                } else if (password != confirmPassword) {
                    errorText = "Passwords do not match"
                } else if (!viewModel.isPasswordStrong(password)) {
                    errorText = "Password is not strong enough"
                } else {
                    errorText = null
                    viewModel.signUpWithEmail(email, password, name) { success, msg ->
                        if (success) {
                            onSignUpSuccess()
                        } else {
                            errorText = msg
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SwiftGold, contentColor = SwiftDark)
        ) {
            Text("Sign Up", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        SocialLoginButton(
            title = "Sign Up with Google",
            icon = "G",
            onClick = {
                errorText = null
                handleGoogleSignIn(
                    context = context,
                    scope = scope,
                    viewModel = viewModel,
                    onSuccess = onSignUpSuccess,
                    onError = { errorText = it }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Already have an account? ", fontSize = 13.sp, color = SwiftTextSecondary)
            Text(
                "Log In",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = SwiftGoldDark,
                modifier = Modifier.clickable { onSwitchToLogin() }
            )
        }
    }
}

@Composable
private fun SocialLoginButton(title: String, icon: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = SwiftTextPrimary),
        border = androidx.compose.foundation.BorderStroke(1.dp, SwiftBorder)
    ) {
        Text(
            text = icon,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun handleGoogleSignIn(
    context: android.content.Context,
    scope: kotlinx.coroutines.CoroutineScope,
    viewModel: SwiftRideViewModel,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val credentialManager = CredentialManager.create(context)
    
    val googleIdOption = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId("678826789099-50tqt8u0j9fbake2l185jvslocj8isjq.apps.googleusercontent.com")
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(googleIdOption)
        .build()

    scope.launch {
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            val credential = result.credential
            
            if (credential is androidx.credentials.CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                
                viewModel.loginWithGoogle(firebaseCredential) { success, msg ->
                    if (success) {
                        onSuccess()
                    } else {
                        onError(msg ?: "Google Sign-In failed")
                    }
                }
            } else {
                onError("Unexpected credential type.")
            }
        } catch (e: GetCredentialException) {
            onError(e.localizedMessage ?: "Google Sign-In canceled or failed")
        } catch (e: Exception) {
            onError(e.localizedMessage ?: "An error occurred during Google Sign-In")
        }
    }
}
