package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.VehicleType
import com.example.ui.theme.SwiftDark
import com.example.ui.theme.SwiftGold
import com.example.ui.theme.SwiftGoldDark
import com.example.ui.theme.SwiftGoldLight
import com.example.ui.theme.SwiftTextPrimary

@Composable
fun SwiftRideLogoBadge(
    modifier: Modifier = Modifier,
    showSubtitle: Boolean = true
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(300.dp)
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_swiftride_logo),
                contentDescription = "SwiftRide Logo",
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun VehicleIconBadge(
    vehicleType: VehicleType,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false
) {
    val (icon, bgGradient) = when (vehicleType) {
        VehicleType.SEDAN -> Pair(
            Icons.Default.DirectionsCar,
            listOf(Color(0xFFFFF4D6), Color(0xFFFFE7A8))
        )
        VehicleType.SUV -> Pair(
            Icons.Default.ElectricCar,
            listOf(Color(0xFFE9F5FE), Color(0xFFD0EAFA))
        )
        VehicleType.VAN -> Pair(
            Icons.Default.DirectionsBus,
            listOf(Color(0xFFE8F8EE), Color(0xFFC7F0D6))
        )
        VehicleType.MOTORCYCLE -> Pair(
            Icons.Default.TwoWheeler,
            listOf(Color(0xFFFFF0E6), Color(0xFFFFDEC9))
        )
    }

    Box(
        modifier = modifier
            .size(54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.linearGradient(bgGradient)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = vehicleType.title,
            tint = if (isSelected) SwiftGoldDark else SwiftDark,
            modifier = Modifier.size(30.dp)
        )
    }
}
