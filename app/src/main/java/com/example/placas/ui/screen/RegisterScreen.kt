package com.example.placas.ui.screen

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.placas.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

// --------- MODELO DE ESTADO ---------
data class RegistrationState(
    var nombre: String = "",
    var isValidNombre: Boolean = false,
    var apellidos: String = "",
    var isValidApellidos: Boolean = false,
    var email: String = "",
    var isValidEmail: Boolean = false,
    var contrasena: String = "",
    var isValidPassword: Boolean = false,
    var passwordVisible: Boolean = false,
    var repetirContrasena: String = "",
    var repetirContrasenaVisible: Boolean = false,
    var isPasswordMatch: Boolean = true,
    var isRegistering: Boolean = false
)

// --------- VALIDACIONES ---------
fun isPasswordValid(password: String): Boolean {
    val pattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")
    return pattern.matches(password)
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

// --------- PANTALLA PRINCIPAL ---------
@Composable
fun RegisterScreen(auth: FirebaseAuth, navController: NavController) {
    val context = LocalContext.current
    var registrationState by remember { mutableStateOf(RegistrationState()) }

    registrationState = registrationState.copy(
        isValidNombre = registrationState.nombre.length >= 3,
        isValidApellidos = registrationState.apellidos.length >= 3,
        isValidEmail = isValidEmail(registrationState.email),
        isValidPassword = isPasswordValid(registrationState.contrasena),
        isPasswordMatch = registrationState.contrasena == registrationState.repetirContrasena
    )

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Card(
                modifier = Modifier.padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RowImage()

                    RowTextInput(
                        label = "Nombre",
                        value = registrationState.nombre,
                        isValid = registrationState.isValidNombre,
                        onValueChange = { registrationState = registrationState.copy(nombre = it) }
                    )

                    RowTextInput(
                        label = "Apellidos",
                        value = registrationState.apellidos,
                        isValid = registrationState.isValidApellidos,
                        onValueChange = { registrationState = registrationState.copy(apellidos = it) }
                    )

                    RowEmail(
                        email = registrationState.email,
                        emailChange = { registrationState = registrationState.copy(email = it) },
                        isValid = registrationState.isValidEmail
                    )

                    RowPassword(
                        contrasena = registrationState.contrasena,
                        passwordChange = { registrationState = registrationState.copy(contrasena = it) },
                        passwordVisible = registrationState.passwordVisible,
                        passwordVisibleChange = {
                            registrationState = registrationState.copy(passwordVisible = !registrationState.passwordVisible)
                        },
                        isValidPassword = registrationState.isValidPassword
                    )

                    RowRepeatPassword(
                        contrasena = registrationState.repetirContrasena,
                        passwordChange = { registrationState = registrationState.copy(repetirContrasena = it) },
                        passwordVisible = registrationState.repetirContrasenaVisible,
                        passwordVisibleChange = {
                            registrationState = registrationState.copy(repetirContrasenaVisible = !registrationState.repetirContrasenaVisible)
                        },
                        isValidPassword = registrationState.isPasswordMatch
                    )

                    RowButtonLogin(
                        auth = auth,
                        navController = navController,
                        email = registrationState.email,
                        contrasena = registrationState.contrasena,
                        isValidEmail = registrationState.isValidEmail,
                        isValidPassword = registrationState.isValidPassword && registrationState.isPasswordMatch,
                        isRegistering = registrationState.isRegistering,
                        isRegisteringChange = { registrationState = registrationState.copy(isRegistering = it) },
                        context = context
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

// --------- COMPONENTES COMPOSABLES ---------
@Composable
fun RowImage() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.width(100.dp),
            painter = painterResource(id = R.drawable.login),
            contentDescription = "Imagen login"
        )
    }
}

@Composable
fun RowTextInput(
    label: String,
    value: String,
    isValid: Boolean,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomOutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = label,
            isValid = isValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}

@Composable
fun RowEmail(email: String, emailChange: (String) -> Unit, isValid: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomOutlinedTextField(
            value = email,
            onValueChange = emailChange,
            label = "Correo electrónico",
            isValid = isValid,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
    }
}

@Composable
fun RowPassword(
    contrasena: String,
    passwordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordVisibleChange: () -> Unit,
    isValidPassword: Boolean
) {
    OutlinedTextField(
        value = contrasena,
        onValueChange = passwordChange,
        label = { Text("Contraseña") },
        isError = !isValidPassword,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = passwordVisibleChange) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Ver contraseña"
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth().padding(10.dp)
    )
}

@Composable
fun RowRepeatPassword(
    contrasena: String,
    passwordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordVisibleChange: () -> Unit,
    isValidPassword: Boolean
) {
    OutlinedTextField(
        value = contrasena,
        onValueChange = passwordChange,
        label = { Text("Repetir Contraseña") },
        isError = !isValidPassword,
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = passwordVisibleChange) {
                Icon(
                    imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                    contentDescription = "Ver contraseña"
                )
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        modifier = Modifier.fillMaxWidth().padding(10.dp)
    )
}

@Composable
fun RowButtonLogin(
    auth: FirebaseAuth,
    navController: NavController,
    email: String,
    contrasena: String,
    isValidEmail: Boolean,
    isValidPassword: Boolean,
    isRegistering: Boolean,
    isRegisteringChange: (Boolean) -> Unit,
    context: Context
) {
    Button(
        onClick = {
            if (!isValidEmail || !isValidPassword) {
                Toast.makeText(context, "Datos inválidos", Toast.LENGTH_SHORT).show()
                return@Button
            }

            isRegisteringChange(true)

            auth.createUserWithEmailAndPassword(email, contrasena)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    isRegisteringChange(false)
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") // Cambia "home" por tu destino real
                    } else {
                        Log.e("Firebase", "Error al registrar", task.exception)
                        Toast.makeText(context, "Error: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
        },
        enabled = !isRegistering,
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
    ) {
        Text(if (isRegistering) "Registrando..." else "Registrarse")
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isValid: Boolean,
    keyboardOptions: KeyboardOptions
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = !isValid,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (isValid) Color(0xFF6200EE) else Color.Red,
            unfocusedBorderColor = if (isValid) Color.Gray else Color.Red
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
