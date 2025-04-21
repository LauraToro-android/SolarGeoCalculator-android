package com.example.placas.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

object OpenStreetMapService {

    fun initConfig(context: Context) {
        val prefs = context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE)

        Configuration.getInstance().load(context, prefs)
        Configuration.getInstance().userAgentValue = context.packageName

        val cacheDir = File(context.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidBasePath = cacheDir
        Configuration.getInstance().osmdroidTileCache = File(cacheDir, "tiles")
    }

    fun crearMapa(context: Context, lat: Double = 40.4168, lon: Double = -3.7038, zoom: Double = 15.0): MapView {
        val map = MapView(context)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)

        val controller = map.controller
        controller.setZoom(zoom)
        controller.setCenter(GeoPoint(lat, lon))

        return map
    }

    fun crearMapaConUbicacion(context: Context, lat: Double, lon: Double): MapView {
        val mapView = MapView(context)
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setMultiTouchControls(true)

        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(GeoPoint(lat, lon))

        val marcador = Marker(mapView).apply {
            position = GeoPoint(lat, lon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Tu ubicación"
        }

        mapView.overlays.add(marcador)
        return mapView
    }

    @SuppressLint("MissingPermission")
    fun obtenerUbicacion(context: Context, onUbicacionObtenida: (Location) -> Unit) {
        val locationProvider = LocationServices.getFusedLocationProviderClient(context)

        locationProvider.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onUbicacionObtenida(location)
                }
            }
    }
}