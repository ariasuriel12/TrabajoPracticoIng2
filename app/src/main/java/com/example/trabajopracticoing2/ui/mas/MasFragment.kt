package com.example.trabajopracticoing2.ui.mas

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.trabajopracticoing2.MainActivity
import com.example.trabajopracticoing2.R
import com.example.trabajopracticoing2.data.RepositorioAuditoria
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.model.TipoEvento
import com.example.trabajopracticoing2.ui.auditoria.AuditoriaActivity
import com.example.trabajopracticoing2.ui.perfil.PerfilActivity
import com.example.trabajopracticoing2.ui.procedimientos.ProcedimientosActivity
import com.example.trabajopracticoing2.ui.sectores.SectoresActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView

/**
 * Seccion "Más": agrupa las pantallas secundarias que no forman parte de la
 * operacion diaria pero si del seguimiento y el control.
 */
class MasFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        contenedor: ViewGroup?,
        estado: Bundle?
    ): View = inflater.inflate(R.layout.fragment_mas, contenedor, false)

    override fun onViewCreated(vista: View, estado: Bundle?) {
        super.onViewCreated(vista, estado)

        vista.findViewById<TextView>(R.id.textoMasNombre).text = SesionUsuario.nombreUsuario()
        vista.findViewById<TextView>(R.id.textoMasRol).text = SesionUsuario.rolUsuario().etiqueta
        vista.findViewById<TextView>(R.id.textoVersion).text = getString(R.string.version_app)

        vista.findViewById<MaterialCardView>(R.id.opcionProcedimientos).setOnClickListener {
            startActivity(Intent(requireContext(), ProcedimientosActivity::class.java))
        }
        vista.findViewById<MaterialCardView>(R.id.opcionSectores).setOnClickListener {
            startActivity(Intent(requireContext(), SectoresActivity::class.java))
        }
        vista.findViewById<MaterialCardView>(R.id.opcionAuditoria).setOnClickListener {
            startActivity(Intent(requireContext(), AuditoriaActivity::class.java))
        }
        vista.findViewById<MaterialCardView>(R.id.opcionPerfil).setOnClickListener {
            startActivity(Intent(requireContext(), PerfilActivity::class.java))
        }

        vista.findViewById<MaterialButton>(R.id.botonCerrarSesion).setOnClickListener {
            RepositorioAuditoria.registrar(
                TipoEvento.ACCESO,
                "SESION",
                "Cierre de sesión de ${SesionUsuario.nombreUsuario()}"
            )
            SesionUsuario.cerrarSesion()
            (activity as? MainActivity)?.irAlLogin()
        }
    }
}
