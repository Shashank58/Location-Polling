package shashank.com.locationpolling

import com.google.android.gms.maps.model.LatLng

interface LocationPollingContract {

  interface Service {
    fun getLocationUpdates()

    fun cancelLocationUpdates()
  }

  interface LocationCallback {
    fun onLocationReceived(location: LatLng)
  }
}