package com.example.placas.data.model

data class Electrodomestico(
    //Se crea esta clse para meterlo en una base de datos

    var nombre: String,
    var potencia: Float = 0f,
    var consumo: Float = 0f,
    var franjaHoraria: String = "" //no sé si meterlo como string, boton o float
)