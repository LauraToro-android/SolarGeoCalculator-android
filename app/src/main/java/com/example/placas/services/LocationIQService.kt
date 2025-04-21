package com.example.placas.services

import android.util.Log
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/** MODELOS **/
data class LocationIQResponseItem(
    val lat: String,
    val lon: String,
    val display_name: String
)

data class ReverseGeocodeResponse(
    val display_name: String,
    val address: Address?
)

data class Address(
    val road: String?,
    val suburb: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val postcode: String?
)

/** INTERFAZ DE RETROFIT **/
interface LocationIQApi {
    @GET("v1/search.php")
    fun geocodeAddress(
        @Query("key") apiKey: String,
        @Query("q") address: String,
        @Query("format") format: String = "json"
    ): Call<List<LocationIQResponseItem>>

    @GET("v1/reverse.php")
    fun reverseGeocode(
        @Query("key") apiKey: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("format") format: String = "json"
    ): Call<ReverseGeocodeResponse>
}

/** SERVICIO HELPER **/
object LocationIQService {
    private const val BASE_URL = "https://us1.locationiq.com/"
    private const val API_KEY = "pk.2025b4f7e4e9f81b4300afc4ae0eae0d"

    private val api: LocationIQApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(LocationIQApi::class.java)
    }

    fun geocode(address: String, onResult: (lat: String, lon: String, name: String) -> Unit) {
        api.geocodeAddress(API_KEY, address).enqueue(object : Callback<List<LocationIQResponseItem>> {
            override fun onResponse(
                call: Call<List<LocationIQResponseItem>>,
                response: Response<List<LocationIQResponseItem>>
            ) {
                if (response.isSuccessful) {
                    val result = response.body()?.firstOrNull()
                    result?.let {
                        onResult(it.lat, it.lon, it.display_name)
                    } ?: Log.e("LocationIQ", "Sin resultados")
                } else {
                    Log.e("LocationIQ", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<LocationIQResponseItem>>, t: Throwable) {
                Log.e("LocationIQ", "Fallo: ${t.message}")
            }
        })
    }

    fun reverseGeocode(lat: String, lon: String, onResult: (address: String) -> Unit) {
        api.reverseGeocode(API_KEY, lat, lon).enqueue(object : Callback<ReverseGeocodeResponse> {
            override fun onResponse(
                call: Call<ReverseGeocodeResponse>,
                response: Response<ReverseGeocodeResponse>
            ) {
                if (response.isSuccessful) {
                    val address = response.body()?.display_name ?: "Dirección no encontrada"
                    onResult(address)
                } else {
                    Log.e("LocationIQ", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ReverseGeocodeResponse>, t: Throwable) {
                Log.e("LocationIQ", "Fallo: ${t.message}")
            }
        })
    }

    /** FUNCIONES VERIFICACIÓN **/

    fun coordenadasValidas(latitud: Double, longitud: Double): Boolean {
        val latitudValida = latitud in -90.0..90.0
        val longitudValida = longitud in -180.0..180.0
        return latitudValida && longitudValida
    }
}





