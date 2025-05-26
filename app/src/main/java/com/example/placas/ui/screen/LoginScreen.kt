package com.example.placas.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.placas.R
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(auth: FirebaseAuth, navController: NavController) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    fun handleLogin() {
        if (username.isBlank() || password.isBlank()) {
            Toast.makeText(context, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            isError = true
            return
        }
        isLoading = true
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    navController.navigate("home")
                } else {
                    Log.e("Firebase", "Error, datos incorrectos.", task.exception)
                    isError = true
                    Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Image(
                painter = painterResource(id = R.drawable.logoplacas),
                contentDescription = "Logo de la aplicación",
                modifier = Modifier.size(200.dp)
            )
        }

        item {
            OutlinedTextField(
                value = username,
                onValueChange = {
                    username = it
                    isError = false
                },
                label = { Text("Correo electrónico") },
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE8DFDF),
                    unfocusedContainerColor = Color(0xFFE8DFDF),
                    focusedIndicatorColor = Color(0xFF006064),
                    unfocusedIndicatorColor = Color.Gray,
                    errorIndicatorColor = Color.Red
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    isError = false
                },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = isError,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFE8DFDF),
                    unfocusedContainerColor = Color(0xFFE8DFDF),
                    focusedIndicatorColor = Color(0xFF006064),
                    unfocusedIndicatorColor = Color.Gray,
                    errorIndicatorColor = Color.Red
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }

        if (isError) {
            item {
                Text("Usuario o contraseña incorrectos", color = Color.Red)
            }
        }

        item {
            Button(
                onClick = { handleLogin() },
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xE1128D93)
                )
            ) {
                Text(
                    text = if (isLoading) "Iniciando..." else "Iniciar sesión",
                    color = Color.White
                )
            }
        }

        item {
            Text(
                text = "¿Olvidaste tu contraseña?",
                color = Color(0xFF1A237E),
                fontSize = 10.sp,
                modifier = Modifier.clickable {
                    navController.navigate("forgot_password")
                }
            )
        }

        item {
            Button(
                onClick = {
                    navController.navigate("register")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xE1128D93)
                )
            ) {
                Text("Registrarse", color = Color.White)
            }
        }


    }
}
