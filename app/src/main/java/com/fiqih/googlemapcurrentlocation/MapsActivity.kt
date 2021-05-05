package com.fiqih.googlemapcurrentlocation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private var fromLocationClient: FusedLocationProviderClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        getCurrentLocation()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("RestrictedApi")
    fun getCurrentLocation(){
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment

        val fusedLocationProviderClient = LocationServices
                .getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest()
                .setInterval(3000)
                .setFastestInterval(3000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                object : LocationCallback(){
                    override fun onLocationResult(p0: LocationResult) {
                        super.onLocationResult(p0)
                        for (location in p0.locations){
                            mapFragment.getMapAsync{
                                mMap = it
                                if (ActivityCompat.checkSelfPermission(
                                                this@MapsActivity,
                                                Manifest.permission.ACCESS_FINE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                                                this@MapsActivity,
                                                Manifest.permission.ACCESS_COARSE_LOCATION
                                        ) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                                }

                                mMap.isMyLocationEnabled = true
                                mMap.uiSettings.isZoomControlsEnabled
                                val locationResult = LocationServices.getFusedLocationProviderClient(this@MapsActivity).lastLocation
                                locationResult.addOnCompleteListener(this@MapsActivity) {
                                    if(it.isSuccessful && it.result != null){
                                        var currentLocation = it.result
                                        var currentLatitude = currentLocation.latitude
                                        var currentLongitude = currentLocation.longitude

                                        var myLocation = LatLng(currentLatitude, currentLongitude)
                                        mMap.addMarker(MarkerOptions().position(myLocation).title("Posisi")).showInfoWindow()
                                        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
                                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,15f))
                                    }
                                }
                            }
                        }
                    }
                },
                Looper.myLooper()
        )


    }

}