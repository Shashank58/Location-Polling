package shashank.com.locationpolling

import android.os.Handler
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

class LocationService(private val locationCallback: LocationPollingContract.LocationCallback): LocationPollingContract.Service {
  private var timeHandler: Handler? = null

  init {
    timeHandler = Handler()
  }

  override fun getLocationUpdates() {
    ApiClient().getClient()
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
        }, {it.printStackTrace()})
  }

  override fun cancelLocationUpdates() {
    timeHandler?.removeCallbacks(run)
  }

  private var run: Runnable = Runnable { getLocationUpdates() }
}