package com.example.placas.ui.screen


import com.example.placas.R
import android.content.Context
import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Preview(showBackground = true)
@Composable
fun Preview(){
    val navController= rememberNavController()
    LoginScreen(navController)
}


@Composable
fun LoginScreen(navController: NavController) {

    val context = LocalContext.current
    var nombre by remember { mutableStateOf("") }
    var isValidNombre by remember { mutableStateOf(false) }
    var apellidos by remember { mutableStateOf("") }
    var isValidApellidos by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var isValidEmail by remember { mutableStateOf(false) }
    var contrasena by remember { mutableStateOf("") }
    var isValidPassword by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var repetirContrasena by remember { mutableStateOf("") }
    var repetirContrasenaVisible by remember { mutableStateOf(false) }
    var isPasswordMatch by remember { mutableStateOf(true) }

    Column {

        RowEmail(email = email, emailChange = { email = it }, isValid = isValidEmail)
    }



    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Column(
            Modifier
                .align(Alignment.Center)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Card(
                Modifier.padding(12.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White)

            ) {
                Column(Modifier.padding(16.dp)) {
                    RowImage()
                    RowNombre(
                        nombre = nombre,
                        nombreChange= {
                            nombre= it
                            isValidNombre = it.length >= 3
                        },
                        isValid = isValidNombre
                    )
                    RowApellidos(
                        apellidos = apellidos,
                        apellidosChange= {
                            nombre= it
                            isValidApellidos = it.length >= 3
                        },


                        isValid = isValidApellidos
                    )

                    RowEmail(
                        email = email,
                        emailChange = {
                            email = it
                            isValidEmail = Patterns.EMAIL_ADDRESS.matcher(it).matches()
                        },
                        isValid = isValidEmail
                    )
                    RowPassword(
                        contrasena = contrasena,
                        passwordChange = {
                            contrasena = it
                            isValidPassword = isPasswordValid(it)
                            isPasswordMatch = repetirContrasena == it
                        },
                        passwordVisible = passwordVisible,
                        passwordVisibleChange = { passwordVisible = !passwordVisible },
                        isValidPassword = isValidPassword
                    )
                    RowRepeatPassword(
                        contrasena = repetirContrasena,
                        passwordChange = {
                            repetirContrasena = it
                            isPasswordMatch = contrasena == it
                        },
                        passwordVisible = repetirContrasenaVisible,
                        passwordVisibleChange = { repetirContrasenaVisible = !repetirContrasenaVisible },
                        isValidPassword = isPasswordMatch )

                    RowButtonLogin(
                        context= context,
                        isValidEmail=isValidEmail,
                        isValidPassword = isValidPassword && isPasswordMatch,
                        navController = navController,


                        )
                    Spacer(modifier = Modifier.height(8.dp))


                }
            }
        }
    }
}

@Composable
fun RowButtonLogin(
    context: Context,
    navController: NavController,
    isValidEmail: Boolean,
    isValidPassword: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Button( shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xE1128D93)
            ),
            onClick = {

                /*if (isValidEmail && isValidPassword) {
                    Toast.makeText(context, "Inicio de sesión exitoso", Toast.LENGTH_LONG).show()
                    navController.navigate("home")
                } else {
                    Toast.makeText(context, "Por favor, ingresa datos válidos", Toast.LENGTH_SHORT).show()
                }*/
                navController.navigate("home")
            },
            //enabled = isValidEmail && isValidPassword
        ) {
            Text("Registrar")
        }
    }
}



fun login(context: Context) {
    Toast.makeText(context, "FAKE LOGIN :)", Toast.LENGTH_LONG).show()
}
fun isPasswordValid(password: String): Boolean {
    val passwordPattern = Regex("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#\$%^&+=!]).{8,}\$")
    return password.matches(passwordPattern)
}



@Composable
fun RowPassword(
    contrasena: String,
    passwordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordVisibleChange: () -> Unit,
    isValidPassword: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = contrasena,
            onValueChange = passwordChange,
            maxLines = 1,
            singleLine = true,
            label = { Text(text = "Contraseña") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),

            shape = RoundedCornerShape(12.dp),

            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            //agrego el icono de ojo
            trailingIcon = {
                IconButton(onClick = passwordVisibleChange) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            isError = !isValidPassword && contrasena.isNotEmpty(),
            supportingText = {
                if (!isValidPassword && contrasena.isNotEmpty()) {
                    Text(
                        text = "Debe tener al menos 8 caracteres, una mayúscula,\nun número y un símbolo especial.",
                        color = Color.Red
                    )
                }
            },

            )
    }
}
@Composable
fun RowRepeatPassword(
    contrasena: String,
    passwordChange: (String) -> Unit,
    passwordVisible: Boolean,
    passwordVisibleChange: () -> Unit,
    isValidPassword: Boolean
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = contrasena,
            onValueChange = passwordChange,
            maxLines = 1,
            singleLine = true,
            label = { Text(text = " Repite Contraseña") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),

            shape = RoundedCornerShape(12.dp),

            visualTransformation = if (passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            //agrego el icono de ojo
            trailingIcon = {
                IconButton(onClick = passwordVisibleChange) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            isError = !isValidPassword && contrasena.isNotEmpty(),
            supportingText = {
                if (!isValidPassword && contrasena.isNotEmpty()) {
                    Text(
                        text = "Las contraseñas no coinciden.",
                        color = Color.Red
                    )
                }
            },


            )
    }
}


@Composable
fun RowEmail(
    email: String,
    emailChange: (String) -> Unit,
    isValid: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = emailChange,
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            maxLines = 1,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(

                focusedBorderColor = if (isValid) Color.Green else Color.Red,
                unfocusedBorderColor = Color.Black,
                focusedLabelColor = if (isValid) Color.Green else Color.Red,
                unfocusedLabelColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        )

    }
}

@Composable
fun RowImage() {
    Row(
        Modifier.fillMaxWidth().padding(10.dp),
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
fun RowNombre(
    nombre: String,
    nombreChange: (String) -> Unit,
    isValid: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = nombre,
            onValueChange = nombreChange,
            label = { Text(text = "Nombre") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(12.dp),
            maxLines = 1,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = if (isValid) Color.Green else Color.Red,
                focusedBorderColor = if (isValid) Color.Green else Color.Red
            )
        )
    }
}
@Composable
fun RowApellidos(
    apellidos: String,
    apellidosChange: (String) -> Unit,
    isValid: Boolean
) {var apellidos by remember { mutableStateOf("")}
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        horizontalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = apellidos,
            onValueChange = {apellidos=it},
            label = { Text(text = "Apellidos") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            shape = RoundedCornerShape(12.dp),
            maxLines = 1,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedLabelColor = if (isValid) Color.Green else Color.Red,
                focusedBorderColor = if (isValid) Color.Green else Color.Red,

                )
        )
    }
}
