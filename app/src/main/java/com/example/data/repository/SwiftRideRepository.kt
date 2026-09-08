package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PushNotificationEntity
import com.example.data.model.RideEntity
import com.example.data.model.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SwiftRideRepository(private val database: AppDatabase) {
    val allRides: Flow<List<RideEntity>> = database.rideDao().getAllRides()
    val allTransactions: Flow<List<WalletTransactionEntity>> = database.transactionDao().getAllTransactions()
    val allNotifications: Flow<List<PushNotificationEntity>> = database.notificationDao().getAllNotifications()

    fun getMessagesForRide(rideId: Long): Flow<List<ChatMessageEntity>> {
        return database.chatDao().getMessagesForRide(rideId)
    }

    suspend fun getRideById(id: Long): RideEntity? {
        return database.rideDao().getRideById(id)
    }

    private val backendBaseUrl = "http://10.0.2.2:5000/api"

    private suspend fun syncToBackend(endpoint: String, jsonBody: String) {
        kotlinx.coroutines.Dispatchers.IO.let { dispatcher ->
            kotlinx.coroutines.withContext(dispatcher) {
                try {
                    val url = java.net.URL("$backendBaseUrl$endpoint")
                    val conn = url.openConnection() as java.net.HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Content-Type", "application/json; utf-8")
                    conn.doOutput = true
                    conn.connectTimeout = 3000
                    conn.readTimeout = 3000
                    conn.outputStream.use { os ->
                        val input = jsonBody.toByteArray(charset("utf-8"))
                        os.write(input, 0, input.size)
                    }
                    conn.responseCode
                } catch (e: Exception) {
                    // Fallback to local Room database if network is unreachable
                }
            }
        }
    }

    suspend fun createRide(ride: RideEntity): Long {
        val insertedId = database.rideDao().insertRide(ride)
        val json = """
            {
              "id": "SR-$insertedId",
              "passengerName": "${ride.passengerName}",
              "vehicleType": "${ride.vehicleType}",
              "pickup": { "name": "${ride.pickupAddress}" },
              "dropoff": { "name": "${ride.dropoffAddress}" },
              "estimatedFare": ${ride.fare},
              "paymentMethod": "${ride.paymentMethod}"
            }
        """.trimIndent()
        syncToBackend("/rides/request", json)
        return insertedId
    }

    suspend fun updateRide(ride: RideEntity) {
        database.rideDao().updateRide(ride)
        val json = """
            {
              "status": "${ride.status}",
              "driverName": "${ride.driverName}",
              "driverPhone": "${ride.driverPhone}",
              "driverPlate": "${ride.vehiclePlate}"
            }
        """.trimIndent()
        syncToBackend("/rides/SR-${ride.id}/status", json)
    }

    suspend fun submitRideRating(rideId: Long, rating: Float, review: String, tip: Double = 0.0) {
        val ride = database.rideDao().getRideById(rideId)
        if (ride != null) {
            val updated = ride.copy(
                ratingGiven = rating,
                reviewFeedback = review,
                passengerRated = true,
                tipAmount = tip
            )
            database.rideDao().updateRide(updated)
        }
    }

    suspend fun sendMessage(
        rideId: Long,
        senderRole: String,
        senderName: String,
        message: String,
        isLocationShare: Boolean = false
    ): Long {
        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val msg = ChatMessageEntity(
            rideId = rideId,
            senderRole = senderRole,
            senderName = senderName,
            message = message,
            timeString = timeStr,
            isLocationShare = isLocationShare
        )
        return database.chatDao().insertMessage(msg)
    }

    suspend fun addTransaction(
        title: String,
        subtitle: String,
        amount: Double,
        isIncome: Boolean,
        paymentType: String
    ) {
        val dateStr = SimpleDateFormat("h:mm a • MMM d, yyyy", Locale.getDefault()).format(Date())
        database.transactionDao().insertTransaction(
            WalletTransactionEntity(
                title = title,
                subtitle = subtitle,
                amount = amount,
                isIncome = isIncome,
                paymentType = paymentType,
                dateString = dateStr
            )
        )
    }

    suspend fun sendPushNotification(title: String, message: String, type: String) {
        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        database.notificationDao().insertNotification(
            PushNotificationEntity(
                title = title,
                message = message,
                type = type,
                timeString = timeStr,
                isRead = false
            )
        )
    }

    suspend fun markAllNotificationsAsRead() {
        database.notificationDao().markAllAsRead()
    }
}
