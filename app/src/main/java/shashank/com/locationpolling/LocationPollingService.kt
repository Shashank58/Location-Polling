package shashank.com.locationpolling

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.android.gms.maps.model.LatLng

class LocationPollingService: Service(), LocationPollingContract.LocationCallback {
  private val locationService = LocationService(this)
  private lateinit var sharedPrefHelper: SharedPrefHelper

  override fun onBind(intent: Intent?): IBinder? = null

  override fun onCreate() {
    super.onCreate()
    sharedPrefHelper = SharedPrefHelper(this)
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    locationService.getLocationUpdates()
    return START_NOT_STICKY
  }

  override fun onDestroy() {
    locationService.cancelLocationUpdates()
    super.onDestroy()
  }

  override fun onLocationReceived(location: LatLng) {
    sharedPrefHelper.saveLatitudeAndLongitude(location.latitude, location.longitude)
  }

}