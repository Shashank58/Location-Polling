package shashank.com.locationpolling

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

  private var isActive = false

  private lateinit var locationPollingContract: LocationPollingContract.Service
  private lateinit var map: GoogleMap

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_tracing)

    locationPollingContract = LocationService(this, SharedPrefHelper(this))
    requestMapLoad()
    toggle_location.setOnClickListener(this)
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

  private fun requestMapLoad() {
    val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
    mapFragment.getMapAsync(this)
  }

  @SuppressLint("MissingPermission")
  override fun onMapReady(googleMap: GoogleMap) {
    map = googleMap
    map.isMyLocationEnabled = true
    if (locationPollingContract.isPollingActive()) {
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

  private fun updateLocation(title: String, location: LatLng, zoom: Float) {
    map.addMarker(MarkerOptions().position(location).title(title))
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, zoom))
  }
}
