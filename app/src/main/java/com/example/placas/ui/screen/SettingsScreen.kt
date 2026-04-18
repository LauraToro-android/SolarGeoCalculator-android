package com.example.placas.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, top = 50.dp)
    ) {

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {

            item {
                Text(
                    text = "Energía Solar ☀️",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF043f70)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Esta aplicación calcula el número de placas solares necesarias en función del consumo energético y la ubicación."
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("📊 Datos utilizados", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text("• Ubicación: determina la radiación solar")
                Text("• Consumo energético: según electrodomésticos")
                Text("• Mes: influye en la luz solar disponible")
                Text("• Ángulo: afecta al rendimiento")
                Text("• Potencia de placa: capacidad de generación")
                Text("• Margen: factor de seguridad")
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("⚙️ ¿Cómo se calcula?", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Se calcula el consumo máximo en una franja horaria y se estima cuántas placas solares son necesarias según la radiación solar."
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("🔋 Interpretación del resultado", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "El número de placas es una estimación del sistema necesario para cubrir el consumo máximo."
                )
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("💡 Consejos", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text("• Orientar al sur mejora el rendimiento")
                Text("• Evitar sombras")
                Text("• Limpieza periódica")
                Text("• Ajustar inclinación")
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text("⚠️ Limitaciones", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                Text("• No considera sombras exactas")
                Text("• No incluye pérdidas reales")
                Text("• Es una estimación")
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF5F5F5)
                    ),
                    modifier = Modifier.fillMaxWidth()

                ) {
                    Text(
                        text = "⚠️ El resultado es orientativo y puede variar según condiciones reales.",
                        modifier = Modifier.padding(12.dp),
                        fontSize = 13.sp,
                        color = Color.DarkGray
                    )
                }
            }
        }

        // 🔙 BOTÓN FIJO ABAJO
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, end = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xE1128D93),
                contentColor = Color.White
            )
        ) {
            Text("Volver atrás")
        }
        Spacer(modifier = Modifier.height(20.dp))
    }
}