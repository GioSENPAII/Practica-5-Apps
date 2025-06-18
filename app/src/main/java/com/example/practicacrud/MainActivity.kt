package com.example.practicacrud

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.practicacrud.utils.AuthManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navigationView: NavigationView
    private lateinit var authManager: AuthManager

    // Constante para ID de menú de logout
    private val MENU_LOGOUT_ID = 9999

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar AuthManager
        authManager = AuthManager(this)

        // Configurar el DrawerLayout
        drawerLayout = findViewById(R.id.drawerLayout)
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(actionBarToggle)
        actionBarToggle.syncState()

        // Habilitar el botón para mostrar el menú
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Configurar el menú de navegación
        navigationView = findViewById(R.id.navigationView)
        setupNavigationMenu()
    }

    private fun setupNavigationMenu() {
        // Actualizar menú según el estado de autenticación
        val menu = navigationView.menu
        val isLoggedIn = authManager.isLoggedIn()
        val isAdmin = authManager.isAdmin()

        // Limpiar las opciones anteriores para evitar duplicados
        menu.clear()

        // Para usuarios no autenticados
        if (!isLoggedIn) {
            menu.add(0, R.id.nav_login, Menu.NONE, "Iniciar Sesión 😎")
            menu.add(0, R.id.nav_register, Menu.NONE, "Registrarse 🫶🏻")
        } else {
            // Para usuarios autenticados - AGREGAR ESTAS NUEVAS OPCIONES
            menu.add(0, R.id.nav_search, Menu.NONE, "Buscar 🔍")
            menu.add(0, R.id.nav_favorites, Menu.NONE, "Favoritos ⭐")
            menu.add(0, R.id.nav_history, Menu.NONE, "Historial 📋")
            menu.add(0, R.id.nav_recommendations, Menu.NONE, "Recomendaciones 💡")

            // Para administradores
            if (isAdmin) {
                menu.add(0, R.id.nav_crud, Menu.NONE, "Operaciones CRUD 🤖")
            } else {
                // Para usuarios normales
                menu.add(0, R.id.nav_profile, Menu.NONE, "Perfil 👤")
            }

            // Opción de logout para todos los usuarios autenticados
            menu.add(0, MENU_LOGOUT_ID, Menu.NONE, "Cerrar Sesión 🔒")
        }

        // Mostrar mensaje de bienvenida con rol
        val headerView = navigationView.getHeaderView(0)
        val tvUsername = headerView.findViewById<TextView>(R.id.tvUsername)
        val tvRole = headerView.findViewById<TextView>(R.id.tvRole)

        // Actualizar según usuario logueado
        if (isLoggedIn) {
            val username = authManager.getUsername() ?: "Usuario"
            val role = if (isAdmin) "Administrador" else "Usuario"
            tvUsername.text = username
            tvRole.text = role
        } else {
            tvUsername.text = "Invitado"
            tvRole.text = "Sin sesión"
        }

        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_login -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                R.id.nav_register -> {
                    startActivity(Intent(this, RegisterActivity::class.java))
                    true
                }
                R.id.nav_search -> {
                    startActivity(Intent(this, SearchActivity::class.java))
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                R.id.nav_history -> {
                    startActivity(Intent(this, HistoryActivity::class.java))
                    true
                }
                R.id.nav_recommendations -> {
                    startActivity(Intent(this, RecommendationsActivity::class.java))
                    true
                }
                R.id.nav_crud -> {
                    if (isAdmin) {
                        startActivity(Intent(this, CrudActivity::class.java))
                    } else {
                        Toast.makeText(this, "Acceso denegado", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                MENU_LOGOUT_ID -> {
                    authManager.clearAll()
                    startActivity(Intent(this, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK))
                    true
                }
                else -> false
            }
        }

        // Cerrar el drawer después de seleccionar una opción
        drawerLayout.closeDrawers()
    }

    override fun onResume() {
        super.onResume()
        // Actualizar el menú de navegación para reflejar el estado de autenticación actual
        setupNavigationMenu()
    }

    override fun onSupportNavigateUp(): Boolean {
        drawerLayout.open()
        return true
    }
}