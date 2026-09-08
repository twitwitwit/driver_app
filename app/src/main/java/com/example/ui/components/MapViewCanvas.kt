package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.SwiftDark
import com.example.ui.theme.SwiftGold
import com.example.ui.theme.SwiftGoldDark
import com.example.ui.theme.SwiftGreen
import com.example.ui.theme.SwiftRed
import com.example.ui.theme.SwiftWhite
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun RealTimeGpsMapView(
    progress: Float, // 0.0f to 1.0f along the route
    driverName: String = "Juan Dela Cruz",
    vehiclePlate: String = "NDA 1234",
    modifier: Modifier = Modifier
) {
    LeafletMapView(
        progress = progress,
        driverName = driverName,
        vehiclePlate = vehiclePlate,
        modifier = modifier
    )
}

// Utility to calculate position along segmented polyline
private fun calculatePositionOnPath(points: List<Offset>, t: Float): Offset {
    if (points.isEmpty()) return Offset.Zero
    if (points.size == 1 || t <= 0f) return points.first()
    if (t >= 1f) return points.last()

    val totalSegments = points.size - 1
    val scaledT = t * totalSegments
    val segmentIndex = scaledT.toInt().coerceIn(0, totalSegments - 1)
    val localT = scaledT - segmentIndex

    val start = points[segmentIndex]
    val end = points[segmentIndex + 1]

    return Offset(
        x = start.x + (end.x - start.x) * localT,
        y = start.y + (end.y - start.y) * localT
    )
}

// Utility to calculate heading/bearing angle
private fun calculateBearingOnPath(points: List<Offset>, t: Float): Float {
    if (points.size < 2) return 0f
    val totalSegments = points.size - 1
    val segmentIndex = (t * totalSegments).toInt().coerceIn(0, totalSegments - 1)
    val start = points[segmentIndex]
    val end = points[segmentIndex + 1]

    val dx = end.x - start.x
    val dy = end.y - start.y
    return (Math.toDegrees(atan2(dx.toDouble(), -dy.toDouble()))).toFloat()
}
