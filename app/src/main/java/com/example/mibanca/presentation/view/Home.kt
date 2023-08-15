package com.example.mibanca.presentation.view

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mibanca.R
import com.example.mibanca.databinding.ActivityHomeBinding
import com.example.mibanca.domain.core.LocationProvider
import com.example.mibanca.domain.core.UserProvider
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Home @Inject constructor() : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val locationPermissionCode = 1

    @Inject
    lateinit var userProvider: UserProvider

    @Inject
    lateinit var locationProvider: LocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                // Explain the reason for the permission
                // Provide guidance on why the permission is needed
                // Optionally, show a dialog or a snackbar with more information
                showRationaleDialog()
            } else {
                // Request permission
                requestLocationPermissions()
            }
        } else {
            // Permission already granted
            // Perform location-related operations here
            locationProvider.requestLocationUpdates()
        }

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if (userProvider.getUser() == null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()

        locationProvider.requestLocationUpdates()
    }

    fun navigateTo (id: Int) {
        binding.navView.selectedItemId = id
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_close_session -> {
                showConfirmLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showConfirmLogoutDialog () {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Cerrar sesión")
            .setMessage("Confirma que desesas cerrar tu sesión de forma segura.")
            .setPositiveButton("Confirmar", ) { _, _ ->
                userProvider.removeUser()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == locationPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                // Perform location-related operations here
                locationProvider.requestLocationUpdates()
            } else {
                // Permission denied
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    // User denied permission with "Don't ask again" option
                    // Show a dialog explaining how to manually grant the permission
                    showPermissionDeniedDialog()
                } else {
                    // User denied permission, show a dialog with a polite explanation
                    showRationaleDialog()
                }
            }
        }
    }

    private fun showRationaleDialog () {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Necesitamos tu ubicación")
            .setMessage("Requerimos acceso a tu localización para poder crear pagos seguros.")
            .setPositiveButton("De acuerdo", ) { _, _ ->
                requestLocationPermissions()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun requestLocationPermissions () {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
            locationPermissionCode
        )
    }

    private fun showPermissionDeniedDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Permiso Rechazado")
            .setMessage("Por favor proporciona acceso a la localización para poder crear pagos seguros.")
            .setPositiveButton("Abrir configuración") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }
}