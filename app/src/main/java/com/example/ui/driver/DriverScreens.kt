package com.example.ui.driver

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Message
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.RideStatus
import com.example.data.model.UserRole
import com.example.ui.components.LeafletMapView
import com.example.ui.components.WithdrawEarningsDialog
import com.example.ui.theme.SwiftBorder
import com.example.ui.theme.SwiftDark
import com.example.ui.theme.SwiftGold
import com.example.ui.theme.SwiftGoldBorder
import com.example.ui.theme.SwiftGoldDark
import com.example.ui.theme.SwiftGoldLight
import com.example.ui.theme.SwiftGrayBg
import com.example.ui.theme.SwiftGreen
import com.example.ui.theme.SwiftGreenBg
import com.example.ui.theme.SwiftGreenText
import com.example.ui.theme.SwiftRed
import com.example.ui.theme.SwiftRedBg
import com.example.ui.theme.SwiftRedText
import com.example.ui.theme.SwiftTextMuted
import com.example.ui.theme.SwiftTextPrimary
import com.example.ui.theme.SwiftTextSecondary
import com.example.ui.theme.SwiftWhite
import com.example.ui.viewmodel.SwiftRideViewModel

// ==========================================
// 0. DRIVER ACTIVE RIDE SCREEN (Navigation Mode)
// ==========================================
@Composable
fun DriverActiveRideScreen(
    viewModel: SwiftRideViewModel
) {
    val activeRide by viewModel.activeRide.collectAsState()
    val rideStatus by viewModel.rideStatus.collectAsState()
    val gpsProgress by viewModel.gpsProgress.collectAsState()
    val etaMinutes by viewModel.etaMinutes.collectAsState()

    val ride = activeRide ?: return

    Box(modifier = Modifier.fillMaxSize()) {
        // Real-Time Leaflet Map View as Background
        LeafletMapView(
            progress = gpsProgress,
            pickupTitle = ride.pickupAddress,
            dropoffTitle = ride.dropoffAddress,
            driverName = ride.driverName,
            vehiclePlate = ride.vehiclePlate,
            modifier = Modifier.fillMaxSize()
        )

        // Floating Header: Navigation Info
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftDark),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (rideStatus == RideStatus.DRIVER_ARRIVED) Icons.Default.CheckCircle else Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = SwiftGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = when (rideStatus) {
                                RideStatus.ACCEPTED -> "Head to Pickup"
                                RideStatus.DRIVER_ARRIVED -> "Waiting for Passenger"
                                RideStatus.IN_PROGRESS -> "Navigating to Destination"
                                else -> "Active Trip"
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwiftWhite
                        )
                        Text(
                            text = if (rideStatus == RideStatus.IN_PROGRESS) "To: ${ride.dropoffAddress}" else "From: ${ride.pickupAddress}",
                            fontSize = 11.sp,
                            color = Color(0xFFD0D5DD),
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    Text(
                        text = if (etaMinutes > 0) "$etaMinutes min" else "Arrived",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = SwiftGold
                    )
                }
            }
        }

        // Bottom Passenger Details & Actions
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftWhite),
            elevation = CardDefaults.cardElevation(10.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(SwiftGoldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ride.passengerName.take(2).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = SwiftGoldDark
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = ride.passengerName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwiftTextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = SwiftGold, modifier = Modifier.size(14.dp))
                            Text(text = " ${ride.passengerRating} • 5.0 Rating", fontSize = 12.sp, color = SwiftTextSecondary)
                        }
                    }
                    IconButton(onClick = { 
                        viewModel.triggerPushNotification("Calling Passenger 📞", "Dialing ${ride.passengerName} (${ride.passengerPhone})...", "INFO")
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = SwiftGreen)
                    }
                    IconButton(onClick = { 
                        viewModel.setDriverTab("Chat")
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Chat", tint = SwiftGoldDark)
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { viewModel.triggerSos() },
                        modifier = Modifier
                            .weight(1f)
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SwiftRed)
                    ) {
                        Text("SOS", color = SwiftWhite, fontWeight = FontWeight.Bold)
                    }
                    
                    Button(
                        onClick = { /* Simulation is automatic */ },
                        modifier = Modifier
                            .weight(2f)
                            .height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (rideStatus == RideStatus.IN_PROGRESS) SwiftGreen else SwiftGold,
                            contentColor = if (rideStatus == RideStatus.IN_PROGRESS) SwiftWhite else SwiftDark
                        )
                    ) {
                        Text(
                            text = when (rideStatus) {
                                RideStatus.ACCEPTED -> "Navigate to Pickup"
                                RideStatus.DRIVER_ARRIVED -> "Arrived at Pickup"
                                RideStatus.IN_PROGRESS -> "Navigating..."
                                else -> "Ongoing Trip"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 1. DRIVER HOME SCREEN (Slide 8)
// ==========================================
@Composable
fun DriverHomeScreen(
    viewModel: SwiftRideViewModel,
    onNavigateToTrips: () -> Unit
) {
    val isOnline by viewModel.isDriverOnline.collectAsState()
    val isVerified by viewModel.isDriverVerified.collectAsState()
    val earnings by viewModel.driverWalletBalance.collectAsState()
    val incomingRequest by viewModel.incomingDriverRequest.collectAsState()
    val countdown by viewModel.incomingCountdown.collectAsState()
    val activeRide by viewModel.activeRide.collectAsState()
    val rideStatus by viewModel.rideStatus.collectAsState()
    val gpsProgress by viewModel.gpsProgress.collectAsState()
    val etaMinutes by viewModel.etaMinutes.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val allRides by viewModel.allRides.collectAsState(initial = emptyList())
    val completedTripsCount = allRides.count { it.status == "COMPLETED" }
    val avgRating = if (completedTripsCount == 0) "—" else "${"%.1f".format(allRides.filter { it.status == "COMPLETED" && it.passengerRated }.map { it.ratingGiven }.average().takeIf { !it.isNaN() } ?: 0.0)} ★"
    
    var showVerificationScreen by remember { mutableStateOf(false) }

    if (showVerificationScreen) {
        DriverDocumentVerificationScreen(
            onBack = { showVerificationScreen = false },
            onVerify = { 
                viewModel.verifyDriver()
                showVerificationScreen = false
            }
        )
        return
    }

    if (activeRide != null && (rideStatus == RideStatus.ACCEPTED || rideStatus == RideStatus.DRIVER_ARRIVED || rideStatus == RideStatus.IN_PROGRESS)) {
        DriverActiveRideScreen(viewModel = viewModel)
        return
    }

    val firstName = userName.split(" ").firstOrNull() ?: "Driver"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 24.dp)
    ) {
        // Driver greeting & Online status (Slide 8)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Good Morning, $firstName!",
                    fontFamily = com.example.ui.theme.PoppinsFontFamily,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = SwiftDark
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (isOnline) SwiftGreen else SwiftRed)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOnline) "You're Online" else "You're Offline",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isOnline) SwiftGreenText else SwiftRedText
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { 
                    if (isVerified) {
                        viewModel.toggleDriverOnline()
                    } else {
                        viewModel.triggerPushNotification(
                            title = "Verification Required 🔒",
                            message = "Please complete your document verification before going online.",
                            type = "RIDE_UPDATE"
                        )
                    }
                }) {
                    Icon(
                        imageVector = Icons.Default.PowerSettingsNew,
                        contentDescription = "Toggle Online",
                        tint = if (isOnline) SwiftGreenText else SwiftRedText
                    )
                }
            }
        }

        if (!isVerified) {
            val docs by viewModel.driverDocuments.collectAsState()
            
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { 
                        viewModel.showProfileDialog("Documents")
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftRedBg),
                border = androidx.compose.foundation.BorderStroke(1.dp, SwiftRed.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = SwiftRed)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Complete Verification", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SwiftRed)
                        val missingDocs = docs.filter { it.status == "Expired" || it.status == "Missing" }
                        Text(
                            text = if (missingDocs.isNotEmpty()) "Required: ${missingDocs.joinToString { it.title }}" else "Documents pending review",
                            fontSize = 11.sp, 
                            color = SwiftTextSecondary
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = SwiftRed)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Today's Earnings Card (Slide 8)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftDark)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Today's Earnings", fontSize = 12.sp, color = Color(0xFFD0D5DD))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₱${"%.2f".format(earnings)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = SwiftGold
                    )
                }
                Image(
                    painter = painterResource(id = R.drawable.hero_car_banner),
                    contentDescription = "Vehicle",
                    modifier = Modifier
                        .size(96.dp, 56.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stat Row: Completed Trips, Online Time, Average Rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SwiftGoldDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "$completedTripsCount", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SwiftTextPrimary)
                    Text(text = "Completed Trips", fontSize = 10.sp, color = SwiftTextSecondary)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = SwiftGoldDark, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = if (isOnline) "Active" else "0h 0m", fontSize = 16.sp, fontWeight = FontWeight.Black, color = SwiftTextPrimary)
                    Text(text = "Online Time", fontSize = 10.sp, color = SwiftTextSecondary)
                }
            }

            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = SwiftGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = avgRating, fontSize = 16.sp, fontWeight = FontWeight.Black, color = SwiftTextPrimary)
                    Text(text = "Average Rating", fontSize = 10.sp, color = SwiftTextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Offline status notice
        if (!isOnline && activeRide == null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                border = androidx.compose.foundation.BorderStroke(1.dp, SwiftBorder)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(SwiftRedBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = SwiftRed, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "You are currently Offline",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwiftTextPrimary
                        )
                        Text(
                            text = if (!isVerified) "Complete document verification to go online." else "Toggle online above to start receiving ride requests.",
                            fontSize = 11.sp,
                            color = SwiftTextSecondary
                        )
                    }
                    if (!isVerified) {
                        TextButton(
                            onClick = { showVerificationScreen = true }
                        ) {
                            Text("Verify", color = SwiftGoldDark, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // New Ride Request Card with 24s timer (Slide 8)
        AnimatedVisibility(visible = isOnline && incomingRequest != null) {
            val req = incomingRequest
            if (req != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("driver_incoming_request_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                    border = androidx.compose.foundation.BorderStroke(2.dp, SwiftGold),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(SwiftGold)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(text = "New Ride Request", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SwiftDark)
                                }
                            }
                            // Countdown timer
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(SwiftRedBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${countdown}s",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SwiftRedText
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Passenger info
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(SwiftGoldLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "MS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SwiftGoldDark)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = req.passengerName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SwiftTextPrimary)
                                Text(text = "★ ${req.passengerRating} • Regular Passenger", fontSize = 11.sp, color = SwiftTextSecondary)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = SwiftBorder)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Pickup / Dropoff
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SwiftGreen))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Pickup: ", fontSize = 12.sp, color = SwiftTextSecondary)
                            Text(text = "${req.pickupAddress} (2.5 km away)", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SwiftTextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SwiftGoldDark))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Drop-off: ", fontSize = 12.sp, color = SwiftTextSecondary)
                            Text(text = req.dropoffAddress, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = SwiftTextPrimary)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(text = "Estimated Fare", fontSize = 11.sp, color = SwiftTextSecondary)
                                Text(text = "₱${req.fare.toInt()}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = SwiftTextPrimary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Est. Time", fontSize = 11.sp, color = SwiftTextSecondary)
                                Text(text = "18 mins", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SwiftGreenText)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Decline / Accept buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.declineIncomingDriverRide() },
                                modifier = Modifier.weight(1f).height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, SwiftBorder)
                            ) {
                                Text(text = "Decline", fontWeight = FontWeight.Bold, color = SwiftTextSecondary)
                            }

                            Button(
                                onClick = { viewModel.acceptIncomingDriverRide() },
                                modifier = Modifier.weight(1f).height(46.dp).testTag("accept_ride_btn"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = SwiftGold,
                                    contentColor = SwiftDark
                                )
                            ) {
                                Text(text = "Accept Ride", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Goal card (Slide 8)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9E6)),
            border = androidx.compose.foundation.BorderStroke(1.dp, SwiftGoldBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "🎯", fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Complete more trips, earn more!",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = SwiftTextPrimary
                    )
                    Text(
                        text = "Aim for 20 trips this week and get ₱100 bonus.",
                        fontSize = 11.sp,
                        color = SwiftTextSecondary
                    )
                }
                Text(text = ">", fontSize = 16.sp, color = SwiftGoldDark)
            }
        }
    }
}

// ==========================================
// 2. DRIVER TRIPS SCREEN (Slide 8)
// ==========================================
@Composable
fun DriverTripsScreen(
    viewModel: SwiftRideViewModel
) {
    val allRides by viewModel.allRides.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Text(
            text = "My Trips",
            fontFamily = com.example.ui.theme.PoppinsFontFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = SwiftDark
        )
        Text(
            text = "View and manage your trips",
            fontSize = 12.sp,
            color = SwiftTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Tabs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Completed", "Cancelled", "Upcoming").forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) SwiftDark else SwiftWhite)
                        .border(1.dp, if (isSelected) SwiftDark else SwiftBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedFilter = filter }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) SwiftWhite else SwiftTextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        val filteredRides = allRides.filter { ride ->
            when (selectedFilter) {
                "All" -> true
                "Completed" -> ride.status == "COMPLETED"
                "Cancelled" -> ride.status == "CANCELLED"
                "Upcoming" -> ride.status == "ACTIVE" || ride.status == "PENDING" || ride.status == "ACCEPTED"
                else -> true
            }
        }

        if (filteredRides.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null,
                        tint = SwiftTextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No Trips Found",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = SwiftTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Your trip requests and completed rides will appear here.",
                        fontSize = 12.sp,
                        color = SwiftTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredRides) { ride ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "${ride.dateLabel} • ${ride.timeLabel}", fontSize = 11.sp, color = SwiftTextMuted)
                                Text(text = "₱${"%.2f".format(ride.fare)}", fontSize = 15.sp, fontWeight = FontWeight.Black, color = SwiftTextPrimary)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "Passenger: ${ride.passengerName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SwiftTextPrimary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "${ride.pickupAddress} ➔ ${ride.dropoffAddress}", fontSize = 11.sp, color = SwiftTextSecondary)

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = { viewModel.showReceipt(ride) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = SwiftGoldLight,
                                        contentColor = SwiftGoldDark
                                    ),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text(text = "Receipt", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. DRIVER CHAT SCREEN (Slide 8)
// ==========================================
@Composable
fun DriverChatScreen(
    viewModel: SwiftRideViewModel
) {
    val activeRide by viewModel.activeRide.collectAsState()
    val messages by viewModel.chatMessages.collectAsState()
    var textMessage by remember { mutableStateOf("") }

    if (activeRide == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SwiftGrayBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(SwiftGoldLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Chat,
                    contentDescription = null,
                    tint = SwiftGoldDark,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Active Ride",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = SwiftTextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Passenger chat is only accessible once you accept an active ride request.",
                fontSize = 13.sp,
                color = SwiftTextSecondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.setDriverTab("Home") },
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = SwiftGold,
                    contentColor = SwiftDark
                )
            ) {
                Text(text = "Go to Home", fontWeight = FontWeight.Bold)
            }
        }
        return
    }

    val ride = activeRide!!
    val passengerInitials = ride.passengerName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifBlank { "P" }

    Column(modifier = Modifier.fillMaxSize().background(SwiftGrayBg)) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = SwiftWhite,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SwiftGoldLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = passengerInitials, fontSize = 16.sp, fontWeight = FontWeight.Black, color = SwiftGoldDark)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = ride.passengerName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = SwiftTextPrimary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "${ride.passengerPhone} • Active Trip", fontSize = 11.sp, color = SwiftTextSecondary)
                        }
                    }
                    IconButton(onClick = {
                        viewModel.triggerPushNotification(
                            title = "Calling Passenger 📞",
                            message = "Dialing ${ride.passengerName} (${ride.passengerPhone})...",
                            type = "RIDE_UPDATE"
                        )
                    }) {
                        Icon(Icons.Default.Call, contentDescription = "Call", tint = SwiftGreenText)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF3F4F6))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Text(text = ride.pickupAddress, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                        Text(text = " ➔ ", fontSize = 11.sp, color = SwiftGoldDark)
                        Text(text = ride.dropoffAddress, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "₱${"%.2f".format(ride.fare)}", fontSize = 12.sp, fontWeight = FontWeight.Black, color = SwiftTextPrimary)
                }
            }
        }

        if (messages.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = SwiftTextMuted,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No Messages Yet",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SwiftTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Send a message or coordinate pickup with ${ride.passengerName}.",
                        fontSize = 12.sp,
                        color = SwiftTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages, key = { it.id }) { msg ->
                    val isMe = msg.senderRole == "DRIVER"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Column(horizontalAlignment = if (isMe) Alignment.End else Alignment.Start) {
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 16.dp,
                                            topEnd = 16.dp,
                                            bottomStart = if (isMe) 16.dp else 4.dp,
                                            bottomEnd = if (isMe) 4.dp else 16.dp
                                        )
                                    )
                                    .background(if (isMe) SwiftGold else SwiftWhite)
                                    .padding(horizontal = 14.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = msg.message,
                                    fontSize = 13.sp,
                                    color = if (isMe) SwiftDark else SwiftTextPrimary
                                )
                            }
                            Text(
                                text = msg.timeString,
                                fontSize = 10.sp,
                                color = SwiftTextMuted,
                                modifier = Modifier.padding(top = 2.dp, start = 4.dp, end = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxWidth().navigationBarsPadding(),
            color = SwiftWhite,
            shadowElevation = 8.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textMessage,
                    onValueChange = { textMessage = it },
                    placeholder = { Text("Reply to ${ride.passengerName}...", fontSize = 13.sp) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        if (textMessage.isNotBlank()) {
                            viewModel.sendChatMessage(ride.id, textMessage)
                            textMessage = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(SwiftGold)
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = SwiftDark)
                }
            }
        }
    }
}

// ==========================================
// 4. DRIVER EARNINGS SCREEN (Slide 9)
// ==========================================
@Composable
fun DriverEarningsScreen(
    viewModel: SwiftRideViewModel
) {
    val earnings by viewModel.driverWalletBalance.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()
    val allRides by viewModel.allRides.collectAsState(initial = emptyList())
    val isOnline by viewModel.isDriverOnline.collectAsState()
    val completedCount = allRides.count { it.status == "COMPLETED" }
    val avgFare = if (completedCount == 0) "₱0.00" else "₱${"%.2f".format(allRides.filter { it.status == "COMPLETED" }.map { it.fare }.average().takeIf { !it.isNaN() } ?: 0.0)}"
    val avgRating = if (completedCount == 0) "—" else "${"%.1f".format(allRides.filter { it.status == "COMPLETED" && it.passengerRated }.map { it.ratingGiven }.average().takeIf { !it.isNaN() } ?: 0.0)} ★"
    val payoutMethods by viewModel.paymentMethods.collectAsState()
    var selectedPeriod by remember { mutableStateOf("Daily") }
    var showWithdrawDialog by remember { mutableStateOf(false) }

    if (showWithdrawDialog) {
        WithdrawEarningsDialog(
            currentBalance = earnings,
            payoutMethods = payoutMethods,
            onDismiss = { showWithdrawDialog = false },
            onWithdrawSuccess = { amount, account ->
                viewModel.withdrawDriverEarnings(amount, account)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Earnings",
                    fontFamily = com.example.ui.theme.PoppinsFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = SwiftDark
                )
                Text(
                    text = "Track your earnings and transactions",
                    fontSize = 12.sp,
                    color = SwiftTextSecondary
                )
            }
            IconButton(onClick = { /* Calendar */ }) {
                Icon(Icons.Default.DateRange, contentDescription = "Calendar", tint = SwiftDark)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Earnings Balance Card (Slide 9)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftDark)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(text = "Today's Earnings", fontSize = 12.sp, color = Color(0xFFD0D5DD))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "₱${"%.2f".format(earnings)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = SwiftGold
                    )
                    Button(
                        onClick = { showWithdrawDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SwiftGold,
                            contentColor = SwiftDark
                        ),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(text = "Withdraw >", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 4 Metric cards: Completed Trips, Online Time, Average Fare, Average Rating
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "$completedCount" to "Completed Trips",
                (if (isOnline) "Active" else "0h 0m") to "Online Time",
                avgFare to "Average Fare",
                avgRating to "Average Rating"
            ).forEach { (value, label) ->
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = SwiftGoldDark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = label, fontSize = 9.sp, color = SwiftTextSecondary, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Earnings Overview (Chart section matching Slide 9)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftWhite),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Earnings Overview",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SwiftTextPrimary
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Period switch: [Daily] [Weekly] [Monthly] [Custom]
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Daily", "Weekly", "Monthly", "Custom").forEach { period ->
                        val isSelected = selectedPeriod == period
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) SwiftDark else Color(0xFFF2F4F7))
                                .clickable { selectedPeriod = period }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = period,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SwiftWhite else SwiftTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Custom Bar Chart Canvas (Slide 9)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val w = size.width
                        val h = size.height
                        val bars = listOf(0.2f, 0.4f, 0.75f, 0.95f, 0.6f, 0.4f, 0.3f)
                        val barWidth = w / (bars.size * 2)

                        bars.forEachIndexed { index, ratio ->
                            val x = (index * 2 + 0.5f) * barWidth
                            val barHeight = (h - 20.dp.toPx()) * ratio
                            val y = h - 20.dp.toPx() - barHeight

                            // Draw bar
                            drawRoundRect(
                                color = if (index == 3) SwiftGoldDark else SwiftGoldLight,
                                topLeft = Offset(x, y),
                                size = Size(barWidth, barHeight),
                                cornerRadius = CornerRadius(8f, 8f)
                            )
                        }

                        // Baseline
                        drawLine(
                            color = Color(0xFFE5E7EB),
                            start = Offset(0f, h - 20.dp.toPx()),
                            end = Offset(w, h - 20.dp.toPx()),
                            strokeWidth = 2f
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                        Text(text = day, fontSize = 10.sp, color = SwiftTextMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Recent Transactions (Slide 9)
        Text(
            text = "Recent Transactions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = SwiftTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (transactions.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                elevation = CardDefaults.cardElevation(1.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalanceWallet,
                        contentDescription = null,
                        tint = SwiftTextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No Transactions Yet",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = SwiftTextPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Your earnings, withdrawals, and wallet transactions will appear here.",
                        fontSize = 11.sp,
                        color = SwiftTextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            transactions.forEach { tx ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = SwiftTextPrimary
                            )
                            Text(
                                text = tx.subtitle,
                                fontSize = 11.sp,
                                color = SwiftTextSecondary
                            )
                            Text(
                                text = tx.dateString,
                                fontSize = 10.sp,
                                color = SwiftTextMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${if (tx.isIncome) "+" else "-"}₱${"%.2f".format(tx.amount)}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = if (tx.isIncome) SwiftGreenText else SwiftRedText
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SwiftGoldLight)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = tx.paymentType,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SwiftGoldDark
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { showWithdrawDialog = true },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SwiftGold,
                contentColor = SwiftDark
            )
        ) {
            Text(text = "Withdraw Earnings", fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// ==========================================
// 5. DRIVER PROFILE SCREEN (Slide 9)
// ==========================================
@Composable
fun DriverProfileScreen(
    viewModel: SwiftRideViewModel
) {
    val earnings by viewModel.driverWalletBalance.collectAsState()
    val userName by viewModel.userName.collectAsState()
    val profilePictureUri by viewModel.profilePictureUri.collectAsState()
    val driverId by viewModel.driverId.collectAsState()
    val vehicleModel by viewModel.driverVehicleModel.collectAsState()
    val plateNumber by viewModel.driverPlateNumber.collectAsState()
    val vehicleColor by viewModel.driverVehicleColor.collectAsState()
    val vehicleType by viewModel.driverVehicleType.collectAsState()
    val isOnline by viewModel.isDriverOnline.collectAsState()
    val allRides by viewModel.allRides.collectAsState(initial = emptyList())
    val completedTripsCount = allRides.count { it.status == "COMPLETED" }
    val ratingStr = if (completedTripsCount == 0) "—" else "%.1f".format(allRides.filter { it.status == "COMPLETED" && it.passengerRated }.map { it.ratingGiven }.average().takeIf { !it.isNaN() } ?: 0.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "Driver's Profile",
            fontFamily = com.example.ui.theme.PoppinsFontFamily,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = SwiftDark
        )
        Text(
            text = "Manage your account and preferences",
            fontSize = 12.sp,
            color = SwiftTextSecondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Driver Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftDark)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(SwiftGold),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePictureUri != null) {
                            coil.compose.AsyncImage(
                                model = profilePictureUri,
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initials = userName.split(" ").mapNotNull { it.firstOrNull() }.joinToString("").take(2).uppercase().ifBlank { "DR" }
                            Text(
                                text = initials,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = SwiftDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = userName.ifBlank { "SwiftRide Driver" },
                            fontFamily = com.example.ui.theme.PoppinsFontFamily,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = SwiftWhite
                        )
                        Text(
                            text = if (isOnline) "Driver ID: $driverId • Online 🟢" else "Driver ID: $driverId • Offline 🔴",
                            fontSize = 11.sp,
                            color = if (isOnline) SwiftGold else SwiftRedText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = Color(0xFF2E323D))
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Wallet Balance", fontSize = 11.sp, color = Color(0xFF98A2B3))
                        Text(text = "₱${"%.2f".format(earnings)}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = SwiftWhite)
                    }
                    Button(
                        onClick = { viewModel.setDriverTab("Earnings") },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SwiftGold,
                            contentColor = SwiftDark
                        ),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(text = "View Wallet >", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Stats: Total Trips, Your Rating, Acceptance Rate, Promo Vouchers
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "$completedTripsCount" to "Total Trips",
                ratingStr to "Your Rating",
                (if (completedTripsCount == 0) "100%" else "98%") to "Acceptance Rate",
                "0" to "Promo Vouchers"
            ).forEach { (value, label) ->
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Black, color = SwiftGoldDark)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = label, fontSize = 9.sp, color = SwiftTextSecondary, maxLines = 1)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // My Vehicle Card (Slide 9)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftWhite),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
                    .clickable { viewModel.showProfileDialog("Manage Vehicle") }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SwiftGoldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SwiftGoldDark)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val displayName = if (vehicleModel.isNotBlank()) "$vehicleModel ${if (vehicleColor.isNotBlank()) "($vehicleColor)" else ""}".trim() else "No vehicle registered"
                    val displaySub = if (plateNumber.isNotBlank()) "$plateNumber • $vehicleType • 4 Seats" else "Tap to add your vehicle details"
                    Text(text = displayName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SwiftTextPrimary)
                    Text(text = displaySub, fontSize = 11.sp, color = SwiftTextSecondary)
                }
                Text(text = "Manage >", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SwiftGoldDark)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu items (Slide 9)
        listOf(
            "Personal Information" to "Manage your personal details",
            "Withdrawal Methods" to "Manage your payout accounts",
            "Documents" to "Driver License, OR/CR",
            "Trip History" to "View your past trips",
            "Your Reviews" to "View and manage your reviews",
            "Refer & Earn" to "Invite friends and earn rewards",
            "Help Center" to "Get help and support",
            "Safety Center" to "Safety tools and emergency contacts",
            "Settings" to "App preferences and notifications"
        ).forEach { (title, subtitle) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable { 
                        when (title) {
                            "Trip History" -> viewModel.setDriverTab("Trips")
                            else -> viewModel.showProfileDialog(title)
                        }
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftWhite),
                elevation = CardDefaults.cardElevation(0.5.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = SwiftTextPrimary)
                        Text(text = subtitle, fontSize = 11.sp, color = SwiftTextSecondary)
                    }
                    Text(text = ">", fontSize = 14.sp, color = SwiftTextMuted)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Log Out Button
        OutlinedButton(
            onClick = { viewModel.logout() },
            modifier = Modifier.fillMaxWidth().height(46.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, SwiftRed)
        ) {
            Text(text = "Log Out", color = SwiftRed, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun DriverDocumentVerificationScreen(
    onBack: () -> Unit,
    onVerify: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SwiftWhite)
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Document Verification", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Verification Requirements",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = SwiftTextPrimary
            )
            Text(
                text = "Please upload clear photos of your required documents.",
                fontSize = 13.sp,
                color = SwiftTextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            DocumentUploadItem(title = "Driver's License", description = "Front and Back side")
            Spacer(modifier = Modifier.height(16.dp))
            DocumentUploadItem(title = "OR/CR", description = "Vehicle registration docs")
            Spacer(modifier = Modifier.height(16.dp))
            DocumentUploadItem(title = "Vehicle Photos", description = "Front, Side, and Interior")

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onVerify,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwiftDark)
            ) {
                Text("Submit for Review", color = SwiftGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DocumentUploadItem(title: String, description: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SwiftGrayBg,
        border = androidx.compose.foundation.BorderStroke(1.dp, SwiftBorder)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.FileUpload, contentDescription = null, tint = SwiftGoldDark)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SwiftTextPrimary)
                Text(text = description, fontSize = 11.sp, color = SwiftTextSecondary)
            }
            Text(text = "Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SwiftGoldDark)
        }
    }
}

@Composable
private fun QuickChatChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(SwiftWhite)
            .border(1.dp, SwiftBorder, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SwiftTextPrimary)
    }
}
