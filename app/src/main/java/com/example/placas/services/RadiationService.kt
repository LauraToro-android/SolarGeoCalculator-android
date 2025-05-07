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
}
