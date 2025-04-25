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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore


class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth


    //private lateinit var ddbb: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        //ddbb = Firebase.firestore
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(auth,navController) }
                composable("home") { HomeScreen(navController) }
                composable("register") { RegisterScreen(auth,navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController)  }

            }
        }
    }
    override fun onStart() {
        super.onStart()
        val currentUser: FirebaseUser? = auth.currentUser
        if (currentUser != null){
            //Si estas registrado ACCEDER DIRECTAMENTE
            auth.signOut()
        }
    }
}