package com.example.placas.ui.screen

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    val isValidEmail = remember(email) { Patterns.EMAIL_ADDRESS.matcher(email).matches() }
    val context = LocalContext.current
    val firebaseAuth = remember { FirebaseAuth.getInstance() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F0)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Recuperar Contraseña",
                color = Color(0xFF1A237E),
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xE1128D93),
                    focusedLabelColor = Color(0xE1128D93),
                    cursorColor = Color(0xE1128D93)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Correo de recuperación enviado",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.popBackStack()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Error al enviar el correo",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                },
                enabled = isValidEmail,
                modifier = Modifier.fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xE1128D93)
                )
            ) {
                Text(text = "Enviar enlace de recuperación", color = Color.White)
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver al inicio de sesión", color = Color(0xE1128D93))
            }
        }
    }
}
