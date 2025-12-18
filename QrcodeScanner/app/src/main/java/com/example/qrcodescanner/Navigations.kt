package com.example.qrcodescanner

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun NavigationScreen(database1: ScannerInventory, menuLiveData: MutableLiveData<List<Scanner>>) {
    val navController: NavHostController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomTabNavigation(navController)
        },
    ){padding->
        val scope = rememberCoroutineScope()
        var length by remember { mutableIntStateOf(0) }
        NavHost(navController = navController, startDestination = Home.route){
            composable(route = Home.route){
                HomeScreen(padding, menuLiveData, database1) {
                    scope.launch(Dispatchers.IO) {
                        database1.deleteItem(it)
                    }
                }
            }
            composable(route = Scan.route){
                CameraScreen(padding){
                    scope.launch(Dispatchers.IO) {
                        var date = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")).toString()
                        var time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
                        database1.insertItem(Scanner( content = it, time = time, date = date))
                    }
                }
            }
        }
    }
}