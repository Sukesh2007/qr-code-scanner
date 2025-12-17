package com.example.qrcodescanner

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.ui.graphics.vector.ImageVector

interface Destinations {
    val route: String
    val icon: ImageVector
    val title: String
}

object Home: Destinations{
    override val route = "home"
    override val icon = Icons.Filled.Home
    override val title = "Home Screen"
}

object Scan: Destinations {
    override val route = "scan"
    override val icon = Icons.Filled.QrCodeScanner
    override val title = "Camera Screen"
}