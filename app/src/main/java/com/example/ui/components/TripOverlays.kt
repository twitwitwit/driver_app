package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SearchingDriverOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SwiftDark.copy(alpha = 0.85f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    color = SwiftGold,
                    strokeWidth = 6.dp
                )
                Icon(
                    Icons.Default.NearMe,
                    contentDescription = null,
                    tint = SwiftWhite,
                    modifier = Modifier.size(40.dp)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Searching for Drivers...",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = SwiftWhite
            )
            Text(
                text = "Connecting you to the nearest SwiftRide",
                fontSize = 14.sp,
                color = SwiftWhite.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun SosActiveOverlay(onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SwiftRed.copy(alpha = 0.9f))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.NotificationImportant,
                contentDescription = null,
                tint = SwiftWhite,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "SOS EMERGENCY ACTIVE",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = SwiftWhite
            )
            Text(
                text = "Safety team notified. Your live location and trip details are being shared with authorities and emergency contacts.",
                fontSize = 16.sp,
                color = SwiftWhite,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = SwiftWhite),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Dismiss Alert", color = SwiftRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}
