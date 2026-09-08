package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PushNotificationEntity
import com.example.data.model.RideEntity
import com.example.data.model.WalletTransactionEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RideEntity::class,
        ChatMessageEntity::class,
        WalletTransactionEntity::class,
        PushNotificationEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rideDao(): RideDao
    abstract fun chatDao(): ChatDao
    abstract fun transactionDao(): TransactionDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "swiftride_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database)
                    }
                }
            }
        }

        suspend fun populateDatabase(database: AppDatabase) {
            val rideDao = database.rideDao()
            val chatDao = database.chatDao()
            val transactionDao = database.transactionDao()
            val notificationDao = database.notificationDao()

            val initialRides = listOf(
                RideEntity(
                    id = 1,
                    passengerName = "John Michael Nabung",
                    passengerPhone = "0912 345 6789",
                    passengerRating = 4.8f,
                    driverName = "Juan Dela Cruz",
                    driverPhone = "0917 123 4567",
                    driverRating = 4.9f,
                    driverTrips = 1248,
                    vehicleType = "Sedan",
                    vehicleModel = "Toyota Vios (Black)",
                    vehiclePlate = "NDA 1234",
                    pickupAddress = "Bagong Silang, Caloocan City",
                    dropoffAddress = "SM North EDSA, Quezon City",
                    fare = 180.0,
                    originalFare = 180.0,
                    paymentMethod = "VISA **** 1234",
                    status = "COMPLETED",
                    dateLabel = "Today",
                    timeLabel = "08:35 AM",
                    timestamp = System.currentTimeMillis() - 3600000 * 2,
                    ratingGiven = 5.0f,
                    reviewFeedback = "Very smooth ride and very polite driver!",
                    passengerRated = true
                ),
                RideEntity(
                    id = 2,
                    passengerName = "John Michael Nabung",
                    passengerPhone = "0912 345 6789",
                    passengerRating = 4.8f,
                    driverName = "Mark Reyes",
                    driverPhone = "0918 234 5678",
                    driverRating = 4.7f,
                    driverTrips = 830,
                    vehicleType = "SUV",
                    vehicleModel = "Mitsubishi Xpander",
                    vehiclePlate = "NCA 5678",
                    pickupAddress = "SM Fairview, Quezon City",
                    dropoffAddress = "UP Diliman, Quezon City",
                    fare = 150.0,
                    originalFare = 150.0,
                    paymentMethod = "GCash",
                    status = "COMPLETED",
                    dateLabel = "Yesterday",
                    timeLabel = "06:20 PM",
                    timestamp = System.currentTimeMillis() - 86400000,
                    ratingGiven = 4.5f,
                    reviewFeedback = "Arrived quickly despite the traffic.",
                    passengerRated = true
                ),
                RideEntity(
                    id = 3,
                    passengerName = "John Michael Nabung",
                    passengerPhone = "0912 345 6789",
                    passengerRating = 4.8f,
                    driverName = "Pending Driver",
                    driverPhone = "N/A",
                    driverRating = 5.0f,
                    driverTrips = 0,
                    vehicleType = "Van",
                    vehicleModel = "Toyota HiAce",
                    vehiclePlate = "NAA 9921",
                    pickupAddress = "Caloocan City Hall, Caloocan City",
                    dropoffAddress = "Robinsons Magnolia, QC",
                    fare = 0.0,
                    originalFare = 240.0,
                    paymentMethod = "Cash",
                    status = "CANCELLED",
                    dateLabel = "July 10, 2025",
                    timeLabel = "09:15 AM",
                    timestamp = System.currentTimeMillis() - 86400000 * 3,
                    reviewFeedback = "Ride was cancelled by you",
                    passengerRated = false
                ),
                RideEntity(
                    id = 4,
                    passengerName = "John Michael Nabung",
                    passengerPhone = "0912 345 6789",
                    passengerRating = 4.8f,
                    driverName = "Carlo Santos",
                    driverPhone = "0920 345 6789",
                    driverRating = 5.0f,
                    driverTrips = 2100,
                    vehicleType = "Sedan",
                    vehicleModel = "Toyota Vios",
                    vehiclePlate = "NBD 2468",
                    pickupAddress = "Novaliches, Quezon City",
                    dropoffAddress = "Cubao, Quezon City",
                    fare = 210.0,
                    originalFare = 210.0,
                    paymentMethod = "SwiftRide Wallet",
                    status = "COMPLETED",
                    dateLabel = "July 8, 2025",
                    timeLabel = "07:45 PM",
                    timestamp = System.currentTimeMillis() - 86400000 * 5,
                    ratingGiven = 5.0f,
                    reviewFeedback = "Clean car, courteous driver.",
                    passengerRated = true
                )
            )
            rideDao.insertRides(initialRides)

            val initialChat = listOf(
                ChatMessageEntity(
                    rideId = 1,
                    senderRole = "DRIVER",
                    senderName = "Juan Dela Cruz",
                    message = "Hi John! I'm on my way to your pickup location.",
                    timeString = "9:35 AM",
                    timestamp = System.currentTimeMillis() - 180000
                ),
                ChatMessageEntity(
                    rideId = 1,
                    senderRole = "PASSENGER",
                    senderName = "John",
                    message = "Hi Kuya! Thank you. I'm waiting outside the gate.",
                    timeString = "9:36 AM",
                    timestamp = System.currentTimeMillis() - 150000
                ),
                ChatMessageEntity(
                    rideId = 1,
                    senderRole = "DRIVER",
                    senderName = "Juan Dela Cruz",
                    message = "Got it! See you in a bit.",
                    timeString = "9:36 AM",
                    timestamp = System.currentTimeMillis() - 120000
                ),
                ChatMessageEntity(
                    rideId = 1,
                    senderRole = "DRIVER",
                    senderName = "Juan Dela Cruz",
                    message = "I've arrived at your location. 📍 Bagong Silang, Caloocan City",
                    timeString = "9:37 AM",
                    timestamp = System.currentTimeMillis() - 90000,
                    isLocationShare = true
                ),
                ChatMessageEntity(
                    rideId = 1,
                    senderRole = "PASSENGER",
                    senderName = "John",
                    message = "Okay, coming na po.",
                    timeString = "9:37 AM",
                    timestamp = System.currentTimeMillis() - 60000
                ),
                ChatMessageEntity(
                    rideId = 1,
                    senderRole = "DRIVER",
                    senderName = "Juan Dela Cruz",
                    message = "No rush, take your time. 😊",
                    timeString = "9:38 AM",
                    timestamp = System.currentTimeMillis() - 30000
                )
            )
            chatDao.insertMessages(initialChat)

            val initialTransactions = listOf(
                WalletTransactionEntity(
                    title = "Maria Santos",
                    subtitle = "SM Fairview ➔ SM North EDSA",
                    amount = 180.00,
                    isIncome = true,
                    paymentType = "Cash",
                    dateString = "08:35 AM • May 12, 2025",
                    timestamp = System.currentTimeMillis() - 3600000
                ),
                WalletTransactionEntity(
                    title = "Juan Dela Cruz",
                    subtitle = "SM Fairview ➔ UP Diliman",
                    amount = 150.00,
                    isIncome = true,
                    paymentType = "GCash",
                    dateString = "10:15 AM • May 12, 2025",
                    timestamp = System.currentTimeMillis() - 7200000
                ),
                WalletTransactionEntity(
                    title = "Carla Reyes",
                    subtitle = "Caloocan City Hall ➔ Robinsons Magnolia",
                    amount = 210.00,
                    isIncome = true,
                    paymentType = "GCash",
                    dateString = "11:40 AM • May 12, 2025",
                    timestamp = System.currentTimeMillis() - 10800000
                ),
                WalletTransactionEntity(
                    title = "Mark Reyes",
                    subtitle = "Novaliches ➔ Cubao",
                    amount = 175.00,
                    isIncome = true,
                    paymentType = "GCash",
                    dateString = "07:25 PM • May 11, 2025",
                    timestamp = System.currentTimeMillis() - 86400000
                ),
                WalletTransactionEntity(
                    title = "Incentive Bonus",
                    subtitle = "Daily Trips Incentive (10+ trips)",
                    amount = 100.00,
                    isIncome = true,
                    paymentType = "Bonus",
                    dateString = "May 12, 2025",
                    timestamp = System.currentTimeMillis() - 1800000
                )
            )
            transactionDao.insertTransactions(initialTransactions)

            val initialNotifications = listOf(
                PushNotificationEntity(
                    title = "Arrival Alert 📍",
                    message = "Your driver Juan Dela Cruz has arrived at Bagong Silang, Caloocan City!",
                    type = "ARRIVAL",
                    timeString = "9:37 AM",
                    timestamp = System.currentTimeMillis() - 90000,
                    isRead = false
                ),
                PushNotificationEntity(
                    title = "Ride Confirmed 🚗",
                    message = "Toyota Vios (NDA 1234) has accepted your ride request. ETA: 5 mins.",
                    type = "RIDE_UPDATE",
                    timeString = "9:34 AM",
                    timestamp = System.currentTimeMillis() - 270000,
                    isRead = true
                ),
                PushNotificationEntity(
                    title = "Special Offer 50% OFF 🏷️",
                    message = "Use promo code TNV550 today to enjoy half off your next ride!",
                    type = "PROMO",
                    timeString = "8:00 AM",
                    timestamp = System.currentTimeMillis() - 3600000 * 4,
                    isRead = true
                ),
                PushNotificationEntity(
                    title = "Wallet Top-Up Completed 💳",
                    message = "₱1,250.00 has been credited to your SwiftRide Wallet balance.",
                    type = "PAYMENT",
                    timeString = "Yesterday",
                    timestamp = System.currentTimeMillis() - 86400000,
                    isRead = true
                )
            )
            notificationDao.insertNotifications(initialNotifications)
        }
    }
}
