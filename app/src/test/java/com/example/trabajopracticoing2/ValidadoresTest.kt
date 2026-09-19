package com.example.trabajopracticoing2

import com.example.trabajopracticoing2.dominio.Motivo
import com.example.trabajopracticoing2.dominio.ResultadoValidacion
import com.example.trabajopracticoing2.dominio.Validadores
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Casos de prueba de las validaciones de formulario.
 *
 * Responden a la estrategia de Verificacion y Validacion pedida por la consigna:
 * se prueban situaciones normales, datos invalidos y valores limite.
 * Las validaciones viven en el paquete `dominio`, sin dependencias de Android,
 * justamente para poder verificarse con pruebas unitarias locales.
 */
class ValidadoresTest {

    // ------------------------------------------------- situaciones normales

    @Test
    fun `un texto con la longitud minima es valido`() {
        assertTrue(Validadores.textoObligatorio("Derrame en depósito", 5).esValido)
    }

    @Test
    fun `un legajo de cuatro digitos es valido`() {
        assertTrue(Validadores.legajo("1001").esValido)
    }

    @Test
    fun `una clave de cuatro caracteres es valida`() {
        assertTrue(Validadores.clave("1234").esValido)
    }

    // ------------------------------------------------------ datos invalidos

    @Test
    fun `un texto vacio se rechaza como obligatorio`() {
        val resultado = Validadores.textoObligatorio("   ", 5)
        assertFalse(resultado.esValido)
        assertEquals(Motivo.OBLIGATORIO, (resultado as ResultadoValidacion.Invalido).motivo)
    }

    @Test
    fun `un texto mas corto que el minimo se rechaza`() {
        val resultado = Validadores.textoObligatorio("Fuga", 5)
        assertFalse(resultado.esValido)
        assertEquals(Motivo.DEMASIADO_CORTO, (resultado as ResultadoValidacion.Invalido).motivo)
    }

    @Test
    fun `un legajo con letras se rechaza`() {
        val resultado = Validadores.legajo("10A1")
        assertFalse(resultado.esValido)
        assertEquals(Motivo.LEGAJO_INVALIDO, (resultado as ResultadoValidacion.Invalido).motivo)
    }

    @Test
    fun `un legajo con menos de cuatro digitos se rechaza`() {
        assertFalse(Validadores.legajo("101").esValido)
    }

    @Test
    fun `una clave corta se rechaza`() {
        val resultado = Validadores.clave("12")
        assertFalse(resultado.esValido)
        assertEquals(Motivo.CLAVE_INVALIDA, (resultado as ResultadoValidacion.Invalido).motivo)
    }

    @Test
    fun `una seleccion sin valor se rechaza`() {
        val resultado = Validadores.seleccion(null)
        assertFalse(resultado.esValido)
        assertEquals(Motivo.SIN_SELECCION, (resultado as ResultadoValidacion.Invalido).motivo)
    }

    @Test
    fun `un texto no numerico no es un entero valido`() {
        val resultado = Validadores.enteroEnRango("dos", 0, 999)
        assertFalse(resultado.esValido)
        assertEquals(Motivo.NUMERO_INVALIDO, (resultado as ResultadoValidacion.Invalido).motivo)
    }

    // -------------------------------------------------------- valores limite

    @Test
    fun `los extremos del rango son validos`() {
        assertTrue(Validadores.enteroEnRango("0", 0, 999).esValido)
        assertTrue(Validadores.enteroEnRango("999", 0, 999).esValido)
    }

    @Test
    fun `un valor fuera del rango se rechaza`() {
        assertFalse(Validadores.enteroEnRango("-1", 0, 999).esValido)
        assertFalse(Validadores.enteroEnRango("1000", 0, 999).esValido)
    }
}
