package com.example.placas.services

import android.util.Log
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

/** MODELOS **/
/**
 * Modelo para la respuesta de búsqueda directa (geocodificación).
 * Representa una ubicación con latitud, longitud y nombre completo.
 */
data class LocationIQResponseItem(
    val lat: String,
    val lon: String,
    val display_name: String
)


/**
 * Modelo para la respuesta de búsqueda inversa (reverse geocoding).
 * Contiene el nombre completo de la dirección y un objeto `Address` más detallado.
 */
data class ReverseGeocodeResponse(
    val display_name: String,
    val address: Address?
)


/**
 * Dirección detallada extraída de una búsqueda inversa.
 */
data class Address(
    val road: String?,
    val suburb: String?,
    val city: String?,
    val state: String?,
    val country: String?,
    val postcode: String?
)

/** INTERFAZ DE RETROFIT **/

/**
 * Interfaz para consumir la API de LocationIQ usando Retrofit.
 * Proporciona funciones para geocodificación directa e inversa.
 */
interface LocationIQApi {
        /**
     * Geocodifica una dirección (texto) y devuelve una lista de posibles coincidencias.
     *
     * @param apiKey Tu clave de API.
     * @param address Dirección a buscar.
     * @param format Formato de la respuesta (por defecto "json").
     */
    @GET("v1/search.php")
    fun geocodeAddress(
        @Query("key") apiKey: String,
        @Query("q") address: String,
        @Query("format") format: String = "json"
    ): Call<List<LocationIQResponseItem>>

        /**
     * Realiza una búsqueda inversa: convierte lat/lon en una dirección legible.
     *
     * @param apiKey Tu clave de API.
     * @param lat Latitud.
     * @param lon Longitud.
     * @param format Formato de la respuesta (por defecto "json").
     */
    @GET("v1/reverse.php")
    fun reverseGeocode(
        @Query("key") apiKey: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("format") format: String = "json"
    ): Call<ReverseGeocodeResponse>
}

/** SERVICIO HELPER **/


/**
 * Objeto singleton que encapsula la lógica para interactuar con la API de LocationIQ.
 */
object LocationIQService {
    private const val BASE_URL = "https://us1.locationiq.com/"
    private const val API_KEY = "pk.2025b4f7e4e9f81b4300afc4ae0eae0d"

    private val api: LocationIQApi

    init {
                // Inicializa Retrofit con el endpoint base y el convertidor JSON (Gson).
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(LocationIQApi::class.java)
    }

        /**
     * Realiza una geocodificación directa (de dirección a coordenadas).
     *
     * @param address Dirección que el usuario introduce.
     * @param onResult Callback que recibe latitud, longitud y nombre completo si hay resultado.
     */
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


    /**
     * Realiza una geocodificación inversa (de coordenadas a dirección).
     *
     * @param lat Latitud de la ubicación.
     * @param lon Longitud de la ubicación.
     * @param onResult Callback que recibe la dirección legible como String.
     */
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

        /**
     * Verifica si las coordenadas están dentro de los rangos válidos para latitud y longitud.
     *
     * @param latitud Latitud en grados (-90 a 90).
     * @param longitud Longitud en grados (-180 a 180).
     * @return true si ambas coordenadas son válidas.
     */
    fun coordenadasValidas(latitud: Double, longitud: Double): Boolean {
        val latitudValida = latitud in -90.0..90.0
        val longitudValida = longitud in -180.0..180.0
        return latitudValida && longitudValida
    }
}





