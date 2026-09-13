package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessageEntity
import com.example.data.model.PushNotificationEntity
import com.example.data.model.RideEntity
import com.example.data.model.DriverDocument
import com.example.data.model.RideStatus
import com.example.data.model.UserRole
import android.util.Log
import com.example.data.model.VehicleType
import com.example.data.model.WalletTransactionEntity
import com.example.data.repository.SwiftRideRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SwiftRideViewModel(application: Application) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val repository: SwiftRideRepository
    val allRides: StateFlow<List<RideEntity>>
    val allTransactions: StateFlow<List<WalletTransactionEntity>>
    val allNotifications: StateFlow<List<PushNotificationEntity>>

    init {
        val db = AppDatabase.getDatabase(application, viewModelScope)
        repository = SwiftRideRepository(db)
        allRides = repository.allRides.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allTransactions = repository.allTransactions.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
        allNotifications = repository.allNotifications.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )
    }

    // Authentication & Role
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userRole = MutableStateFlow(UserRole.PASSENGER)
    val userRole: StateFlow<UserRole> = _userRole.asStateFlow()

    private val _isDriverVerified = MutableStateFlow(false)
    val isDriverVerified: StateFlow<Boolean> = _isDriverVerified.asStateFlow()

    // Navigation Tabs
    private val _passengerTab = MutableStateFlow("Home") // Home, BookRide, ActiveRide, History, Chat, Profile
    val passengerTab: StateFlow<String> = _passengerTab.asStateFlow()

    private val _driverTab = MutableStateFlow("Home") // Home, Trips, Chat, Earnings, Profile
    val driverTab: StateFlow<String> = _driverTab.asStateFlow()

    // Wallets
    private val _passengerWalletBalance = MutableStateFlow(1250.00)
    val passengerWalletBalance: StateFlow<Double> = _passengerWalletBalance.asStateFlow()

    private val _driverWalletBalance = MutableStateFlow(1250.00)
    val driverWalletBalance: StateFlow<Double> = _driverWalletBalance.asStateFlow()

    // Active Ride & Live GPS Tracking
    private val _activeRide = MutableStateFlow<RideEntity?>(null)
    val activeRide: StateFlow<RideEntity?> = _activeRide.asStateFlow()

    private val _rideStatus = MutableStateFlow(RideStatus.COMPLETED)
    val rideStatus: StateFlow<RideStatus> = _rideStatus.asStateFlow()

    private val _isSearchingForDriver = MutableStateFlow(false)
    val isSearchingForDriver: StateFlow<Boolean> = _isSearchingForDriver.asStateFlow()

    private val _isSosActive = MutableStateFlow(false)
    val isSosActive: StateFlow<Boolean> = _isSosActive.asStateFlow()

    private val _gpsProgress = MutableStateFlow(0.0f)
    val gpsProgress: StateFlow<Float> = _gpsProgress.asStateFlow()

    private val _etaMinutes = MutableStateFlow(5)
    val etaMinutes: StateFlow<Int> = _etaMinutes.asStateFlow()

    // Driver Specific State
    private val _isDriverOnline = MutableStateFlow(true)
    val isDriverOnline: StateFlow<Boolean> = _isDriverOnline.asStateFlow()

    // Dark Mode Support
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

    private val _incomingDriverRequest = MutableStateFlow<RideEntity?>(null)
    val incomingDriverRequest: StateFlow<RideEntity?> = _incomingDriverRequest.asStateFlow()

    private val _incomingCountdown = MutableStateFlow(24)
    val incomingCountdown: StateFlow<Int> = _incomingCountdown.asStateFlow()

    // Push Notification popup banner
    private val _currentPushNotification = MutableStateFlow<PushNotificationEntity?>(null)
    val currentPushNotification: StateFlow<PushNotificationEntity?> = _currentPushNotification.asStateFlow()

    // Active Chat
    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()

    // Receipt & Rating modal dialogs
    private val _dialogReceiptRide = MutableStateFlow<RideEntity?>(null)
    val dialogReceiptRide: StateFlow<RideEntity?> = _dialogReceiptRide.asStateFlow()

    private val _dialogRatingRide = MutableStateFlow<RideEntity?>(null)
    val dialogRatingRide: StateFlow<RideEntity?> = _dialogRatingRide.asStateFlow()

    private val _isScheduledMode = MutableStateFlow(false)
    val isScheduledMode: StateFlow<Boolean> = _isScheduledMode.asStateFlow()

    private val _scheduledTime = MutableStateFlow("10:30 AM")
    val scheduledTime: StateFlow<String> = _scheduledTime.asStateFlow()

    // Profile Management State
    private val _userName = MutableStateFlow("John Michael Nabung")
    val userName: StateFlow<String> = _userName.asStateFlow()

    private val _userPhone = MutableStateFlow("0912 345 6789")
    val userPhone: StateFlow<String> = _userPhone.asStateFlow()

    private val _userEmail = MutableStateFlow("john.michael@email.com")
    val userEmail: StateFlow<String> = _userEmail.asStateFlow()

    private val _profilePictureUri = MutableStateFlow<String?>(null)
    val profilePictureUri: StateFlow<String?> = _profilePictureUri.asStateFlow()

    private val _driverDocuments = MutableStateFlow<List<DriverDocument>>(
        listOf(
            DriverDocument("Driver's License", "Verified", "GREEN"),
            DriverDocument("Vehicle OR/CR", "Verified", "GREEN"),
            DriverDocument("NBI Clearance", "Expired", "RED")
        )
    )
    val driverDocuments: StateFlow<List<DriverDocument>> = _driverDocuments.asStateFlow()

    fun updateProfilePicture(uri: String) {
        _profilePictureUri.value = uri
        triggerPushNotification("Profile Updated 📸", "Your profile picture has been updated.", "INFO")
    }

    fun uploadDocument(
        title: String,
        frontUri: String?,
        backUri: String?,
        details: Map<String, String>
    ) {
        val currentDocs = _driverDocuments.value.toMutableList()
        val index = currentDocs.indexOfFirst { it.title == title }
        val newDoc = DriverDocument(
            title = title,
            status = "Pending",
            colorKey = "GOLD",
            frontPhotoUri = frontUri,
            backPhotoUri = backUri,
            details = details
        )
        if (index != -1) {
            currentDocs[index] = newDoc
        } else {
            currentDocs.add(newDoc)
        }
        _driverDocuments.value = currentDocs
        triggerPushNotification("Document Uploaded 📄", "$title is now pending review.", "INFO")
    }

    private val _savedPlaces = MutableStateFlow(listOf("Home" to "Bagong Silang, Caloocan City", "Work" to "SM North EDSA, Quezon City"))
    val savedPlaces: StateFlow<List<Pair<String, String>>> = _savedPlaces.asStateFlow()

    private val _paymentMethods = MutableStateFlow(listOf("VISA **** 1234", "GCash", "SwiftRide Wallet"))
    val paymentMethods: StateFlow<List<String>> = _paymentMethods.asStateFlow()

    private val _activeProfileDialog = MutableStateFlow<String?>(null)
    val activeProfileDialog: StateFlow<String?> = _activeProfileDialog.asStateFlow()

    fun showProfileDialog(type: String?) {
        _activeProfileDialog.value = type
    }

    fun updateProfile(name: String, phone: String, email: String) {
        _userName.value = name
        _userPhone.value = phone
        _userEmail.value = email
    }

    fun addPaymentMethod(method: String) {
        _paymentMethods.value = _paymentMethods.value + method
    }

    fun removePaymentMethod(method: String) {
        _paymentMethods.value = _paymentMethods.value.filter { it != method }
    }

    fun addSavedPlace(title: String, address: String) {
        _savedPlaces.value = _savedPlaces.value + (title to address)
    }

    fun removeSavedPlace(title: String) {
        _savedPlaces.value = _savedPlaces.value.filter { it.first != title }
    }

    private var simulationJob: Job? = null
    private var driverRequestCountdownJob: Job? = null

    fun toggleScheduledMode() {
        _isScheduledMode.value = !_isScheduledMode.value
    }

    fun setScheduledTime(time: String) {
        _scheduledTime.value = time
    }

    fun cancelRide() {
        _activeRide.value = null
        _rideStatus.value = RideStatus.COMPLETED
        simulationJob?.cancel()
        triggerPushNotification(
            title = "Ride Cancelled ❌",
            message = "Your ride has been cancelled successfully.",
            type = "RIDE_UPDATE"
        )
        _passengerTab.value = "Home"
        _driverTab.value = "Home"
    }

    init {
        // Collect chat messages for ride #1 by default
        viewModelScope.launch {
            repository.getMessagesForRide(1).collect { list ->
                _chatMessages.value = list
            }
        }

        // Initialize driver incoming request simulation matching Slide 8
        initDriverRequestSimulation()
    }

    private fun initDriverRequestSimulation() {
        val sampleRequest = RideEntity(
            id = 999,
            passengerName = "Maria Santos",
            passengerPhone = "0917 555 9876",
            passengerRating = 4.9f,
            driverName = "John Michael Nabung",
            driverPhone = "0912 345 6789",
            vehicleType = "Sedan",
            vehicleModel = "Toyota Vios (Black)",
            vehiclePlate = "NDA 1234",
            pickupAddress = "SM Fairview",
            dropoffAddress = "SM North EDSA",
            fare = 185.00,
            originalFare = 185.00,
            status = "PENDING",
            dateLabel = "Today",
            timeLabel = "10:30 AM"
        )
        _incomingDriverRequest.value = sampleRequest
        startIncomingCountdown()
    }

    private fun startIncomingCountdown() {
        driverRequestCountdownJob?.cancel()
        driverRequestCountdownJob = viewModelScope.launch {
            for (i in 24 downTo 0) {
                _incomingCountdown.value = i
                delay(1000)
            }
        }
    }

    fun setUserRole(role: UserRole) {
        _userRole.value = role
    }

    fun setPassengerTab(tab: String) {
        _passengerTab.value = tab
    }

    fun setDriverTab(tab: String) {
        _driverTab.value = tab
    }

    private fun logDiagnostic(tag: String, message: String, e: Exception? = null) {
        if (e != null) {
            Log.e(tag, message, e)
        } else {
            Log.d(tag, message)
        }
    }

    private var verificationId: String? = null

    // Registration state
    private val _registrationName = kotlinx.coroutines.flow.MutableStateFlow("")
    val registrationName = _registrationName.asStateFlow()

    private val _registrationEmail = kotlinx.coroutines.flow.MutableStateFlow("")
    val registrationEmail = _registrationEmail.asStateFlow()

    private val _registrationPassword = kotlinx.coroutines.flow.MutableStateFlow("")
    val registrationPassword = _registrationPassword.asStateFlow()

    private val _registrationConfirmPassword = kotlinx.coroutines.flow.MutableStateFlow("")
    val registrationConfirmPassword = _registrationConfirmPassword.asStateFlow()

    private val _registrationVehicleDetails = kotlinx.coroutines.flow.MutableStateFlow("")
    val registrationVehicleDetails = _registrationVehicleDetails.asStateFlow()

    fun updateRegistrationName(value: String) { _registrationName.value = value }
    fun updateRegistrationEmail(value: String) { _registrationEmail.value = value }
    fun updateRegistrationPassword(value: String) { _registrationPassword.value = value }
    fun updateRegistrationConfirmPassword(value: String) { _registrationConfirmPassword.value = value }
    fun updateRegistrationVehicleDetails(value: String) { _registrationVehicleDetails.value = value }

    fun isPasswordStrong(password: String): Boolean {
        val hasUppercase = password.any { it.isUpperCase() }
        val hasLowercase = password.any { it.isLowerCase() }
        val hasSpecial = password.any { !it.isLetterOrDigit() }
        val isLongEnough = password.length >= 8
        return hasUppercase && hasLowercase && hasSpecial && isLongEnough
    }

    fun getPasswordStrengthCriteria(password: String): Map<String, Boolean> {
        return mapOf(
            "Uppercase" to password.any { it.isUpperCase() },
            "Lowercase" to password.any { it.isLowerCase() },
            "Special" to password.any { !it.isLetterOrDigit() },
            "Min 8 chars" to (password.length >= 8)
        )
    }

    fun requestOtp(phoneNumber: String, activity: android.app.Activity) {
        val options = com.google.firebase.auth.PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                    logDiagnostic("SwiftRideAuth", "Phone verification completed automatically")
                    verifyOtpWithCredential(credential)
                }

                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    logDiagnostic("SwiftRideAuth", "Phone verification failed", e)
                    triggerPushNotification("Auth Failed ❌", e.localizedMessage ?: "Unknown error", "RIDE_UPDATE")
                }

                override fun onCodeSent(id: String, token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken) {
                    verificationId = id
                    logDiagnostic("SwiftRideAuth", "OTP code sent to phone")
                    triggerPushNotification("OTP Sent 📱", "Please check your phone for the verification code.", "INFO")
                }
            })
            .build()
        com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(code: String) {
        if (verificationId == null) {
            logDiagnostic("SwiftRideAuth", "No verification ID found for OTP")
            return
        }
        val credential = com.google.firebase.auth.PhoneAuthProvider.getCredential(verificationId!!, code)
        verifyOtpWithCredential(credential)
    }

    private fun verifyOtpWithCredential(credential: com.google.firebase.auth.PhoneAuthCredential) {
        viewModelScope.launch {
            try {
                com.google.android.gms.tasks.Tasks.await(auth.signInWithCredential(credential))
                _isLoggedIn.value = true
                logDiagnostic("SwiftRideAuth", "Phone auth successful")
            } catch (e: Exception) {
                logDiagnostic("SwiftRideAuth", "OTP verification failed", e)
                triggerPushNotification("OTP Verification Failed ❌", "Invalid code or expired.", "RIDE_UPDATE")
            }
        }
    }

    fun triggerSos() {
        _isSosActive.value = true
        triggerPushNotification(
            title = "SOS Emergency Triggered! 🚨",
            message = "Emergency services and local authorities have been notified of your location.",
            type = "RIDE_UPDATE"
        )
    }

    fun dismissSos() {
        _isSosActive.value = false
    }

    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
    }

    fun unlockWithBiometric(): Boolean {
        if (auth.currentUser == null) return false
        _isLoggedIn.value = true
        return true
    }

    fun hasAuthenticatedSession(): Boolean = auth.currentUser != null

    fun toggleDriverOnline() {
        _isDriverOnline.value = !_isDriverOnline.value
        if (_isDriverOnline.value && _incomingDriverRequest.value == null) {
            initDriverRequestSimulation()
        }
    }

    fun acceptIncomingDriverRide() {
        driverRequestCountdownJob?.cancel()
        val req = _incomingDriverRequest.value ?: return
        _incomingDriverRequest.value = null
        
        viewModelScope.launch {
            val id = repository.createRide(req.copy(id = 0))
            val active = req.copy(id = id, status = "ACTIVE")
            _activeRide.value = active
            _rideStatus.value = RideStatus.ACCEPTED
            _driverTab.value = "Home"

            triggerPushNotification(
                title = "Ride Accepted 🚗",
                message = "You accepted the trip for ${req.passengerName} (SM Fairview ➔ SM North EDSA)",
                type = "RIDE_UPDATE"
            )

            startDriverGpsSimulation(active)
        }
    }

    fun declineIncomingDriverRide() {
        driverRequestCountdownJob?.cancel()
        _incomingDriverRequest.value = null
    }

    // Passenger booking a ride
    fun bookRide(
        pickup: String,
        dropoff: String,
        vehicleType: VehicleType,
        paymentMethod: String,
        promoCode: String?,
        isScheduled: Boolean = false,
        scheduledTime: String? = null
    ) {
        val hasPromo = promoCode.equals("TNV550", ignoreCase = true)
        val origFare = vehicleType.baseFare
        val discount = if (hasPromo) origFare * 0.5 else 0.0
        val finalFare = origFare - discount

        val newRide = RideEntity(
            passengerName = "John Michael Nabung",
            passengerPhone = "0912 345 6789",
            passengerRating = 4.8f,
            driverName = "Juan Dela Cruz",
            driverPhone = "0917 123 4567",
            driverRating = 4.9f,
            driverTrips = 1248,
            vehicleType = vehicleType.title,
            vehicleModel = "Toyota Vios (NDA 1234)",
            vehiclePlate = "NDA 1234",
            pickupAddress = pickup,
            dropoffAddress = dropoff,
            fare = finalFare,
            originalFare = origFare,
            promoDiscount = discount,
            promoCode = promoCode,
            paymentMethod = paymentMethod,
            status = if (isScheduled) "SCHEDULED" else "PENDING",
            dateLabel = if (isScheduled) "Upcoming" else "Today",
            timeLabel = scheduledTime ?: SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
        )

        viewModelScope.launch {
            if (isScheduled) {
                repository.createRide(newRide)
                triggerPushNotification(
                    title = "Ride Scheduled 🗓️",
                    message = "Your ride for $scheduledTime has been scheduled successfully.",
                    type = "RIDE_UPDATE"
                )
                _passengerTab.value = "History"
                return@launch
            }

            _isSearchingForDriver.value = true
            delay(3500) // Simulating "Proximity Matching"
            _isSearchingForDriver.value = false

            val id = repository.createRide(newRide)
            val created = newRide.copy(id = id, status = "ACTIVE")
            _activeRide.value = created
            _rideStatus.value = RideStatus.ACCEPTED
            _passengerTab.value = "ActiveRide"

            triggerPushNotification(
                title = "Ride Confirmed 🚗",
                message = "${created.driverName} (${created.vehiclePlate}) is on the way! ETA: 5 mins.",
                type = "RIDE_UPDATE"
            )

            startPassengerGpsSimulation(created)
        }
    }

    private fun startPassengerGpsSimulation(ride: RideEntity) {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            _gpsProgress.value = 0.0f
            _etaMinutes.value = 5

            // Step 1: Driver approaching pickup
            for (step in 1..25) {
                delay(400)
                _gpsProgress.value = step / 100f
                if (step == 12) _etaMinutes.value = 3
                if (step == 20) _etaMinutes.value = 1
            }

            // Step 2: Driver Arrived at Pickup!
            _gpsProgress.value = 0.25f
            _rideStatus.value = RideStatus.DRIVER_ARRIVED
            _etaMinutes.value = 0

            triggerPushNotification(
                title = "Arrival Alert 📍",
                message = "Your driver ${ride.driverName} has arrived at ${ride.pickupAddress}!",
                type = "ARRIVAL"
            )

            // Send simulated chat from driver
            repository.sendMessage(
                rideId = ride.id,
                senderRole = "DRIVER",
                senderName = ride.driverName,
                message = "I've arrived at your pickup location outside the gate. Take your time!"
            )

            delay(3000)

            // Step 3: Trip in progress
            _rideStatus.value = RideStatus.IN_PROGRESS
            _etaMinutes.value = 12

            for (step in 26..99) {
                delay(300)
                _gpsProgress.value = step / 100f
                if (step == 50) _etaMinutes.value = 7
                if (step == 75) _etaMinutes.value = 3
                if (step == 90) _etaMinutes.value = 1
            }

            // Step 4: Arrived at Destination / Trip Completed!
            _gpsProgress.value = 1.0f
            _rideStatus.value = RideStatus.COMPLETED
            _etaMinutes.value = 0

            val completedRide = ride.copy(status = "COMPLETED")
            repository.updateRide(completedRide)
            _activeRide.value = completedRide

            // Deduct payment if wallet
            if (ride.paymentMethod.contains("Wallet", ignoreCase = true)) {
                _passengerWalletBalance.value = (_passengerWalletBalance.value - ride.fare).coerceAtLeast(0.0)
            }

            triggerPushNotification(
                title = "Trip Completed ✅",
                message = "You have arrived at ${ride.dropoffAddress}. Total: ₱${"%.2f".format(ride.fare)}",
                type = "PAYMENT"
            )

            // Prompt rating dialog
            delay(1200)
            _dialogRatingRide.value = completedRide
        }
    }

    private fun startDriverGpsSimulation(ride: RideEntity) {
        simulationJob?.cancel()
        simulationJob = viewModelScope.launch {
            _gpsProgress.value = 0.0f
            _etaMinutes.value = 4

            // Driver navigates to SM Fairview pickup
            for (step in 1..25) {
                delay(400)
                _gpsProgress.value = step / 100f
            }

            _rideStatus.value = RideStatus.DRIVER_ARRIVED
            triggerPushNotification(
                title = "Arrived at Pickup 📍",
                message = "You have arrived at ${ride.pickupAddress}. Passenger has been notified.",
                type = "ARRIVAL"
            )

            delay(2500)
            _rideStatus.value = RideStatus.IN_PROGRESS

            for (step in 26..99) {
                delay(300)
                _gpsProgress.value = step / 100f
            }

            _gpsProgress.value = 1.0f
            _rideStatus.value = RideStatus.COMPLETED
            
            val completedRide = ride.copy(status = "COMPLETED")
            repository.updateRide(completedRide)
            _activeRide.value = completedRide

            // Add driver earnings
            _driverWalletBalance.value = _driverWalletBalance.value + ride.fare
            repository.addTransaction(
                title = ride.passengerName,
                subtitle = "${ride.pickupAddress} ➔ ${ride.dropoffAddress}",
                amount = ride.fare,
                isIncome = true,
                paymentType = "GCash"
            )

            triggerPushNotification(
                title = "Trip Completed 💰",
                message = "+₱${"%.2f".format(ride.fare)} added to your driver wallet balance!",
                type = "PAYMENT"
            )
        }
    }

    fun sendChatMessage(rideId: Long, text: String, isLocation: Boolean = false) {
        val role = if (_userRole.value == UserRole.PASSENGER) "PASSENGER" else "DRIVER"
        val name = if (role == "PASSENGER") "John Michael Nabung" else "Juan Dela Cruz"

        viewModelScope.launch {
            repository.sendMessage(
                rideId = rideId,
                senderRole = role,
                senderName = name,
                message = text,
                isLocationShare = isLocation
            )

            // Simulate counter-party auto reply
            if (role == "PASSENGER") {
                delay(1200)
                val reply = when {
                    text.contains("location", ignoreCase = true) || isLocation ->
                        "Received your location pin! Heading straight there."
                    text.contains("coming", ignoreCase = true) ->
                        "Alright! Take your time, I have hazard lights on."
                    else ->
                        "Copy that! See you in a few minutes."
                }
                repository.sendMessage(
                    rideId = rideId,
                    senderRole = "DRIVER",
                    senderName = "Juan Dela Cruz",
                    message = reply
                )
            }
        }
    }

    fun submitRating(rideId: Long, rating: Float, review: String, tip: Double) {
        viewModelScope.launch {
            repository.submitRideRating(rideId, rating, review, tip)
            if (tip > 0 && _passengerWalletBalance.value >= tip) {
                _passengerWalletBalance.value = _passengerWalletBalance.value - tip
            }
            triggerPushNotification(
                title = "Thank You! ⭐",
                message = "Your rating of ${rating.toInt()} stars was submitted successfully.",
                type = "RIDE_UPDATE"
            )
        }
    }

    fun topUpWallet(amount: Double, method: String) {
        _passengerWalletBalance.value = _passengerWalletBalance.value + amount
        viewModelScope.launch {
            repository.addTransaction(
                title = "Wallet Top Up",
                subtitle = "Loaded via $method",
                amount = amount,
                isIncome = true,
                paymentType = method
            )
            triggerPushNotification(
                title = "Top-Up Successful 💳",
                message = "₱${"%.2f".format(amount)} added to your SwiftRide Wallet balance.",
                type = "PAYMENT"
            )
        }
    }

    fun withdrawDriverEarnings(amount: Double, account: String) {
        if (_driverWalletBalance.value >= amount) {
            _driverWalletBalance.value = _driverWalletBalance.value - amount
            viewModelScope.launch {
                repository.addTransaction(
                    title = "Withdrawal to $account",
                    subtitle = "Payout processing",
                    amount = amount,
                    isIncome = false,
                    paymentType = "GCash"
                )
                triggerPushNotification(
                    title = "Withdrawal Requested 💸",
                    message = "₱${"%.2f".format(amount)} has been transferred to $account.",
                    type = "PAYMENT"
                )
            }
        }
    }

    fun triggerPushNotification(title: String, message: String, type: String) {
        viewModelScope.launch {
            val timeStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
            val entity = PushNotificationEntity(
                title = title,
                message = message,
                type = type,
                timeString = timeStr,
                isRead = false
            )
            repository.sendPushNotification(title, message, type)
            _currentPushNotification.value = entity
            delay(5000)
            if (_currentPushNotification.value?.id == entity.id) {
                _currentPushNotification.value = null
            }
        }
    }

    fun dismissPushNotification() {
        _currentPushNotification.value = null
    }

    fun showReceipt(ride: RideEntity) {
        _dialogReceiptRide.value = ride
    }

    fun dismissReceipt() {
        _dialogReceiptRide.value = null
    }

    fun showRating(ride: RideEntity) {
        _dialogRatingRide.value = ride
    }

    fun dismissRating() {
        _dialogRatingRide.value = null
    }
}
