package shashank.com.locationpolling

import com.google.android.gms.maps.model.LatLng

interface LocationPollingContract {

  interface Service {
    fun getContinuousLocationUpdates()

    fun cancelLocationUpdates()

    fun isPollingActive(): Boolean
    
    fun saveLatitudeAndLongitude(latitude: Double, longitude: Double)
  }

  interface LocationCallback {
    fun onLocationReceived(location: LatLng)
  }
}