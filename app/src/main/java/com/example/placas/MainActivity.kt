package com.example.placas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.placas.ui.screen.HomeScreen
import com.example.placas.ui.screen.LoginScreen
import com.example.placas.ui.screen.RegisterScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.placas.ui.screen.ForgotPasswordScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("home") { HomeScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController)  }

            }
        }
    }
}