package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.RideEntity
import com.example.ui.theme.SwiftBorder
import com.example.ui.theme.SwiftDark
import com.example.ui.theme.SwiftGold
import com.example.ui.theme.SwiftGoldBorder
import com.example.ui.theme.SwiftGoldDark
import com.example.ui.theme.SwiftGoldLight
import com.example.ui.theme.SwiftGreen
import com.example.ui.theme.SwiftGreenBg
import com.example.ui.theme.SwiftGreenText
import com.example.ui.theme.SwiftTextMuted
import com.example.ui.theme.SwiftTextPrimary
import com.example.ui.theme.SwiftTextSecondary
import com.example.ui.theme.SwiftWhite

@Composable
fun ReceiptDialog(
    ride: RideEntity,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ride Receipt",
                        fontFamily = com.example.ui.theme.PoppinsFontFamily,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SwiftDark
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(SwiftGreenBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SwiftGreen,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "₱${"%.2f".format(ride.fare)}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = SwiftTextPrimary
                )
                Text(
                    text = "Paid via ${ride.paymentMethod}",
                    fontSize = 12.sp,
                    color = SwiftTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SwiftBorder)
                Spacer(modifier = Modifier.height(16.dp))

                // Ride details
                ReceiptRow(label = "Date & Time", value = "${ride.dateLabel}, ${ride.timeLabel}")
                ReceiptRow(label = "Driver", value = "${ride.driverName} (${ride.vehiclePlate})")
                ReceiptRow(label = "Vehicle", value = "${ride.vehicleType} • ${ride.vehicleModel}")

                Spacer(modifier = Modifier.height(12.dp))

                // Route
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF9FAFB))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SwiftGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ride.pickupAddress,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SwiftTextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(SwiftGoldDark)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = ride.dropoffAddress,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = SwiftTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = SwiftBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Breakdown
                val base = if (ride.fare > 0) ride.fare * 0.4 else 0.0
                val distanceFare = if (ride.fare > 0) ride.fare * 0.6 else 0.0
                ReceiptRow(label = "Base Fare", value = "₱${"%.2f".format(base)}")
                ReceiptRow(label = "Distance & Time", value = "₱${"%.2f".format(distanceFare)}")
                if (ride.promoDiscount > 0) {
                    ReceiptRow(
                        label = "Promo Discount (50% OFF)",
                        value = "-₱${"%.2f".format(ride.promoDiscount)}",
                        valueColor = SwiftGreenText
                    )
                }
                if (ride.tipAmount > 0) {
                    ReceiptRow(
                        label = "Driver Tip",
                        value = "₱${"%.2f".format(ride.tipAmount)}"
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                ReceiptRow(
                    label = "Total Charged",
                    value = "₱${"%.2f".format(ride.fare + ride.tipAmount)}",
                    isBold = true
                )

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SwiftGold,
                        contentColor = SwiftDark
                    )
                ) {
                    Text(text = "Done", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReceiptRow(
    label: String,
    value: String,
    valueColor: Color = SwiftTextPrimary,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = SwiftTextSecondary,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RatingDialog(
    ride: RideEntity,
    onDismiss: () -> Unit,
    onSubmit: (rating: Float, review: String, tip: Double) -> Unit
) {
    var rating by remember { mutableFloatStateOf(5.0f) }
    var reviewText by remember { mutableStateOf("") }
    var selectedTip by remember { mutableFloatStateOf(0.0f) }
    val selectedTags = remember { mutableStateListOf<String>() }

    val tags = listOf(
        "Polite & Professional",
        "Clean Car",
        "Safe Driving",
        "Great Route",
        "Punctual & Fast",
        "Pleasant Music"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rate Your Trip",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SwiftTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = ride.driverName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SwiftTextPrimary
                )
                Text(
                    text = "${ride.vehicleType} • ${ride.vehiclePlate}",
                    fontSize = 12.sp,
                    color = SwiftTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Interactive 5 Stars
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..5) {
                        IconButton(
                            onClick = { rating = i.toFloat() },
                            modifier = Modifier.size(42.dp)
                        ) {
                            Icon(
                                imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = "$i Stars",
                                tint = if (i <= rating) SwiftGold else Color(0xFFD0D5DD),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                    }
                }

                Text(
                    text = when (rating.toInt()) {
                        5 -> "Excellent! 🌟"
                        4 -> "Good Job! 👍"
                        3 -> "Average 😐"
                        2 -> "Below Average 👎"
                        else -> "Needs Improvement ⚠️"
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SwiftGoldDark
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Compliment Chips
                Text(
                    text = "What went well?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SwiftTextPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tags.forEach { tag ->
                        val isSelected = selectedTags.contains(tag)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) SwiftGoldLight else Color(0xFFF2F4F7))
                                .border(
                                    1.dp,
                                    if (isSelected) SwiftGold else Color.Transparent,
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable {
                                    if (isSelected) selectedTags.remove(tag) else selectedTags.add(tag)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) SwiftDark else SwiftTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Optional Driver Tip
                Text(
                    text = "Add a tip for the driver",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SwiftTextPrimary,
                    modifier = Modifier.align(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(0.0f to "No Tip", 20.0f to "₱20", 50.0f to "₱50", 100.0f to "₱100").forEach { (amount, label) ->
                        val isSelected = selectedTip == amount
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) SwiftGoldLight else Color(0xFFF9FAFB))
                                .border(
                                    1.dp,
                                    if (isSelected) SwiftGold else SwiftBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { selectedTip = amount }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SwiftDark else SwiftTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Leave a detailed review (optional)...", fontSize = 12.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwiftGold,
                        unfocusedBorderColor = SwiftBorder
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val fullReview = if (selectedTags.isNotEmpty()) {
                            "${selectedTags.joinToString(", ")}. $reviewText".trim()
                        } else {
                            reviewText
                        }
                        onSubmit(rating, fullReview, selectedTip.toDouble())
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SwiftGold,
                        contentColor = SwiftDark
                    )
                ) {
                    Text(text = "Submit Rating", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun TopUpWalletDialog(
    currentBalance: Double,
    onDismiss: () -> Unit,
    onTopUpSuccess: (amount: Double, paymentMethod: String) -> Unit
) {
    var selectedAmount by remember { mutableFloatStateOf(500f) }
    var selectedPayment by remember { mutableStateOf("GCash") }
    var customAmount by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Top Up Wallet",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SwiftTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Current Balance: ₱${"%.2f".format(currentBalance)}",
                    fontSize = 13.sp,
                    color = SwiftTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Select Amount",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SwiftTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100f, 250f, 500f, 1000f).forEach { amount ->
                        val isSelected = selectedAmount == amount && customAmount.isEmpty()
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) SwiftGoldLight else Color(0xFFF9FAFB))
                                .border(
                                    1.dp,
                                    if (isSelected) SwiftGold else SwiftBorder,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    selectedAmount = amount
                                    customAmount = ""
                                }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "₱${amount.toInt()}",
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SwiftDark else SwiftTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = customAmount,
                    onValueChange = {
                        customAmount = it
                        it.toFloatOrNull()?.let { amt -> selectedAmount = amt }
                    },
                    label = { Text("Or enter custom amount (₱)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwiftGold,
                        unfocusedBorderColor = SwiftBorder
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Payment Method",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SwiftTextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))

                listOf("GCash", "Credit Card (VISA **** 1234)", "Maya", "Online Bank Transfer").forEach { method ->
                    val isSelected = selectedPayment == method
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SwiftGoldLight else Color(0xFFF9FAFB))
                            .border(
                                1.dp,
                                if (isSelected) SwiftGold else SwiftBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedPayment = method }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = if (isSelected) SwiftGoldDark else SwiftTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = method,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = SwiftTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onTopUpSuccess(selectedAmount.toDouble(), selectedPayment)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SwiftGold,
                        contentColor = SwiftDark
                    )
                ) {
                    Text(
                        text = "Pay ₱${"%.2f".format(selectedAmount)}",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WithdrawEarningsDialog(
    currentBalance: Double,
    onDismiss: () -> Unit,
    onWithdrawSuccess: (amount: Double, account: String) -> Unit
) {
    var amountText by remember { mutableStateOf(currentBalance.toInt().toString()) }
    var destination by remember { mutableStateOf("GCash (0912 345 6789)") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SwiftWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Withdraw Earnings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = SwiftTextPrimary
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Available Balance: ₱${"%.2f".format(currentBalance)}",
                    fontSize = 13.sp,
                    color = SwiftTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount to withdraw (₱)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SwiftGold,
                        unfocusedBorderColor = SwiftBorder
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Payout Destination",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = SwiftTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))

                listOf("GCash (0912 345 6789)", "BDO Savings (**** 4892)", "Maya Account").forEach { acc ->
                    val isSelected = destination == acc
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) SwiftGoldLight else Color(0xFFF9FAFB))
                            .border(
                                1.dp,
                                if (isSelected) SwiftGold else SwiftBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { destination = acc }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = acc,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = SwiftTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amt = amountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            onWithdrawSuccess(amt, destination)
                        }
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SwiftGold,
                        contentColor = SwiftDark
                    )
                ) {
                    Text(text = "Confirm Withdrawal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
