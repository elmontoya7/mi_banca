package com.example.mibanca.domain.core

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationProvider @Inject constructor (@ApplicationContext context: Context) {

    private var locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var _latitude: Double? = null
    val latitude: Double
        get () = _latitude ?: 0.0

    private var _longitude: Double? = null
    val longitude: Double
        get () = _longitude ?: 0.0

    private val locationPermissionCode = 1

    private val _context = context

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Handle updated location here
            _latitude = location.latitude
            _longitude = location.longitude
            // You can use the latitude and longitude
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    fun requestLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                _context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                _context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0F,
            locationListener
        )
    }

    fun removeLocationUpdates() {
        locationManager.removeUpdates(locationListener)
    }
}
