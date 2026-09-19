package com.example.trabajopracticoing2.ui.perfil

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.Permiso
import androidx.core.content.ContextCompat
import com.example.trabajopracticoing2.ui.comun.ActividadBase

/**
 * Perfil del usuario y matriz de operaciones habilitadas.
 *
 * Hace visible que operaciones estan restringidas para el perfil activo,
 * evitando que el usuario descubra la restriccion recien al intentar la accion.
 */
class PerfilActivity : ActividadBase() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)
        prepararRepositorio()
        configurarBarra(R.string.perfil_titulo)

        val usuario = SesionUsuario.usuarioActual
        if (usuario == null) {
            finish()
            return
        }

        findViewById<TextView>(R.id.textoPerfilNombre).text = usuario.nombre
        findViewById<TextView>(R.id.textoPerfilRol).text = usuario.rol.etiqueta
        findViewById<TextView>(R.id.textoPerfilLegajo).text =
            getString(R.string.perfil_legajo, usuario.legajo)
        findViewById<TextView>(R.id.textoPerfilArea).text = usuario.area

        pintarPermisos()
    }

    private fun pintarPermisos() {
        val contenedor = findViewById<LinearLayout>(R.id.contenedorPermisos)
        val inflador = LayoutInflater.from(this)
        val rol = SesionUsuario.rolUsuario()

        Permiso.values().forEach { permiso ->
            val fila: View = inflador.inflate(R.layout.item_permiso, contenedor, false)
            val habilitado = rol.puede(permiso)

            val texto = fila.findViewById<TextView>(R.id.textoPermiso)
            texto.text = etiquetaDe(permiso)
            texto.setTextColor(
                ContextCompat.getColor(
                    this,
                    if (habilitado) R.color.texto_primario else R.color.texto_secundario
                )
            )

            // La matriz es informativa, no editable: depende del rol asignado.
            val icono = fila.findViewById<ImageView>(R.id.iconoPermiso)
            icono.setImageResource(
                if (habilitado) R.drawable.ic_check else R.drawable.ic_bloqueado
            )
            icono.imageTintList = ContextCompat.getColorStateList(
                this,
                if (habilitado) R.color.estado_normal else R.color.texto_secundario
            )

            contenedor.addView(fila)
        }
    }

    private fun etiquetaDe(permiso: Permiso): String = when (permiso) {
        Permiso.VER_TABLERO -> getString(R.string.permiso_ver_tablero)
        Permiso.REGISTRAR_INCIDENTE -> getString(R.string.permiso_registrar_incidente)
        Permiso.CERRAR_INCIDENTE -> getString(R.string.permiso_cerrar_incidente)
        Permiso.RECONOCER_ALERTA -> getString(R.string.permiso_reconocer_alerta)
        Permiso.ASIGNAR_TAREA -> getString(R.string.permiso_asignar_tarea)
        Permiso.COMPLETAR_TAREA -> getString(R.string.permiso_completar_tarea)
        Permiso.REGISTRAR_VERIFICACION -> getString(R.string.permiso_registrar_verificacion)
        Permiso.DECLARAR_EMERGENCIA -> getString(R.string.permiso_declarar_emergencia)
        Permiso.VER_AUDITORIA -> getString(R.string.permiso_ver_auditoria)
    }
}
