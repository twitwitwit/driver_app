package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class UserRole {
    PASSENGER,
    DRIVER
}

enum class VehicleType(
    val title: String,
    val capacity: String,
    val maxSeats: Int,
    val description: String,
    val baseFare: Double,
    val etaMinutes: String
) {
    SEDAN("Sedan", "1-4 seats", 4, "Comfortable rides for everyday travel", 120.0, "3-5 min"),
    SUV("SUV", "1-6 seats", 6, "Spacious and perfect for group travel", 180.0, "4-6 min"),
    VAN("Van", "1-10 seats", 10, "Best for big groups and bulky items", 250.0, "5-8 min"),
    MOTORCYCLE("Motorcycle", "1 seat", 1, "Beat the traffic and get there faster", 80.0, "2-4 min")
}

enum class RideStatus {
    PENDING,
    ACCEPTED,
    DRIVER_ARRIVED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}

@Entity(tableName = "rides")
data class RideEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val passengerName: String,
    val passengerPhone: String = "0912 345 6789",
    val passengerRating: Float = 4.9f,
    val driverName: String,
    val driverPhone: String = "0917 888 1234",
    val driverRating: Float = 4.9f,
    val driverTrips: Int = 1248,
    val vehicleType: String,
    val vehicleModel: String,
    val vehiclePlate: String,
    val pickupAddress: String,
    val dropoffAddress: String,
    val fare: Double,
    val originalFare: Double,
    val promoDiscount: Double = 0.0,
    val promoCode: String? = null,
    val paymentMethod: String = "VISA **** 1234",
    val status: String = "COMPLETED",
    val dateLabel: String, // "Today", "Yesterday", "July 10, 2025"
    val timeLabel: String, // "08:35 AM"
    val timestamp: Long = System.currentTimeMillis(),
    val ratingGiven: Float = 0f,
    val reviewFeedback: String = "",
    val driverRated: Boolean = false,
    val passengerRated: Boolean = false,
    val tipAmount: Double = 0.0
)

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rideId: Long = 1,
    val senderRole: String, // "PASSENGER" or "DRIVER"
    val senderName: String,
    val message: String,
    val timeString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isLocationShare: Boolean = false
)

@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val amount: Double,
    val isIncome: Boolean,
    val paymentType: String, // "Cash", "GCash", "Wallet", "Bonus"
    val dateString: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "push_notifications")
data class PushNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // "ARRIVAL", "RIDE_UPDATE", "PAYMENT", "PROMO", "DRIVER_REQUEST"
    val timeString: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class DriverDocument(
    val title: String,
    val status: String, // "Verified", "Expired", "Pending", "Missing"
    val colorKey: String, // "GREEN", "RED", "GOLD", "GRAY"
    val frontPhotoUri: String? = null,
    val backPhotoUri: String? = null,
    val details: Map<String, String> = emptyMap()
)
