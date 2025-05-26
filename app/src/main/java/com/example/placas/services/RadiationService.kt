package com.example.placas.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object RadiationService {

    /**
     * Consulta el valor de radiación solar anual horizontal (kWh/m²) para una ubicación geográfica.
     *
     * @param lat Latitud de la ubicación.
     * @param lon Longitud de la ubicación.
     * @return Valor de radiación en formato texto (ej. "1234.56 kWh/m²").
     */
    suspend fun fetchAnnualRadiation(lat: Double, lon: Double): String = withContext(Dispatchers.IO) {
        val url = "https://re.jrc.ec.europa.eu/api/MRcalc?lat=$lat&lon=$lon&horirrad=1&outputformat=json"

         // Realiza la llamada HTTP a la API y lee la respuesta como texto.
        val response = URL(url).readText()
        val jsonObject = JSONObject(response)

        // Accede al objeto JSON de salida, específicamente a los datos mensuales.
        val outputs = jsonObject.getJSONObject("outputs")
        val monthlyData = outputs.getJSONArray("monthly")
        // El índice 12 representa el resumen anual (según la API del JRC).
        val yearlyData = monthlyData.getJSONObject(12)
        val radiationValue = yearlyData.getDouble("H(h)_m")

          // Devuelve el valor con unidad como String.
        return@withContext "$radiationValue kWh/m²"
    }

    /**
     * Función de alto nivel que valida la entrada y luego obtiene la radiación anual.
     *
     * @param lat Latitud en formato String.
     * @param lon Longitud en formato String.
     * @return Resultado exitoso o fallo con el mensaje de error correspondiente.
     */


    suspend fun fetchRadiation(lat: String, lon: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (lat.isEmpty() || lon.isEmpty()) {
                Result.failure(Exception("Latitud y longitud son requeridas"))
            } else {
                val result = fetchAnnualRadiation(lat.toDouble(), lon.toDouble())
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Consulta el mes con menor radiación solar inclinada para una ubicación y ángulo.
     *
     * @param lat Latitud en grados.
     * @param lon Longitud en grados.
     * @param angle Ángulo de inclinación de los paneles (en grados).
     * @return Número del mes con peor radiación (ej. "12" para diciembre).
     */
    suspend fun fetchWorstMonth(lat: Double, lon: Double, angle: Double): String = withContext(Dispatchers.IO) {
        val url = "https://re.jrc.ec.europa.eu/api/MRcalc?lat=$lat&lon=$lon&horirrad=0&selectrad=1&angle=$angle&startyear=2023&outputformat=json"

        val response = URL(url).readText()
        val jsonObject = JSONObject(response)

        val outputs = jsonObject.getJSONObject("outputs")
        val monthlyData = outputs.getJSONArray("monthly")

        var minMonth = -1
        var minValue = Double.MAX_VALUE

        for (i in 0 until monthlyData.length()) {
            val monthObject = monthlyData.getJSONObject(i)
            val month = monthObject.getInt("month")
            val hi_m = monthObject.getDouble("H(i)_m")

            if (hi_m < minValue) {
                minValue = hi_m
                minMonth = month
            }
        }

        // Devuelve el número del mes como String.

        return@withContext minMonth.toString() // Ej: "12" para diciembre
    }

    /**
     * Función de alto nivel que valida la entrada y obtiene el peor mes de radiación.
     *
     * @param lat Latitud en formato String.
     * @param lon Longitud en formato String.
     * @param angle Ángulo de inclinación en formato String.
     * @return Resultado exitoso con el mes, o fallo con mensaje de error.
     */

    suspend fun fetchWorstMonthResult(lat: String, lon: String, angle: String): Result<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            if (lat.isEmpty() || lon.isEmpty() || angle.isEmpty()) {
                Result.failure(Exception("Latitud, longitud y ángulo son requeridos"))
            } else {
                val result = fetchWorstMonth(lat.toDouble(), lon.toDouble(), angle.toDouble())
                Result.success(result)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
