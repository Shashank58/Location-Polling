package shashank.com.locationpolling

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

class ApiClient {
  private val baseUrl = "http://10.0.2.2:8080"
  private lateinit var httpClient: OkHttpClient.Builder

  public fun getClient(): Retrofit.Builder {
    httpClient = OkHttpClient.Builder()
    val logging = HttpLoggingInterceptor()
    // set your desired log level
    logging.level = HttpLoggingInterceptor.Level.BODY
    // add logging as last interceptor
    httpClient.addInterceptor(logging)

    return Retrofit.Builder().baseUrl(baseUrl).client(httpClient.build())
  }
}