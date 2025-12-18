package com.example.qrcodescanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.MutableLiveData

class MainActivity : ComponentActivity() {
    var menuLiveData = MutableLiveData<List<Scanner>>()
    val database by lazy {
        ScannerInventory(this)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuLiveData.value = database.getAllItems().value
        enableEdgeToEdge()
        setContent {
            NavigationScreen(database, menuLiveData)
        }
    }
}

