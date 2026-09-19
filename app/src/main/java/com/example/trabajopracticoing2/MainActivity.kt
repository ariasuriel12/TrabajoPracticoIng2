package com.example.trabajopracticoing2

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.trabajopracticoing2.data.RepositorioSeguridad
import com.example.trabajopracticoing2.data.SesionUsuario
import com.example.trabajopracticoing2.ui.alertas.AlertasFragment
import com.example.trabajopracticoing2.ui.emergencia.EmergenciaActivity
import com.example.trabajopracticoing2.ui.incidentes.IncidentesFragment
import com.example.trabajopracticoing2.ui.login.LoginActivity
import com.example.trabajopracticoing2.ui.mas.MasFragment
import com.example.trabajopracticoing2.ui.perfil.PerfilActivity
import com.example.trabajopracticoing2.ui.tablero.TableroFragment
import com.example.trabajopracticoing2.ui.tareas.TareasFragment
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

/**
 * Contenedor principal de la aplicacion.
 *
 * Implementa la navegacion primaria del prototipo mediante una barra inferior
 * de cinco destinos y mantiene siempre visible el acceso al modo emergencia,
 * que es la situacion critica identificada en la consigna.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var barraSuperior: MaterialToolbar
    private lateinit var navegacionInferior: BottomNavigationView

    /** Destino actualmente visible; se conserva ante rotacion. */
    private var destinoActual = R.id.nav_tablero

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Si no hay sesion activa el usuario vuelve al login: ninguna operacion
        // del sistema puede quedar sin usuario identificado (requisito de auditoria).
        if (SesionUsuario.usuarioActual == null) {
            irAlLogin()
            return
        }

        RepositorioSeguridad.inicializarSiHaceFalta()
        aplicarInsets()
        enlazarVistas()
        configurarBarraSuperior()
        configurarNavegacionInferior()
        configurarBotonEmergencia()

        destinoActual = savedInstanceState?.getInt(CLAVE_DESTINO) ?: R.id.nav_tablero
        navegacionInferior.selectedItemId = destinoActual
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(CLAVE_DESTINO, destinoActual)
    }

    private fun aplicarInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { vista, insets ->
            val barras = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            vista.setPadding(barras.left, barras.top, barras.right, barras.bottom)
            insets
        }
    }

    private fun enlazarVistas() {
        barraSuperior = findViewById(R.id.barraSuperior)
        navegacionInferior = findViewById(R.id.navegacionInferior)
    }

    private fun configurarBarraSuperior() {
        barraSuperior.inflateMenu(R.menu.menu_principal)
        barraSuperior.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.accion_perfil) {
                startActivity(Intent(this, PerfilActivity::class.java))
                true
            } else {
                false
            }
        }
    }

    private fun configurarNavegacionInferior() {
        navegacionInferior.setOnItemSelectedListener { item ->
            mostrarDestino(item.itemId)
            true
        }
    }

    /** El acceso a emergencia esta siempre a un toque de distancia. */
    private fun configurarBotonEmergencia() {
        findViewById<ExtendedFloatingActionButton>(R.id.botonEmergencia).setOnClickListener {
            startActivity(Intent(this, EmergenciaActivity::class.java))
        }
    }

    private fun mostrarDestino(id: Int) {
        destinoActual = id
        val fragmento: Fragment = when (id) {
            R.id.nav_alertas -> AlertasFragment()
            R.id.nav_incidentes -> IncidentesFragment()
            R.id.nav_tareas -> TareasFragment()
            R.id.nav_mas -> MasFragment()
            else -> TableroFragment()
        }
        barraSuperior.title = getString(tituloDe(id))
        barraSuperior.subtitle = SesionUsuario.nombreUsuario()
        supportFragmentManager.beginTransaction()
            .replace(R.id.contenedorFragmentos, fragmento)
            .commit()
    }

    private fun tituloDe(id: Int): Int = when (id) {
        R.id.nav_alertas -> R.string.alertas_titulo
        R.id.nav_incidentes -> R.string.incidentes_titulo
        R.id.nav_tareas -> R.string.tareas_titulo
        R.id.nav_mas -> R.string.mas_titulo
        else -> R.string.app_name
    }

    /** Permite que "Más > Cerrar sesión" vuelva al login limpiando la pila. */
    fun irAlLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    /** Permite que el tablero cambie de pestaña (accesos rápidos). */
    fun seleccionarDestino(id: Int) {
        navegacionInferior.selectedItemId = id
    }

    private companion object {
        const val CLAVE_DESTINO = "destino_actual"
    }
}
