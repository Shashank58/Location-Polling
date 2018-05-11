package shashank.com.locationpolling

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class ApiClient {

  companion object {
    private const val baseUrl = "http://10.0.2.2:8080"
    private var httpClient: OkHttpClient.Builder? = null
    fun getClient(): Retrofit.Builder {
      if (httpClient == null) {
        httpClient = OkHttpClient.Builder()
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        // add logging as interceptor
        httpClient!!.addInterceptor(logging)
      }

      return Retrofit.Builder().baseUrl(baseUrl).client(httpClient!!.build())
    }
  }
}