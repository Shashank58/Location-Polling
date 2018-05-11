package shashank.com.locationpolling

import okhttp3.ResponseBody
import retrofit2.http.GET
import rx.Observable

interface ApiRoutes {
  @GET("explore")
  fun getLocation(): Observable<ResponseBody>
}