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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

data class RegistrationState(
    var email: String = "",
    var isValidEmail: Boolean = false,
    var contrasena: String = "",
    var isValidPasswordFormat: Boolean = false,
    var passwordVisible: Boolean = false,
    var repetirContrasena: String = "",
    var repetirContrasenaVisible: Boolean = false,
    var doPasswordsMatch: Boolean = true,
    var isRegistering: Boolean = false,
    var registrationAttempted: Boolean = false
)

fun isPasswordValid(password: String): Boolean {
    val pattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!.]).{8,}\$")
    return pattern.matches(password)
}

fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

@Composable
fun RegisterScreen(auth: FirebaseAuth, navController: NavController) {
    val context = LocalContext.current
    var registrationState by remember { mutableStateOf(RegistrationState()) }
    val customColor = Color(0xE1128D93)

    LaunchedEffect(registrationState.email) {
        registrationState = registrationState.copy(isValidEmail = isValidEmail(registrationState.email))
    }
    LaunchedEffect(registrationState.contrasena) {
        registrationState = registrationState.copy(isValidPasswordFormat = isPasswordValid(registrationState.contrasena))
    }
    LaunchedEffect(registrationState.contrasena, registrationState.repetirContrasena) {
        registrationState = registrationState.copy(doPasswordsMatch = registrationState.contrasena == registrationState.repetirContrasena)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.padding(start = 15.dp, top = 30.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Volver",
                tint = customColor
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(0.9f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        RowImage()

                        RowEmail(
                            email = registrationState.email,
                            emailChange = { registrationState = registrationState.copy(email = it) },
                            showError = registrationState.registrationAttempted && !registrationState.isValidEmail
                        )

                        RowPassword(
                            contrasena = registrationState.contrasena,
                            passwordChange = { registrationState = registrationState.copy(contrasena = it) },
                            passwordVisible = registrationState.passwordVisible,
                            passwordVisibleChange = {
                                registrationState = registrationState.copy(passwordVisible = !registrationState.passwordVisible)
                            },
                            showError = registrationState.registrationAttempted && !registrationState.isValidPasswordFormat
                        )

                        RowRepeatPassword(
                            contrasena = registrationState.repetirContrasena,
                            passwordChange = { registrationState = registrationState.copy(repetirContrasena = it) },
                            passwordVisible = registrationState.repetirContrasenaVisible,
                            passwordVisibleChange = {
                                registrationState = registrationState.copy(repetirContrasenaVisible = !registrationState.repetirContrasenaVisible)
                            },
                            showError = registrationState.registrationAttempted && !registrationState.doPasswordsMatch
                        )

                        RowButtonLogin(
                            auth = auth,
                            navController = navController,
                            registrationState = registrationState,
                            onStateChange = { newState -> registrationState = newState },
                            context = context,
                            buttonColor = customColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

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
fun RowEmail(email: String, emailChange: (String) -> Unit, showError: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        CustomOutlinedTextField(
            value = email,
            onValueChange = emailChange,
            label = "Correo electrónico",
            showError = showError,
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
    showError: Boolean
) {
    OutlinedTextField(
        value = contrasena,
        onValueChange = passwordChange,
        label = { Text("Contraseña") },
        isError = showError,
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
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (!showError) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            unfocusedBorderColor = if (!showError) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.error
        )
    )
}

@Composable
fun RowRepeatPassword(
    contrasena: String,
    passwordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordVisibleChange: () -> Unit,
    showError: Boolean
) {
    OutlinedTextField(
        value = contrasena,
        onValueChange = passwordChange,
        label = { Text("Repetir Contraseña") },
        isError = showError,
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
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (!showError) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            unfocusedBorderColor = if (!showError) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.error
        )
    )
}

@Composable
fun RowButtonLogin(
    auth: FirebaseAuth,
    navController: NavController,
    registrationState: RegistrationState,
    onStateChange: (RegistrationState) -> Unit,
    context: Context,
    buttonColor: Color
) {
    Button(
        onClick = {
            val isEmailCurrentlyValid = isValidEmail(registrationState.email)
            val isPasswordFormatCurrentlyValid = isPasswordValid(registrationState.contrasena)
            val doPasswordsCurrentlyMatch = registrationState.contrasena == registrationState.repetirContrasena

            var newState = registrationState.copy(
                isValidEmail = isEmailCurrentlyValid,
                isValidPasswordFormat = isPasswordFormatCurrentlyValid,
                doPasswordsMatch = doPasswordsCurrentlyMatch,
                registrationAttempted = true
            )
            onStateChange(newState)

            if (!newState.isValidEmail || !newState.isValidPasswordFormat || !newState.doPasswordsMatch) {
                Toast.makeText(context, "Por favor, corrige los errores indicados.", Toast.LENGTH_SHORT).show()
                return@Button
            }

            newState = newState.copy(isRegistering = true)
            onStateChange(newState)

            auth.createUserWithEmailAndPassword(newState.email, newState.contrasena)
                .addOnCompleteListener { task: Task<AuthResult> ->
                    val finalState: RegistrationState
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        finalState = newState.copy(isRegistering = false)
                        navController.navigate("home") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                        }
                    } else {
                        Log.e("Firebase", "Error al registrar", task.exception)
                        Toast.makeText(context, "Error de registro: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                        finalState = newState.copy(isRegistering = false)
                    }
                    onStateChange(finalState)
                }
        },
        enabled = !registrationState.isRegistering,
        modifier = Modifier.fillMaxWidth().padding(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = buttonColor,
            contentColor = Color.White,
            disabledContainerColor = Color.LightGray,
            disabledContentColor = Color.DarkGray
        )
    ) {
        Text(if (registrationState.isRegistering) "Registrando..." else "Registrarse")
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    showError: Boolean,
    keyboardOptions: KeyboardOptions
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = showError,
        singleLine = true,
        keyboardOptions = keyboardOptions,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (!showError) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
            unfocusedBorderColor = if (!showError) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.error
        ),
        modifier = Modifier.fillMaxWidth()
    )
}