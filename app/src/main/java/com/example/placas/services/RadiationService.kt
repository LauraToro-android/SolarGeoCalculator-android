package com.example.placas.services

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object RadiationService {
    suspend fun fetchAnnualRadiation(lat: Double, lon: Double): String = withContext(Dispatchers.IO) {
        val url = "https://re.jrc.ec.europa.eu/api/MRcalc?lat=$lat&lon=$lon&horirrad=1&outputformat=json"

        val response = URL(url).readText()
        val jsonObject = JSONObject(response)

        val outputs = jsonObject.getJSONObject("outputs")
        val monthlyData = outputs.getJSONArray("monthly")
        val yearlyData = monthlyData.getJSONObject(12)
        val radiationValue = yearlyData.getDouble("H(h)_m")

        return@withContext "$radiationValue kWh/m²"
    }

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

        return@withContext minMonth.toString() // Ej: "12" para diciembre
    }

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
