package com.example.placas.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import android.view.ViewGroup
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

object OpenStreetMapService {


    /**
     * Inicializa la configuración de OSMDroid.
     *
     * Debe llamarse antes de usar cualquier MapView para establecer las rutas de caché
     * y configurar el agente de usuario con el nombre del paquete.
     *
     * @param context Contexto de la aplicación o actividad.
     */
    fun initConfig(context: Context) {
        val prefs = context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)

        Configuration.getInstance().load(context, prefs)
        Configuration.getInstance().userAgentValue = context.packageName
        // Define rutas personalizadas para caché de mapas
        val cacheDir = File(context.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidBasePath = cacheDir
        Configuration.getInstance().osmdroidTileCache = File(cacheDir, "tiles")
    }

    /**
     * Crea un MapView simple centrado en una ubicación específica.

     * @param context Contexto necesario para instanciar el MapView.
     * @param lat Latitud de la ubicación inicial del mapa. Valor por defecto: Madrid (40.4168).
     * @param lon Longitud de la ubicación inicial del mapa. Valor por defecto: Madrid (-3.7038).
     * @param zoom Nivel de zoom inicial. Por defecto 15.0.
     * @return Instancia de MapView lista para mostrar.
     */
    fun crearMapa(context: Context, lat: Double = 40.4168, lon: Double = -3.7038, zoom: Double = 15.0): MapView {
        val map = MapView(context)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val controller = map.controller
        controller.setZoom(zoom)
        controller.setCenter(GeoPoint(lat, lon))

        return map
    }


    /**
     * Crea un MapView centrado en una ubicación e incluye un marcador con la posición.
     *
     * @param context Contexto necesario para instanciar el MapView.
     * @param lat Latitud del marcador y del centro del mapa.
     * @param lon Longitud del marcador y del centro del mapa.
     * @return Instancia de MapView con marcador incluido.
     */
    private var marcadorGlobal: Marker? = null
    private var mapViewGlobal: MapView? = null

    fun crearMapaConUbicacion(
        context: Context,
        lat: Double,
        lon: Double
    ): MapView {

        val mapView = MapView(context).apply {

            // 🔑 ESTO ES LO QUE FALTABA
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(true)

            controller.setZoom(15.0)
            controller.setCenter(GeoPoint(lat, lon))
        }

        // 📍 Marcador
        val marcador = Marker(mapView).apply {
            position = GeoPoint(lat, lon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Tu ubicación"
        }

        mapView.overlays.add(marcador)

        marcadorGlobal = marcador
        mapViewGlobal = mapView

        return mapView
    }


    fun actualizarUbicacion(lat: Double, lon: Double) {
        val mapView = mapViewGlobal ?: return
        val marcador = marcadorGlobal ?: return

        marcador.position = GeoPoint(lat, lon)
        mapView.controller.animateTo(GeoPoint(lat, lon))
        mapView.invalidate() // fuerza redraw
    }

    /**
     * Obtiene la última ubicación conocida del dispositivo usando FusedLocationProvider.
     *
     * ⚠️ Requiere permisos de ubicación concedidos (ACCESS_FINE_LOCATION o ACCESS_COARSE_LOCATION).
     *
     * @param context Contexto necesario para acceder a los servicios de ubicación.
     * @param onUbicacionObtenida Callback que recibe la ubicación si se obtiene correctamente.
     */
    @SuppressLint("MissingPermission")
    fun obtenerUbicacion(context: Context, onUbicacionObtenida: (Location) -> Unit) {

        val locationProvider = LocationServices.getFusedLocationProviderClient(context)

        locationProvider.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location != null) {
                Log.d("GPS", "location = ${location.latitude}, ${location.longitude}")
                onUbicacionObtenida(location)
            } else {
                Log.d("GPS", "location = null (no se pudo obtener)")
            }
        }.addOnFailureListener {
            Log.d("GPS", "error obteniendo ubicación: ${it.message}")
        }
    }
}