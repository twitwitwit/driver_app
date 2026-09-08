# SwiftRide - Android Application

SwiftRide is a ride booking application developed natively for Android using Kotlin and Jetpack Compose.

## Features

- **Passenger Experience**:
  - **Explore / Home**: View profile, wallet balance, recommended ride categories (Sedan, SUV, Van, Motorcycle), quick bookings, recent rides, and special promo offers.
  - **Ride Booking**: Pickup & drop-off address selection with map integration, interactive vehicle category cards with live fare calculations, payment method selector (Cash, GCash, Card, Wallet), promo code application, and schedule ride support.
  - **Live Ride Tracking**: Real-time simulated GPS tracking with route polyline, driver details (photo, vehicle plate, model, rating, contact), ETA countdown, status updates (Searching, Driver Arriving, In Progress, Completed), SOS emergency support, in-app receipt modal, and star rating/review dialog with tip options.
  - **Ride History**: Filterable history of past rides (All, Completed, Cancelled) with trip summaries and one-tap re-booking.
  - **In-Ride Messaging**: Direct chat between passenger and driver with quick reply chips and message timestamps.
  - **Profile & Wallet**: Personal details, wallet top-up dialog with preset amounts and payment channels, saved places, safety center, and role switcher.

- **Driver Partner Experience**:
  - **Console / Home**: Online / Offline tactile toggle, real-time incoming ride request overlay with 30s countdown, interactive accept/decline actions, and turn-by-turn navigation mode with trip status transitions.
  - **My Trips**: Comprehensive log of completed and ongoing trips with fare breakdown.
  - **Earnings Dashboard**: Daily, weekly, and monthly gross earnings breakdown, completed rides count, online hours, wallet balance, withdrawal dialog, and recent transaction history.
  - **Driver Profile**: Verification documents overview (License, OR/CR, Vehicle Inspection), rating, acceptance rate, and role switcher.

- **Architecture & Technologies**:
  - **Jetpack Compose & Material 3**: Edge-to-edge layout, dynamic theming, gold/dark branding, responsive layouts.
  - **Room Database**: Offline persistence for trips, chat messages, wallet transactions, and push notifications.
  - **Kotlin Coroutines & StateFlow**: Reactive UI state management in `SwiftRideViewModel`.
