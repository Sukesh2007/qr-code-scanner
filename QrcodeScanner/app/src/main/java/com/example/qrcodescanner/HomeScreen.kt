package com.example.qrcodescanner

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen(pd: PaddingValues) {
    Surface(modifier = Modifier.padding(pd).fillMaxSize()) {
        Text(
            text = "Home Screen",
            color = Color.Black,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}