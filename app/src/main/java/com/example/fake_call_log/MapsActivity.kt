package com.example.fake_call_log

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.fake_call_log.databinding.ActivityMapsBinding
import com.example.fake_call_log.singleton.MockApi
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var locationManager : LocationManager
    var latitude:Double=0.0
    var longitude:Double=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val tv = findViewById<TextView>(R.id.locationTxt)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
            && checkSelfPermission( Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
        {
            val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            requestPermissions(permission, 1)
        }
        else
        {
            val gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(gps_loc!=null)
                getLocation(tv,gps_loc!!)
        }
        MockApi.Queue(this)
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
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        // Add a marker in currentLocation and move the camera
        val currentLocation = LatLng(latitude, longitude)
        mMap.addMarker(MarkerOptions().position(currentLocation))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation))

        MockApi.getData(mMap)
        mMap.setOnMapClickListener { googleMap->
            val marker=mMap.addMarker(MarkerOptions().position(googleMap).title("new"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(googleMap))
            if (marker != null) {
                MockApi.Queue(this)
                MockApi.postData(marker.position.latitude.toString(),marker.position.longitude.toString())
            }
        }
        mMap.setOnMarkerClickListener { marker ->
            if (marker.isInfoWindowShown) {
                marker.hideInfoWindow()
            } else {
                marker.showInfoWindow()
            }

            val builder = AlertDialog.Builder(this)
            //set title for alert dialog
            builder.setTitle("Pin ${marker.id}")
            //set message for alert dialog
            builder.setMessage("Do you want to delete pin with \n latitude: $latitude and longitude: $longitude")
            builder.setIcon(android.R.drawable.ic_dialog_alert)

            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->
                Toast.makeText(applicationContext,marker.id.substring(1,2), Toast.LENGTH_LONG).show()
                Log.d("aloooo",marker.id.substring(1,2))
                //delete(2)
                val m=marker.id
                MockApi.delete(marker.id.substring(1,2).toInt())
                marker.remove()
            }
            //performing negative action
            builder.setNegativeButton("No"){dialogInterface, which ->
                Toast.makeText(applicationContext,"clicked No", Toast.LENGTH_LONG).show()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
            true
        }

    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    )
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    val tv = findViewById<TextView>(R.id.locationTxt)
                    val gps_loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    getLocation(tv,gps_loc!!)
                }
                else
                {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getLocation(tv: TextView, gps_loc: Location)
    {
        val final_loc = gps_loc
        latitude = final_loc.latitude
        longitude = final_loc.longitude
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
        if (addresses != null && addresses.isNotEmpty())
        {
            val userCountry = addresses[0].countryName
            val userAddress = addresses[0].getAddressLine(0)
            tv.text = "country: $userCountry,\n\nAddress: $userAddress,\n\nLatitude: $latitude, \n\nLongitude: $longitude "
        }
        else{
            Log.d("loc","Error")
        }
    }
}