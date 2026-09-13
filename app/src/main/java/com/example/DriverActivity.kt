package com.example

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.data.model.UserRole
import com.example.ui.auth.AuthScreen
import com.example.ui.components.PermissionHandler
import com.example.ui.components.ProfileDialogManager
import com.example.ui.components.PushNotificationBanner
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.SosActiveOverlay
import com.example.ui.driver.DriverChatScreen
import com.example.ui.driver.DriverEarningsScreen
import com.example.ui.driver.DriverHomeScreen
import com.example.ui.driver.DriverProfileScreen
import com.example.ui.driver.DriverTripsScreen
import com.example.ui.driver.DriverBottomNav
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.SwiftRideViewModel
import com.example.security.BiometricGate

class DriverActivity : FragmentActivity() {

    private val viewModel: SwiftRideViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.setUserRole(UserRole.DRIVER) // Set role early
        enableEdgeToEdge()
        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                if (viewModel.hasAuthenticatedSession()) {
                    BiometricGate(viewModel, onFallback = { viewModel.logout(); SwiftRideDriverApp(viewModel) }) { SwiftRideDriverApp(viewModel) }
                } else {
                    SwiftRideDriverApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun SwiftRideDriverApp(viewModel: SwiftRideViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val driverTab by viewModel.driverTab.collectAsState()
    val currentNotification by viewModel.currentPushNotification.collectAsState()

    PermissionHandler()
    val receiptRide by viewModel.dialogReceiptRide.collectAsState()
    val isSosActive by viewModel.isSosActive.collectAsState()

    // Dialogs
    receiptRide?.let { ride ->
        ReceiptDialog(
            ride = ride,
            onDismiss = { viewModel.dismissReceipt() }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isLoggedIn || userRole != UserRole.DRIVER) {
            AuthScreen(
                viewModel = viewModel,
                lockedRole = UserRole.DRIVER,
                onLoginSuccess = {
                    viewModel.setUserRole(UserRole.DRIVER)
                }
            )
        } else {
            com.example.security.BiometricGate(
                viewModel = viewModel,
                onFallback = {
                    AuthScreen(
                        viewModel = viewModel,
                        lockedRole = UserRole.DRIVER,
                        onLoginSuccess = { viewModel.setUserRole(UserRole.DRIVER) }
                    )
                }
            ) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        DriverBottomNav(
                            currentTab = driverTab,
                            onTabSelected = { tab ->
                                viewModel.setDriverTab(tab)
                            }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        Crossfade(targetState = driverTab, label = "driver_tabs") { tab ->
                            when (tab) {
                                "Home" -> DriverHomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToTrips = { viewModel.setDriverTab("Trips") }
                                )
                                "Trips" -> DriverTripsScreen(viewModel = viewModel)
                                "Chat" -> DriverChatScreen(viewModel = viewModel)
                                "Earnings" -> DriverEarningsScreen(viewModel = viewModel)
                                "Profile" -> DriverProfileScreen(viewModel = viewModel)
                                else -> DriverHomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToTrips = { viewModel.setDriverTab("Trips") }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Push Notification Banner
        PushNotificationBanner(
            notification = currentNotification,
            onDismiss = { viewModel.dismissPushNotification() },
            onClick = {
                viewModel.setDriverTab("Home")
                viewModel.dismissPushNotification()
            }
        )

        if (isSosActive) {
            SosActiveOverlay(onDismiss = { viewModel.dismissSos() })
        }

        ProfileDialogManager(viewModel = viewModel)
    }
}
