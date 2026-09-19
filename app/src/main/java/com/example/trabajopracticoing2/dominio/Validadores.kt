package com.example.trabajopracticoing2.dominio

/**
 * Resultado de una validacion.
 * Se mantiene independiente del framework Android para poder probarlo con JUnit.
 */
sealed class ResultadoValidacion {
    object Valido : ResultadoValidacion()
    data class Invalido(val motivo: Motivo) : ResultadoValidacion()

    val esValido: Boolean
        get() = this is Valido
}

/** Motivos de error posibles, resueltos a texto por la capa de UI. */
enum class Motivo {
    OBLIGATORIO,
    DEMASIADO_CORTO,
    NUMERO_INVALIDO,
    SIN_SELECCION,
    LEGAJO_INVALIDO,
    CLAVE_INVALIDA
}

/** Validaciones de los formularios del prototipo. */
object Validadores {

    fun textoObligatorio(valor: String?, minimo: Int = 1): ResultadoValidacion {
        val texto = valor?.trim().orEmpty()
        if (texto.isEmpty()) return ResultadoValidacion.Invalido(Motivo.OBLIGATORIO)
        if (texto.length < minimo) return ResultadoValidacion.Invalido(Motivo.DEMASIADO_CORTO)
        return ResultadoValidacion.Valido
    }

    fun seleccion(valor: String?): ResultadoValidacion =
        if (valor.isNullOrBlank()) ResultadoValidacion.Invalido(Motivo.SIN_SELECCION)
        else ResultadoValidacion.Valido

    fun enteroEnRango(valor: String?, minimo: Int, maximo: Int): ResultadoValidacion {
        val texto = valor?.trim().orEmpty()
        if (texto.isEmpty()) return ResultadoValidacion.Invalido(Motivo.OBLIGATORIO)
        val numero = texto.toIntOrNull() ?: return ResultadoValidacion.Invalido(Motivo.NUMERO_INVALIDO)
        if (numero < minimo || numero > maximo) {
            return ResultadoValidacion.Invalido(Motivo.NUMERO_INVALIDO)
        }
        return ResultadoValidacion.Valido
    }

    fun legajo(valor: String?): ResultadoValidacion {
        val texto = valor?.trim().orEmpty()
        if (texto.length != 4 || texto.any { !it.isDigit() }) {
            return ResultadoValidacion.Invalido(Motivo.LEGAJO_INVALIDO)
        }
        return ResultadoValidacion.Valido
    }

    fun clave(valor: String?): ResultadoValidacion {
        val texto = valor.orEmpty()
        if (texto.length < 4) return ResultadoValidacion.Invalido(Motivo.CLAVE_INVALIDA)
        return ResultadoValidacion.Valido
    }
}
