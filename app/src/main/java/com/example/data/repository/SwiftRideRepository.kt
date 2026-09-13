package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PushNotificationEntity
import com.example.data.model.RideEntity
import com.example.data.model.WalletTransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SwiftRideRepository(private val database: AppDatabase) {
    val allRides: Flow<List<RideEntity>> = database.rideDao().getAllRides()
    val allTransactions: Flow<List<WalletTransactionEntity>> = database.transactionDao().getAllTransactions()
    val allNotifications: Flow<List<PushNotificationEntity>> = database.notificationDao().getAllNotifications()

    fun getMessagesForRide(rideId: Long): Flow<List<ChatMessageEntity>> = database.chatDao().getMessagesForRide(rideId)
    suspend fun getRideById(id: Long): RideEntity? = database.rideDao().getRideById(id)

    private val backendBaseUrl = BuildConfig.SWIFTRIDE_API_BASE_URL.trimEnd('/')

    private suspend fun syncToBackend(endpoint: String, method: String, body: JSONObject): Boolean = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL("$backendBaseUrl$endpoint").openConnection() as HttpURLConnection).apply {
                requestMethod = method
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                if (BuildConfig.SWIFTRIDE_MOBILE_KEY.isNotBlank()) {
                    setRequestProperty("X-SwiftRide-Client-Key", BuildConfig.SWIFTRIDE_MOBILE_KEY)
                }
                connectTimeout = 5000
                readTimeout = 5000
                doOutput = method != "GET"
            }
            if (method != "GET") connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
            connection.responseCode in 200..299
        }.getOrDefault(false)
    }

    suspend fun createRide(ride: RideEntity): Long {
        val insertedId = database.rideDao().insertRide(ride)
        val body = JSONObject().apply {
            put("id", "SR-$insertedId")
            put("passengerName", ride.passengerName)
            put("vehicleType", ride.vehicleType)
            put("pickup", JSONObject().put("name", ride.pickupAddress))
            put("dropoff", JSONObject().put("name", ride.dropoffAddress))
            put("estimatedFare", ride.fare)
            put("paymentMethod", ride.paymentMethod)
        }
        syncToBackend("/rides/request", "POST", body)
        return insertedId
    }

    suspend fun updateRide(ride: RideEntity) {
        database.rideDao().updateRide(ride)
        val body = JSONObject().apply {
            put("status", ride.status)
            put("driverName", ride.driverName)
            put("driverPhone", ride.driverPhone)
            put("driverPlate", ride.vehiclePlate)
        }
        syncToBackend("/rides/SR-${ride.id}/status", "PATCH", body)
    }

    suspend fun submitRideRating(rideId: Long, rating: Float, review: String, tip: Double = 0.0) {
        database.rideDao().getRideById(rideId)?.let { ride ->
            database.rideDao().updateRide(ride.copy(ratingGiven = rating, reviewFeedback = review, passengerRated = true, tipAmount = tip))
        }
    }

    suspend fun sendMessage(rideId: Long, senderRole: String, senderName: String, message: String, isLocationShare: Boolean = false): Long {
        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        return database.chatDao().insertMessage(ChatMessageEntity(rideId = rideId, senderRole = senderRole, senderName = senderName, message = message, timeString = timeStr, isLocationShare = isLocationShare))
    }

    suspend fun addTransaction(title: String, subtitle: String, amount: Double, isIncome: Boolean, paymentType: String) {
        val dateStr = SimpleDateFormat("h:mm a • MMM d, yyyy", Locale.getDefault()).format(Date())
        database.transactionDao().insertTransaction(WalletTransactionEntity(title = title, subtitle = subtitle, amount = amount, isIncome = isIncome, paymentType = paymentType, dateString = dateStr))
    }

    suspend fun sendPushNotification(title: String, message: String, type: String) {
        val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        database.notificationDao().insertNotification(PushNotificationEntity(title = title, message = message, type = type, timeString = timeStr, isRead = false))
    }

    suspend fun markAllNotificationsAsRead() = database.notificationDao().markAllAsRead()

    suspend fun clearAllUserData() = withContext(Dispatchers.IO) {
        database.rideDao().deleteAllRides()
        database.chatDao().deleteAllMessages()
        database.transactionDao().deleteAllTransactions()
        database.notificationDao().deleteAllNotifications()
    }
}
