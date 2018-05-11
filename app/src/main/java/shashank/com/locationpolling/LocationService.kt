package shashank.com.locationpolling

import android.os.Handler
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LocationService(
    private val locationCallback: LocationPollingContract.LocationCallback,
    private val sharedPrefHelper: SharedPrefHelper
) : LocationPollingContract.Service {

  private var timeHandler: Handler? = null

  init {
    timeHandler = Handler()
  }

  override fun getContinuousLocationUpdates() {
    ApiClient.getClient()
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build().create(ApiRoutes::class.java)
        .getLocation()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          val result = JSONObject(it.string())
          if (result.getString("status") == Constants.API_SUCCESS) {
            val latitude = result.getDouble("latitude")
            val longitude = result.getDouble("longitude")
            locationCallback.onLocationReceived(LatLng(latitude, longitude))
          }
          timeHandler?.postDelayed(run, Constants.UPDATE_FREQUENCY_MILLISECONDS)
        }, { it.printStackTrace() })
  }

  override fun cancelLocationUpdates() {
    timeHandler?.removeCallbacks(run)
    sharedPrefHelper.clearSharedPref()
  }

  override fun isPollingActive(): Boolean = sharedPrefHelper.getLatitude().toInt() != -1 &&
      sharedPrefHelper.getLongitude().toInt() != -1

  override fun saveLatitudeAndLongitude(latitude: Double, longitude: Double) {
    sharedPrefHelper.saveLatitudeAndLongitude(latitude, longitude)
  }

  private var run: Runnable = Runnable { getContinuousLocationUpdates() }
}