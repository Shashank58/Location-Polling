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

  private val locationPollingContract = LocationService(this)
  private var sharedPrefHelper: SharedPrefHelper? = null

  private lateinit var map: GoogleMap

  private var isActive = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_tracing)

    sharedPrefHelper = SharedPrefHelper(this)
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
          isActive = false
          locationPollingContract.cancelLocationUpdates()
          sharedPrefHelper!!.clearSharedPref()
        } else {
          isActive = true
          locationPollingContract.getLocationUpdates()
        }
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
    if (sharedPrefHelper!!.getLatitude().toInt() != -1 && sharedPrefHelper!!.getLongitude().toInt() != -1) {
      stopService(Intent(this, LocationPollingService::class.java))
      sharedPrefHelper!!.clearSharedPref()

      isActive = true
      locationPollingContract.getLocationUpdates()
    } else {
      val bangalore = LatLng(12.9716, 77.5946)
      map.addMarker(MarkerOptions().position(bangalore).title("Bangalore"))
      map.animateCamera(CameraUpdateFactory.newLatLngZoom(bangalore, 12f))
    }
  }

  override fun onLocationReceived(location: LatLng) {
    map.addMarker(MarkerOptions().position(location).title("Location"))
    map.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 14.4f))
  }
}
