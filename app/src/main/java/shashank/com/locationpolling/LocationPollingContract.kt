package shashank.com.locationpolling

import com.google.android.gms.maps.model.LatLng

interface LocationPollingContract {

  interface Service {
    fun getLocationUpdates()

    fun cancelLocationUpdates()

    fun isPollingActive(): Boolean
  }

  interface LocationCallback {
    fun onLocationReceived(location: LatLng)
  }
}