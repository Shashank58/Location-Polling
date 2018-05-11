package shashank.com.locationpolling

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_tracing.*
import kotlin.concurrent.thread

class TracingActivity : AppCompatActivity(), OnMapReadyCallback, View.OnClickListener, LocationPollingContract.LocationCallback {

  private val FINE_LOCATION_PERMISSION = 1

  private var isActive = false

  private lateinit var locationPollingContract: LocationPollingContract.Service
  private lateinit var map: GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_tracing)

    locationPollingContract = LocationService(this, SharedPrefHelper(this))
    if (savedInstanceState != null) {
      isActive = savedInstanceState.getBoolean(Constants.IS_ACTIVE)
    }
    if (isLocationPermissionGranted()) {
      requestMapLoad()
    } else {
      requestPermission()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    if (isActive) {
      locationPollingContract.cancelLocationUpdates()
      thread {
        startService(Intent(this, LocationPollingService::class.java))
      }
    }
  }

  override fun onClick(v: View?) {
    when (v?.id) {
      R.id.toggle_location -> {
        if (isActive) {
          toggle_location.setImageResource(R.drawable.ic_play_arrow)
          locationPollingContract.cancelLocationUpdates()
        } else {
          toggle_location.setImageResource(R.drawable.ic_stop)
          locationPollingContract.getContinuousLocationUpdates()
        }
        isActive = !isActive
      }
    }
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      FINE_LOCATION_PERMISSION -> {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
          requestMapLoad()
        } else {
          AlertDialog.Builder(this)
              .setTitle("Error")
              .setMessage("Please enable location access to use this app")
              .setPositiveButton(android.R.string.ok) { _, _ -> requestPermission() }
              .create().show()
        }
      }
    }
  }

  @SuppressLint("MissingPermission")
  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    map.isMyLocationEnabled = true
    if (isActive || locationPollingContract.isPollingActive()) {
      stopService(Intent(this, LocationPollingService::class.java))
      locationPollingContract.cancelLocationUpdates()

      toggle_location.setImageResource(R.drawable.ic_stop)
      isActive = true
      // Move over to normal polling instead of service
      locationPollingContract.getContinuousLocationUpdates()
    } else {
      toggle_location.setImageResource(R.drawable.ic_play_arrow)
      val bangalore = LatLng(12.9716, 77.5946)
      updateLocation("Bangalore", bangalore, Constants.DEFAULT_LOCATION_ZOOM)
    }
  }

  override fun onLocationReceived(location: LatLng) {
    updateLocation("Location", location, Constants.LOCATION_ZOOM)
  }

  override fun onSaveInstanceState(outState: Bundle) {
    outState.putBoolean(Constants.IS_ACTIVE, isActive)
    super.onSaveInstanceState(outState)
  }

  private fun updateLocation(title: String, location: LatLng, zoom: Float) {
    map.addMarker(MarkerOptions().position(location).title(title))
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
  }

  private fun requestPermission() {
    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), FINE_LOCATION_PERMISSION)
  }

  private fun requestMapLoad() {
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
    toggle_location.setOnClickListener(this)
  }

  private fun isLocationPermissionGranted() = (ContextCompat.checkSelfPermission(this, Manifest
      .permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
}
