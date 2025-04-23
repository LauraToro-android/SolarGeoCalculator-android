package com.example.placas.ui.screen

import androidx.activity.compose.setContent
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.placas.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    fun handleLogin() {
        if (username == "usuario" && password == "contraseña") {
            isError = false
        } else {
            isError = true
        }

        navController.navigate("home")
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
                onValueChange = { username = it },
                label = { Text("Usuario") },
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
                onValueChange = { password = it },
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xE1128D93)
                )
            ) {
                Text(
                    text = "Iniciar sesión",
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

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
}

