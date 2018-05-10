package shashank.com.locationpolling

import android.content.Context
import android.content.SharedPreferences

class SharedPrefHelper(context: Context) {
  private val sharedPreferences: SharedPreferences = context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)

  fun saveLatitudeAndLongitude(latitude: Double, longitude: Double) {
    val editor = sharedPreferences.edit()
    editor.putFloat(Constants.LATITUDE, latitude.toFloat())
    editor.putFloat(Constants.LONGITUDE, longitude.toFloat())
    editor.apply()
  }

  fun getLatitude() = sharedPreferences.getFloat(Constants.LATITUDE, -1f).toDouble()

  fun getLongitude() = sharedPreferences.getFloat(Constants.LONGITUDE, -1f).toDouble()

  fun clearSharedPref() {
    val editor = sharedPreferences.edit()
    editor.clear()
    editor.apply()
  }
}