package com.example.qrcodescanner

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavigationScreen(){
    val navController: NavHostController = rememberNavController()
    Scaffold(
        bottomBar = {
            BottomTabNavigation(navController)
        },
    ){padding->
        NavHost(navController = navController, startDestination = Home.route){
            composable(route = Home.route){
                HomeScreen(padding)
            }
            composable(route = Scan.route){
                CameraScreen(padding)
            }
        }
    }
}