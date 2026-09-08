# Implementation Plan - Safety System Upgrade

Upgrade the Emergency SOS feature with legal warnings, a confirmation dialog, and enhanced status information to prevent misuse and improve safety.

## Proposed Changes

### UI Components

#### [ProfileDialogs.kt](file:///C:/Users/xivaM/OneDrive/Documents/swiftride_app-main/app/src/main/java/com/example/ui/components/ProfileDialogs.kt)

- **Confirmation Dialog**: Add `showConfirmDialog` state to `SafetyCenterPage`.
- **Legal Warning Note**: Insert a red-themed warning card below the SOS button in `SafetyCenterPage`.
- **Trigger Logic**: Update the SOS button's `onClick` to show the confirmation dialog instead of immediately triggering the SOS.

```kotlin
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
    // ... SOS Card triggers showConfirmDialog = true
    // ... Add Warning Note Card
}
```

#### [TripOverlays.kt](file:///C:/Users/xivaM/OneDrive/Documents/swiftride_app-main/app/src/main/java/com/example/ui/components/TripOverlays.kt)

- **Enhanced SOS Overlay**: Update the text in `SosActiveOverlay` to inform the user about immediate data sharing with authorities and contacts.

```kotlin
@Composable
fun SosActiveOverlay(onDismiss: () -> Unit) {
    // ...
    Text(
        text = "Safety team notified. Your live location and trip details are being shared with authorities and emergency contacts.",
        fontSize = 16.sp,
        color = SwiftWhite,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(vertical = 16.dp)
    )
    // ...
}
```

## Verification Plan

### Manual Verification
1.  **Safety Center Deterrents**:
    - Navigate to Profile -> Safety Center.
    - Verify the "IMPORTANT NOTE" card is visible and has the legal warning text.
2.  **Confirmation Flow**:
    - Click the "Emergency SOS" button.
    - Verify that an `AlertDialog` appears instead of immediate SOS.
    - Verify the legal warning is present in the dialog text.
    - Click "CANCEL" and verify no SOS is triggered.
3.  **SOS Activation**:
    - Click "Emergency SOS" then "YES, SEND HELP".
    - Verify the SOS Overlay appears.
    - Verify the updated text regarding location sharing with authorities is present.
    - Click "Dismiss Alert" to exit.
