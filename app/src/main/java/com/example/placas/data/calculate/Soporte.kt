package com.example.placas.data.calculate


object Soporte {

    // Variables privadas para almacenar información de ubicación y parámetros de cálculo
    private var _latitud: Double? = 41.553645
    private var _longitud: Double? = -0.707426
    private var _anguloInclinacion: Int = 25
    private var _mes: Int? = 12
    private var _potenciaPlacaW: Int = 500
    private var _margen: Double = 0.8
    private var _energiaCalculada: Int = 6000

    // Getters y Setters públicos
    var latitud: Double?
        get() = _latitud
        set(value) {
            _latitud = value
        }

    var longitud: Double?
        get() = _longitud
        set(value) {
            _longitud = value
        }

    var anguloInclinacion: Int
        get() = _anguloInclinacion
        set(value) {
            _anguloInclinacion = value
        }

    var mes: Int?
        get() = _mes
        set(value) {
            _mes = value
        }

    var potenciaPlacaW: Int
        get() = _potenciaPlacaW
        set(value) {
            _potenciaPlacaW = value
        }

    var margen: Double
        get() = _margen
        set(value) {
            _margen = value
        }

    var energiaCalculada: Int
        get() = _energiaCalculada
        set(value) {
            _energiaCalculada = value
        }

}
