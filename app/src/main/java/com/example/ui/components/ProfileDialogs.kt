package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.DriverDocument
import com.example.data.model.UserRole
import com.example.ui.theme.*
import com.example.ui.viewmodel.SwiftRideViewModel

@Composable
fun ProfileDialogManager(viewModel: SwiftRideViewModel) {
    val activeDialog by viewModel.activeProfileDialog.collectAsState()

    if (activeDialog != null) {
        Dialog(
            onDismissRequest = { viewModel.showProfileDialog(null) },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = SwiftWhite
            ) {
                when (activeDialog) {
                    "Personal Information" -> PersonalInfoPage(viewModel)
                    "Payment Methods", "Withdrawal Methods" -> PaymentMethodsPage(viewModel)
                    "Saved Places" -> SavedPlacesPage(viewModel)
                    "Your Reviews" -> ReviewsPage(viewModel)
                    "Refer & Earn" -> ReferEarnPage(viewModel)
                    "Help Center" -> HelpCenterPage(viewModel)
                    "Safety Center" -> SafetyCenterPage(viewModel)
                    "Settings" -> SettingsPage(viewModel)
                    "Documents" -> DocumentsPage(viewModel)
                    "Manage Vehicle" -> ManageVehiclePage(viewModel)
                    "Terms of Service" -> TermsOfServicePage(viewModel)
                    "Privacy Policy" -> PrivacyPolicyPage(viewModel)
                }
            }
        }
    }
}

@Composable
fun DialogHeader(title: String, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = SwiftDark)
    }
}

@Composable
fun PersonalInfoPage(viewModel: SwiftRideViewModel) {
    val name by viewModel.userName.collectAsState()
    val phone by viewModel.userPhone.collectAsState()
    val email by viewModel.userEmail.collectAsState()
    val profilePictureUri by viewModel.profilePictureUri.collectAsState()

    var editName by remember { mutableStateOf(name) }
    var editPhone by remember { mutableStateOf(phone) }
    var editEmail by remember { mutableStateOf(email) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { viewModel.updateProfilePicture(it.toString()) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Personal Information") { viewModel.showProfileDialog(null) }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture Section
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(SwiftDark)
                    .clickable { photoPickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profilePictureUri != null) {
                    AsyncImage(
                        model = profilePictureUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Change Photo",
                        tint = SwiftGold,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            TextButton(onClick = { photoPickerLauncher.launch("image/*") }) {
                Text("Change Profile Picture", color = SwiftGoldDark, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = editName,
                onValueChange = { editName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = editPhone,
                onValueChange = { editPhone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = editEmail,
                onValueChange = { editEmail = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.updateProfile(editName, editPhone, editEmail)
                    viewModel.showProfileDialog(null)
                    viewModel.triggerPushNotification("Profile Updated", "Your changes have been saved.", "INFO")
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwiftDark)
            ) {
                Text("Save Changes", color = SwiftGold, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun PaymentMethodsPage(viewModel: SwiftRideViewModel) {
    val role by viewModel.userRole.collectAsState()
    val isDriver = role == UserRole.DRIVER
    val pageTitle = if (isDriver) "Withdrawal Methods" else "Payment Methods"
    val addLabel = if (isDriver) "Add Payout Method" else "Add Payment Method"
    val placeholderLabel = if (isDriver) "e.g. GCash or Bank Account" else "e.g. Card Number or GCash"

    val methods by viewModel.paymentMethods.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newMethod by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = pageTitle) { viewModel.showProfileDialog(null) }

        LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
            items(methods) { method ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SwiftGrayBg)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDriver) Icons.Default.AccountBalanceWallet else Icons.Default.Payment,
                                contentDescription = null,
                                tint = SwiftGoldDark
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(text = method, fontWeight = FontWeight.SemiBold)
                        }
                        IconButton(onClick = { viewModel.removePaymentMethod(method) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SwiftRed)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showAddDialog) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(addLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = newMethod,
                        onValueChange = { newMethod = it },
                        placeholder = { Text(placeholderLabel) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        Button(onClick = {
                            if (newMethod.isNotBlank()) {
                                viewModel.addPaymentMethod(newMethod)
                                newMethod = ""
                                showAddDialog = false
                            }
                        }) { Text("Add") }
                    }
                }
            }
        } else {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth().padding(20.dp).height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwiftGold)
            ) {
                Text(addLabel, color = SwiftDark, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun SavedPlacesPage(viewModel: SwiftRideViewModel) {
    val places by viewModel.savedPlaces.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newAddress by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Saved Places") { viewModel.showProfileDialog(null) }

        LazyColumn(modifier = Modifier.padding(horizontal = 20.dp)) {
            items(places) { (title, address) ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SwiftGrayBg)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            if (title == "Home") Icons.Default.Home else if (title == "Work") Icons.Default.Work else Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = SwiftGoldDark
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, fontWeight = FontWeight.Bold)
                            Text(text = address, fontSize = 12.sp, color = SwiftTextSecondary)
                        }
                        IconButton(onClick = { viewModel.removeSavedPlace(title) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SwiftRed)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (showAddDialog) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add Saved Place", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Title (e.g. School)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = newAddress, onValueChange = { newAddress = it }, label = { Text("Address") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        Button(onClick = {
                            if (newTitle.isNotBlank() && newAddress.isNotBlank()) {
                                viewModel.addSavedPlace(newTitle, newAddress)
                                newTitle = ""; newAddress = ""
                                showAddDialog = false
                            }
                        }) { Text("Save") }
                    }
                }
            }
        } else {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth().padding(20.dp).height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwiftGold)
            ) {
                Text("Add New Place", color = SwiftDark, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ReviewsPage(viewModel: SwiftRideViewModel) {
    val role by viewModel.userRole.collectAsState()
    val isDriver = role == UserRole.DRIVER
    val ratingValue = if (isDriver) "4.9" else "4.8"
    val reviewCount = if (isDriver) "542" else "12"

    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Your Reviews") { viewModel.showProfileDialog(null) }

        Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SwiftDark),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(ratingValue, fontSize = 48.sp, fontWeight = FontWeight.Black, color = SwiftGold)
                    Row {
                        repeat(5) { Icon(Icons.Default.Star, contentDescription = null, tint = SwiftGold, modifier = Modifier.size(24.dp)) }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Based on $reviewCount reviews", color = SwiftWhite, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(text = if (isDriver) "Recent Passenger Feedback" else "Recent Driver Feedback", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            if (isDriver) {
                ReviewItem(
                    name = "Maria Santos",
                    date = "Today",
                    rating = 5,
                    comment = "Very smooth ride! The car was super clean and the driver was very professional. Highly recommended!"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ReviewItem(
                    name = "Robert Fox",
                    date = "Yesterday",
                    rating = 5,
                    comment = "Arrived exactly on time. Great music choice and comfortable temperature."
                )
                Spacer(modifier = Modifier.height(12.dp))
                ReviewItem(
                    name = "Jenny Wilson",
                    date = "2 days ago",
                    rating = 4,
                    comment = "Fast and safe driving. A bit quiet but overall a great experience."
                )
            } else {
                ReviewItem(
                    name = "Juan Dela Cruz",
                    date = "Oct 12, 2023",
                    rating = 5,
                    comment = "Excellent passenger, very polite and was ready at the pickup point. 5 stars!"
                )
                Spacer(modifier = Modifier.height(12.dp))
                ReviewItem(
                    name = "Ricardo Dalisay",
                    date = "Sep 28, 2023",
                    rating = 5,
                    comment = "Very respectful and followed all safety protocols. Easy to communicate with."
                )
                Spacer(modifier = Modifier.height(12.dp))
                ReviewItem(
                    name = "Sarah Geronimo",
                    date = "Aug 15, 2023",
                    rating = 5,
                    comment = "Prompt and friendly! Looking forward to having them as a passenger again."
                )
            }
        }
    }
}

@Composable
fun ReviewItem(name: String, date: String, rating: Int, comment: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SwiftGrayBg)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = name, fontWeight = FontWeight.Bold)
                Text(text = date, fontSize = 11.sp, color = SwiftTextSecondary)
            }
            Row {
                repeat(rating) { Icon(Icons.Default.Star, contentDescription = null, tint = SwiftGold, modifier = Modifier.size(14.dp)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comment, fontSize = 13.sp, color = SwiftTextPrimary)
        }
    }
}

@Composable
fun ReferEarnPage(viewModel: SwiftRideViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Refer & Earn") { viewModel.showProfileDialog(null) }

        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(80.dp), tint = SwiftGoldDark)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Invite Friends & Earn Rewards", fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Share your referral code and get ₱100 for every friend who completes their first ride.", fontSize = 14.sp, color = SwiftTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SwiftGrayBg,
                border = BorderStroke(1.dp, SwiftBorder)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("SWIFT-JN123", fontSize = 18.sp, fontWeight = FontWeight.Black, modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.ContentCopy,
                        contentDescription = "Copy",
                        tint = SwiftGoldDark,
                        modifier = Modifier.clickable { 
                            viewModel.triggerPushNotification("Code Copied 📋", "Referral code SWIFT-JN123 copied to clipboard.", "INFO")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwiftDark)
            ) {
                Text("Invite Contacts", color = SwiftGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HelpCenterPage(viewModel: SwiftRideViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Help Center") { viewModel.showProfileDialog(null) }

        Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
            Text("Frequently Asked Questions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            listOf("How to book a ride?", "Payment issues", "Reporting a lost item", "Safety concerns").forEach { faq ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SwiftGrayBg)
                ) {
                    Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(faq, fontWeight = FontWeight.SemiBold)
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("Still need help?", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { 
                    viewModel.showProfileDialog(null)
                    viewModel.triggerPushNotification("Connecting... 🎧", "A support agent will be with you shortly.", "INFO")
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwiftGold)
            ) {
                Icon(Icons.Default.Chat, contentDescription = null, tint = SwiftDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chat with Support", color = SwiftDark, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun SafetyCenterPage(viewModel: SwiftRideViewModel) {
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Emergency SOS", fontWeight = FontWeight.Bold) },
            text = {
                Text("Are you in immediate danger? This will alert our 24/7 safety team and local authorities. \n\nIMPORTANT: Misuse of this feature for non-emergencies will result in immediate account termination.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        viewModel.showProfileDialog(null)
                        viewModel.triggerSos()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SwiftRed)
                ) {
                    Text("YES, SEND HELP", color = SwiftWhite)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("CANCEL", color = SwiftDark)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Safety Center") { viewModel.showProfileDialog(null) }

        Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showConfirmDialog = true },
                colors = CardDefaults.cardColors(containerColor = SwiftRedBg),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = SwiftRed, modifier = Modifier.size(32.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("Emergency SOS", fontWeight = FontWeight.Bold, color = SwiftRed)
                        Text("Contact emergency services immediately", fontSize = 12.sp, color = SwiftRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SwiftRed.copy(alpha = 0.05f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SwiftRed.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = SwiftRed, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("IMPORTANT NOTE", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = SwiftRed)
                        Text(
                            text = "This button is for extreme emergencies only. False alarms or 'prank' triggers are taken very seriously and will be reported to local law enforcement. Misuse results in permanent account suspension.",
                            fontSize = 11.sp,
                            color = SwiftRed,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Safety Features", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            SafetyItem(icon = Icons.Default.Share, title = "Share Trip Status", subtitle = "Let friends and family know where you are")
            Spacer(modifier = Modifier.height(12.dp))
            SafetyItem(icon = Icons.Default.Shield, title = "Ride Insurance", subtitle = "Every trip is covered by personal accident insurance")
            Spacer(modifier = Modifier.height(12.dp))
            SafetyItem(icon = Icons.Default.Call, title = "24/7 Safety Line", subtitle = "Call us anytime for safety-related concerns")
        }
    }
}

@Composable
fun SafetyItem(icon: ImageVector, title: String, subtitle: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(44.dp).clip(CircleShape).background(SwiftGrayBg), contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = null, tint = SwiftGoldDark)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 12.sp, color = SwiftTextSecondary)
        }
    }
}

@Composable
fun SettingsPage(viewModel: SwiftRideViewModel) {
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Settings") { viewModel.showProfileDialog(null) }

        Column(modifier = Modifier.padding(20.dp)) {
            SettingToggle(title = "Push Notifications", subtitle = "Receive alerts for ride updates", checked = true) {
                viewModel.triggerPushNotification("Settings Update", "Push Notifications: ${if(it) "ENABLED" else "DISABLED"}", "INFO")
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SwiftBorder)
            SettingToggle(title = "Dark Mode", subtitle = "Adjust app appearance", checked = isDarkMode) {
                viewModel.setDarkMode(it)
                viewModel.triggerPushNotification("Settings Update", "Dark Mode: ${if(it) "ENABLED" else "DISABLED"}", "INFO")
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = SwiftBorder)
            SettingToggle(title = "Location Services", subtitle = "Improve pickup accuracy", checked = true) {
                viewModel.triggerPushNotification("Settings Update", "Location Services: ${if(it) "ENABLED" else "DISABLED"}", "INFO")
            }

            Spacer(modifier = Modifier.height(32.dp))
            Text("About SwiftRide", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text("App Version 2.4.0", fontSize = 14.sp, color = SwiftTextSecondary)
            Text(
                text = "Terms of Service",
                fontSize = 14.sp,
                color = SwiftGoldDark,
                modifier = Modifier.clickable { viewModel.showProfileDialog("Terms of Service") }
            )
            Text(
                text = "Privacy Policy",
                fontSize = 14.sp,
                color = SwiftGoldDark,
                modifier = Modifier.clickable { viewModel.showProfileDialog("Privacy Policy") }
            )
        }
    }
}

@Composable
fun SettingToggle(title: String, subtitle: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    var isChecked by remember { mutableStateOf(checked) }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 12.sp, color = SwiftTextSecondary)
        }
        Switch(
            checked = isChecked, 
            onCheckedChange = { 
                isChecked = it
                onToggle(it)
            }, 
            colors = SwitchDefaults.colors(checkedThumbColor = SwiftGold, checkedTrackColor = SwiftDark)
        )
    }
}

@Composable
fun DocumentsPage(viewModel: SwiftRideViewModel) {
    var showInsertDialog by remember { mutableStateOf(false) }
    var newDocTitle by remember { mutableStateOf("") }
    val documents by viewModel.driverDocuments.collectAsState()
    
    var selectedDocForForm by remember { mutableStateOf<DriverDocument?>(null) }
    var selectedDocForView by remember { mutableStateOf<DriverDocument?>(null) }

    if (selectedDocForForm != null) {
        DocumentFormPage(
            doc = selectedDocForForm!!,
            viewModel = viewModel,
            onBack = { selectedDocForForm = null }
        )
        return
    }

    if (selectedDocForView != null) {
        DocumentViewPage(
            doc = selectedDocForView!!,
            onBack = { selectedDocForView = null }
        )
        return
    }

    val docPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && newDocTitle.isNotBlank()) {
            viewModel.uploadDocument(newDocTitle, uri.toString(), null, emptyMap())
            newDocTitle = ""
            showInsertDialog = false
        } else if (uri != null) {
            viewModel.triggerPushNotification("Error", "Please enter a document name first.", "ERROR")
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Documents") { viewModel.showProfileDialog(null) }

        Column(modifier = Modifier.padding(20.dp)) {
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(documents) { doc ->
                    val color = when (doc.colorKey) {
                        "GREEN" -> SwiftGreenText
                        "RED" -> SwiftRed
                        "GOLD" -> SwiftGoldDark
                        else -> SwiftTextSecondary
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { 
                            if (doc.status == "Verified" || doc.status == "Pending") {
                                selectedDocForView = doc
                            } else {
                                selectedDocForForm = doc
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SwiftGrayBg)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(doc.title, fontWeight = FontWeight.Bold)
                                Text(doc.status, fontWeight = FontWeight.Bold, color = color, fontSize = 12.sp)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (showInsertDialog) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SwiftGrayBg),
                    border = BorderStroke(1.dp, SwiftBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Insert New Document", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = newDocTitle,
                            onValueChange = { newDocTitle = it },
                            label = { Text("Document Name (e.g. Insurance)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = { 
                                if (newDocTitle.isNotBlank()) {
                                    docPhotoPickerLauncher.launch("image/*")
                                } else {
                                    viewModel.triggerPushNotification("Information 📄", "Please enter a document name first.", "INFO")
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SwiftGold)
                        ) {
                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = SwiftGoldDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Select Document Picture", color = SwiftGoldDark)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(
                                onClick = { showInsertDialog = false },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Cancel")
                            }
                        }
                    }
                }
            } else {
                Button(
                    onClick = { showInsertDialog = true },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SwiftGold)
                ) {
                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = SwiftDark)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Insert New Document", color = SwiftDark, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { 
                    viewModel.triggerPushNotification("Documents Update 📄", "Your request for review has been sent.", "INFO")
                    viewModel.showProfileDialog(null)
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, SwiftBorder)
            ) {
                Text("Submit for Verification", color = SwiftDark, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun TermsOfServicePage(viewModel: SwiftRideViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Terms of Service") { viewModel.showProfileDialog("Settings") }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Last updated: September 04, 2026",
                fontSize = 12.sp,
                color = SwiftTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "1. Acceptance of Terms",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "By accessing and using the SwiftRide application, you agree to be bound by these Terms of Service. If you do not agree, please do not use the service.",
                fontSize = 14.sp,
                color = SwiftTextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "2. User Accounts",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "You are responsible for maintaining the confidentiality of your account and password. SwiftRide reserves the right to terminate accounts that violate our policies.",
                fontSize = 14.sp,
                color = SwiftTextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "3. Ride Conduct",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Both passengers and drivers are expected to behave respectfully. Any form of harassment, discrimination, or illegal activity will result in immediate suspension.",
                fontSize = 14.sp,
                color = SwiftTextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "4. Payments and Fees",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Fares are calculated based on distance, time, and demand. By booking a ride, you agree to pay the estimated fare displayed in the app.",
                fontSize = 14.sp,
                color = SwiftTextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "© 2026 SwiftRide Inc. All rights reserved.",
                fontSize = 12.sp,
                color = SwiftTextMuted,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PrivacyPolicyPage(viewModel: SwiftRideViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Privacy Policy") { viewModel.showProfileDialog("Settings") }

        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Last updated: September 04, 2026",
                fontSize = 12.sp,
                color = SwiftTextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Data Collection",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "We collect information you provide directly to us, such as when you create an account, request a ride, or contact support. This includes your name, email, phone number, and payment information.",
                fontSize = 14.sp,
                color = SwiftTextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Location Information",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "To provide ride-sharing services, we collect precise location data from your device while the app is in use. This is necessary for matching you with drivers and tracking trip progress.",
                fontSize = 14.sp,
                color = SwiftTextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Data Usage",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "We use the data we collect to provide, maintain, and improve our services, to process payments, and to communicate with you about updates and promotions.",
                fontSize = 14.sp,
                color = SwiftTextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Data Security",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "We implement industry-standard security measures to protect your personal information from unauthorized access or disclosure.",
                fontSize = 14.sp,
                color = SwiftTextPrimary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
@Composable
fun ManageVehiclePage(viewModel: SwiftRideViewModel) {
    Column(modifier = Modifier.fillMaxSize()) {
        DialogHeader(title = "Manage Vehicle") { viewModel.showProfileDialog(null) }

        Column(modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState())) {
            Text("Current Vehicle", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SwiftDark)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = SwiftGold, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Toyota Vios (Black)", fontWeight = FontWeight.Bold, color = SwiftWhite)
                            Text("NDA 1234 • Sedan", fontSize = 12.sp, color = SwiftGold)
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Status: Active & Verified", color = SwiftGreenText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Vehicle Details", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            var model by remember { mutableStateOf("Toyota Vios") }
            var plate by remember { mutableStateOf("NDA 1234") }
            var color by remember { mutableStateOf("Black") }
            var vehicleType by remember { mutableStateOf("Sedan") }
            var showTypeDropdown by remember { mutableStateOf(false) }

            OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text("Vehicle Model") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = plate, onValueChange = { plate = it }, label = { Text("Plate Number") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = vehicleType,
                    onValueChange = { },
                    label = { Text("Vehicle Type") },
                    modifier = Modifier.fillMaxWidth(),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showTypeDropdown = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                DropdownMenu(
                    expanded = showTypeDropdown,
                    onDismissRequest = { showTypeDropdown = false },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    listOf("Motor", "Sedan", "SUV", "Van").forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                vehicleType = type
                                showTypeDropdown = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    viewModel.showProfileDialog(null)
                    viewModel.triggerPushNotification("Vehicle Updated", "Your vehicle details have been saved.", "INFO")
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwiftDark)
            ) {
                Text("Save Changes", color = SwiftGold, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DocumentStatusItem(title: String, status: String, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SwiftGrayBg)
    ) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(title, fontWeight = FontWeight.Bold)
            Text(status, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
fun DocumentFormPage(
    doc: DriverDocument,
    viewModel: SwiftRideViewModel,
    onBack: () -> Unit
) {
    var frontUri by remember { mutableStateOf<String?>(doc.frontPhotoUri) }
    var backUri by remember { mutableStateOf<String?>(doc.backPhotoUri) }
    
    val formFields = when (doc.title) {
        "Driver's License" -> listOf("License Number", "Expiry Date", "Driver Name")
        "Vehicle OR/CR" -> listOf("Plate Number", "Engine Number", "Chassis Number")
        "NBI Clearance" -> listOf("Clearance Number", "Date of Issue")
        else -> listOf("Document ID", "Notes")
    }
    
    val details = remember { mutableStateMapOf<String, String>().apply { 
        doc.details.forEach { (k, v) -> put(k, v) }
    } }

    val frontLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { frontUri = it?.toString() }
    val backLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { backUri = it?.toString() }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        DialogHeader(title = "${doc.title} Form") { onBack() }

        Column(modifier = Modifier.padding(20.dp)) {
            Text("Document Photos", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PhotoUploadBox("Front Photo", frontUri, modifier = Modifier.weight(1f)) { frontLauncher.launch("image/*") }
                PhotoUploadBox("Back Photo", backUri, modifier = Modifier.weight(1f)) { backLauncher.launch("image/*") }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Information Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(12.dp))

            formFields.forEach { field ->
                OutlinedTextField(
                    value = details[field] ?: "",
                    onValueChange = { details[field] = it },
                    label = { Text(field) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    viewModel.uploadDocument(doc.title, frontUri, backUri, details.toMap())
                    onBack()
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SwiftDark),
                enabled = frontUri != null && (doc.title == "NBI Clearance" || backUri != null)
            ) {
                Text("Submit for Review", color = SwiftGold, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DocumentViewPage(doc: DriverDocument, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        DialogHeader(title = doc.title) { onBack() }

        Column(modifier = Modifier.padding(20.dp)) {
            val statusColor = when (doc.colorKey) {
                "GREEN" -> SwiftGreenText
                "GOLD" -> SwiftGoldDark
                else -> SwiftRed
            }
            
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = statusColor.copy(alpha = 0.1f),
                border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
            ) {
                Text(
                    text = "Status: ${doc.status}",
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Uploaded Photos", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            if (doc.frontPhotoUri != null) {
                AsyncImage(
                    model = doc.frontPhotoUri,
                    contentDescription = "Front",
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(SwiftGrayBg),
                    contentScale = ContentScale.Crop
                )
                Text("Front View", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }
            
            if (doc.backPhotoUri != null) {
                Spacer(modifier = Modifier.height(16.dp))
                AsyncImage(
                    model = doc.backPhotoUri,
                    contentDescription = "Back",
                    modifier = Modifier.fillMaxWidth().height(200.dp).clip(RoundedCornerShape(12.dp)).background(SwiftGrayBg),
                    contentScale = ContentScale.Crop
                )
                Text("Back View", fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Submitted Details", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            doc.details.forEach { (k, v) ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(k, color = SwiftTextSecondary)
                    Text(v, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PhotoUploadBox(label: String, uri: String?, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SwiftGrayBg)
            .clickable { onClick() }
            .border(1.dp, if (uri != null) SwiftGold else SwiftBorder, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        if (uri != null) {
            AsyncImage(model = uri, contentDescription = label, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = SwiftWhite)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = SwiftGoldDark)
                Text(label, fontSize = 10.sp, color = SwiftTextSecondary)
            }
        }
    }
}
