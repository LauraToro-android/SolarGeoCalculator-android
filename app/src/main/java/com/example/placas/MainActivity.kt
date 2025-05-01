package com.example.placas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.placas.ui.screen.HomeScreen
import com.example.placas.ui.screen.LoginScreen
import com.example.placas.ui.screen.RegisterScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.placas.ui.screen.ForgotPasswordScreen
import com.example.placas.ui.screen.SettingsScreen
//Imports para hacer pruebas en main activity// se puede eliminar despuoes
import kotlinx.coroutines.launch
import android.util.Log
import androidx.lifecycle.lifecycleScope
import com.example.placas.data.calculate.CalculoNPlacas


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Pruebas calculo0 de placas
        //EJEMPLO DE USO:
        //CalculoNPlacas(latitud, longitud, anguloInclinacion, mes, energiaCalculada)
        val calculo = CalculoNPlacas(
            latitud = 41.553645,
            longitud = -0.707426,
            anguloInclinacion = 25,
            mes = 12,
            energiaCalculada = 6000
        )

        lifecycleScope.launch {
            val nPlacas = calculo.calcularNumeroPlacas()
            Log.d("Resultado", "Número necesario de placas solares: $nPlacas")
        }
        enableEdgeToEdge()
        setContent {

            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("home") { HomeScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("forgot_password") { ForgotPasswordScreen(navController) }
                composable("Settings") { SettingsScreen(navController) }

            }
        }
    }

}