package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.theme.SwiftBorder
import com.example.ui.theme.SwiftDark
import com.example.ui.theme.SwiftGold
import com.example.ui.theme.SwiftGoldDark
import com.example.ui.theme.SwiftGreen
import com.example.ui.theme.SwiftTextMuted
import com.example.ui.theme.SwiftTextPrimary
import com.example.ui.theme.SwiftWhite

/**
 * Interactive Real-Time Map component powered by Leaflet Map API & OpenStreetMap tiles.
 * Renders live route polylines, pickup/dropoff markers, and vehicle movement.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun LeafletMapView(
    progress: Float, // 0.0f to 1.0f along route
    modifier: Modifier = Modifier,
    pickupTitle: String = "Bagong Silang, Caloocan City",
    dropoffTitle: String = "SM North EDSA, Quezon City",
    driverName: String = "Juan Dela Cruz",
    vehiclePlate: String = "NDA 1234",
    showControls: Boolean = true,
    isInteractive: Boolean = true
) {
    val context = LocalContext.current
    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }

    val leafletHtml = remember(pickupTitle, dropoffTitle, driverName, vehiclePlate) {
        buildLeafletHtml(
            pickupTitle = pickupTitle,
            dropoffTitle = dropoffTitle,
            driverName = driverName,
            vehiclePlate = vehiclePlate,
            isInteractive = isInteractive
        )
    }

    // Push progress updates into Leaflet map via evaluateJavascript
    LaunchedEffect(progress, isMapLoaded) {
        if (isMapLoaded) {
            webViewInstance?.evaluateJavascript(
                "if (window.updateDriverProgress) { window.updateDriverProgress($progress); }",
                null
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("leaflet_map_container")
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                WebView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // Force software rendering for the WebView specifically to avoid 
                    // MESA rendernode crashes in the emulated cloud environment.
                    setLayerType(View.LAYER_TYPE_SOFTWARE, null)

                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        setSupportZoom(isInteractive)
                        builtInZoomControls = false
                        displayZoomControls = false
                        cacheMode = WebSettings.LOAD_NO_CACHE // Reduce memory pressure
                    }

                    webViewClient = object : WebViewClient() {
                        override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                            // Return true to prevent the app from crashing.
                            isMapLoaded = false
                            // Destroy the crashed webview to free resources
                            view?.let {
                                (it.parent as? ViewGroup)?.removeView(it)
                                it.destroy()
                            }
                            // The user will see the "Loading..." state again, and we can trigger a reload if needed
                            // For now, staying alive is the priority
                            return true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isMapLoaded = true
                            view?.evaluateJavascript("if (window.updateDriverProgress) { window.updateDriverProgress($progress); }", null)
                        }
                    }

                    loadDataWithBaseURL(
                        "https://leafletjs.com",
                        leafletHtml,
                        "text/html",
                        "UTF-8",
                        null
                    )
                    webViewInstance = this
                }
            },
            update = { webView ->
                // Update progress when recomposed
                if (isMapLoaded) {
                    webView.evaluateJavascript(
                        "if (window.updateDriverProgress) { window.updateDriverProgress($progress); }",
                        null
                    )
                }
            }
        )

        DisposableEffect(Unit) {
            onDispose {
                webViewInstance?.stopLoading()
                webViewInstance = null
            }
        }

        // Loading indicator while Leaflet tiles load
        AnimatedVisibility(
            visible = !isMapLoaded,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SwiftWhite,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp,
                        color = SwiftGoldDark
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Loading Leaflet Map...",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = SwiftTextPrimary
                    )
                }
            }
        }

        // Top Leaflet API Badge
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 80.dp),
            shape = RoundedCornerShape(20.dp),
            color = SwiftDark.copy(alpha = 0.88f),
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(SwiftGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Leaflet Map API • OSM Tiles",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = SwiftWhite
                )
            }
        }

        // On-screen Leaflet Map Controls (Zoom In, Zoom Out, Recenter, Driver Lock)
        if (showControls && isInteractive) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                // Zoom In
                Surface(
                    shape = CircleShape,
                    color = SwiftWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            webViewInstance?.evaluateJavascript("window.zoomInMap();", null)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            tint = SwiftDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Zoom Out
                Surface(
                    shape = CircleShape,
                    color = SwiftWhite,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            webViewInstance?.evaluateJavascript("window.zoomOutMap();", null)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom Out",
                            tint = SwiftDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Center on Moving Driver
                Surface(
                    shape = CircleShape,
                    color = SwiftGold,
                    shadowElevation = 4.dp,
                    modifier = Modifier
                        .size(42.dp)
                        .clickable {
                            webViewInstance?.evaluateJavascript("window.centerOnDriver();", null)
                        }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DirectionsCar,
                            contentDescription = "Center on Driver",
                            tint = SwiftDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Recenter Full Route
                FloatingActionButton(
                    onClick = {
                        webViewInstance?.evaluateJavascript("window.recenterMap();", null)
                    },
                    modifier = Modifier.size(44.dp),
                    containerColor = SwiftWhite,
                    contentColor = SwiftDark,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = "Recenter Route",
                        tint = SwiftGoldDark,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

/**
 * Builds HTML with Leaflet CSS/JS, OpenStreetMap TileLayer, Route polyline, and markers.
 */
private fun buildLeafletHtml(
    pickupTitle: String,
    dropoffTitle: String,
    driverName: String,
    vehiclePlate: String,
    isInteractive: Boolean
): String {
    val escapedPickup = pickupTitle.replace("'", "\\'")
    val escapedDropoff = dropoffTitle.replace("'", "\\'")
    val escapedDriver = driverName.replace("'", "\\'")
    val escapedPlate = vehiclePlate.replace("'", "\\'")

    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" crossorigin="" />
    <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" crossorigin=""></script>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }
        html, body {
            width: 100%;
            height: 100%;
            overflow: hidden;
            background-color: #EBF0F5;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif;
        }
        #map {
            width: 100%;
            height: 100%;
            background: #EBF0F5;
        }
        .leaflet-container {
            font-family: inherit;
        }
        .leaflet-control-attribution {
            font-size: 8px !important;
            background: rgba(255, 255, 255, 0.8) !important;
            padding: 2px 5px !important;
            border-radius: 4px;
        }
        /* Custom Map Pins */
        .pin-wrapper {
            display: flex;
            align-items: center;
            justify-content: center;
            position: relative;
        }
        .pickup-pin {
            width: 32px;
            height: 32px;
            background: #12B76A;
            border: 3px solid #FFFFFF;
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(18, 183, 106, 0.4);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #FFFFFF;
            font-size: 14px;
        }
        .dropoff-pin {
            width: 32px;
            height: 32px;
            background: #F04438;
            border: 3px solid #FFFFFF;
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(240, 68, 56, 0.4);
            display: flex;
            align-items: center;
            justify-content: center;
            color: #FFFFFF;
            font-size: 14px;
        }
        .car-pin {
            width: 42px;
            height: 42px;
            background: #1D2939;
            border: 3px solid #F5A623;
            border-radius: 50%;
            box-shadow: 0 4px 14px rgba(245, 166, 35, 0.6);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 19px;
            transition: all 0.35s ease-out;
        }
        .pulse {
            position: absolute;
            width: 48px;
            height: 48px;
            border-radius: 50%;
            background: rgba(18, 183, 106, 0.25);
            animation: pulse-ring 1.8s infinite ease-out;
            pointer-events: none;
        }
        @keyframes pulse-ring {
            0% { transform: scale(0.6); opacity: 0.9; }
            100% { transform: scale(1.3); opacity: 0; }
        }
        .leaflet-popup-content-wrapper {
            border-radius: 12px;
            box-shadow: 0 6px 16px rgba(0,0,0,0.15);
            padding: 4px;
        }
        .leaflet-popup-content {
            margin: 8px 12px;
            font-size: 11px;
            line-height: 1.35;
            color: #1D2939;
        }
    </style>
</head>
<body>
    <div id="map"></div>
    <script>
        var map;
        var pickupMarker, dropoffMarker, driverMarker, routePolyline;

        // Coordinates: Bagong Silang, Caloocan to SM North EDSA, QC
        var pickupCoord = [14.7600, 121.0400];
        var dropoffCoord = [14.6537, 121.0318];

        var routePoints = [
            [14.7600, 121.0400],
            [14.7460, 121.0440],
            [14.7310, 121.0495],
            [14.7150, 121.0535],
            [14.6980, 121.0470],
            [14.6820, 121.0390],
            [14.6670, 121.0340],
            [14.6537, 121.0318]
        ];

        function init() {
            if (typeof L === 'undefined') {
                setTimeout(init, 200);
                return;
            }

            map = L.map('map', {
                zoomControl: false,
                attributionControl: true,
                dragging: $isInteractive,
                touchZoom: $isInteractive,
                scrollWheelZoom: $isInteractive,
                doubleClickZoom: $isInteractive
            }).setView([14.7068, 121.0360], 13);

            // OpenStreetMap tile layer
            L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
                maxZoom: 19,
                attribution: '&copy; OpenStreetMap | Leaflet'
            }).addTo(map);

            // Custom Leaflet Div Icons
            var pickupIcon = L.divIcon({
                className: '',
                html: '<div class="pin-wrapper"><div class="pulse"></div><div class="pickup-pin">📍</div></div>',
                iconSize: [32, 32],
                iconAnchor: [16, 16],
                popupAnchor: [0, -16]
            });

            var dropoffIcon = L.divIcon({
                className: '',
                html: '<div class="pin-wrapper"><div class="dropoff-pin">🏁</div></div>',
                iconSize: [32, 32],
                iconAnchor: [16, 16],
                popupAnchor: [0, -16]
            });

            var carIcon = L.divIcon({
                className: '',
                html: '<div class="pin-wrapper"><div class="car-pin" id="carPin">🚗</div></div>',
                iconSize: [42, 42],
                iconAnchor: [21, 21],
                popupAnchor: [0, -20]
            });

            // Pickup Marker
            pickupMarker = L.marker(pickupCoord, { icon: pickupIcon }).addTo(map)
                .bindPopup("<b>Pickup Location</b><br>$escapedPickup");

            // Dropoff Marker
            dropoffMarker = L.marker(dropoffCoord, { icon: dropoffIcon }).addTo(map)
                .bindPopup("<b>Drop-off Location</b><br>$escapedDropoff");

            // Background line (Golden glow casing)
            L.polyline(routePoints, {
                color: '#FEDF89',
                weight: 8,
                opacity: 0.7,
                lineJoin: 'round'
            }).addTo(map);

            // Route Polyline
            routePolyline = L.polyline(routePoints, {
                color: '#F5A623',
                weight: 5,
                opacity: 0.95,
                lineJoin: 'round'
            }).addTo(map);

            // Driver Car Marker
            driverMarker = L.marker(pickupCoord, { icon: carIcon }).addTo(map)
                .bindPopup("<b>SwiftRide Driver</b><br>$escapedDriver ($escapedPlate)");

            // Fit initial view to route bounds
            map.fitBounds(routePolyline.getBounds(), { padding: [40, 40] });
        }

        window.updateDriverProgress = function(progress) {
            if (!driverMarker || !routePoints) return;
            if (progress < 0) progress = 0;
            if (progress > 1) progress = 1;

            var totalSegments = routePoints.length - 1;
            var scaled = progress * totalSegments;
            var segIndex = Math.min(Math.floor(scaled), totalSegments - 1);
            var t = scaled - segIndex;

            var p1 = routePoints[segIndex];
            var p2 = routePoints[segIndex + 1];

            var lat = p1[0] + (p2[0] - p1[0]) * t;
            var lng = p1[1] + (p2[1] - p1[1]) * t;

            driverMarker.setLatLng([lat, lng]);
        };

        window.recenterMap = function() {
            if (map && routePolyline) {
                map.fitBounds(routePolyline.getBounds(), { padding: [40, 40] });
            }
        };

        window.zoomInMap = function() {
            if (map) map.zoomIn();
        };

        window.zoomOutMap = function() {
            if (map) map.zoomOut();
        };

        window.centerOnDriver = function() {
            if (map && driverMarker) {
                map.setView(driverMarker.getLatLng(), 15, { animate: true });
            }
        };

        window.onload = function() {
            init();
        };
    </script>
</body>
</html>
""".trimIndent()
}
