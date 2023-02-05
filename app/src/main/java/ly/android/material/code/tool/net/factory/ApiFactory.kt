package ly.android.material.code.tool.net.factory

import android.text.TextUtils
import com.franmontiel.persistentcookiejar.ClearableCookieJar
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.util.SharedPreferencesUtil
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit


/**
 * 接口请求工厂
 */
object ApiFactory {

    // OkHttpClient客户端
    private val aliIconClient: OkHttpClient by lazy { newClient() }
    private val encodeClient: OkHttpClient by lazy { newClient() }
    /**
     * 创建阿里矢量图标库API Service接口实例
     */
    fun <T> createAliIconService(baseUrl: String, clazz: Class<T>): T = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(aliIconClient)
        .addConverterFactory(AliIconJsonConverterFactory(Gson()))
        .build().create(clazz)

    fun <T> createEncodeService(baseUrl: String, clazz: Class<T>): T = Retrofit.Builder().baseUrl(baseUrl).client(
        encodeClient
    ).addConverterFactory(JsonConverterFactory(Gson()))
        .build().create(clazz)

    /**
     * OkHttpClient客户端
     */
    private fun newClient(): OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)// 连接时间：30s超时
        readTimeout(10, TimeUnit.SECONDS)// 读取时间：10s超时
        writeTimeout(10, TimeUnit.SECONDS)// 写入时间：10s超时
        addInterceptor { chain -> // 设置 Cookie
            val cookieStr: String? = SharedPreferencesUtil.load("cookie")
            if (!TextUtils.isEmpty(cookieStr)) {
                chain.proceed(chain.request().newBuilder().header("Cookie", cookieStr!!).build())
            } else chain.proceed(chain.request())
        }
        addInterceptor(BusinessErrorInterceptor())
    }.build()

    private fun newEncodeClient(): OkHttpClient = OkHttpClient.Builder().apply {
        connectTimeout(30, TimeUnit.SECONDS)// 连接时间：30s超时
        readTimeout(10, TimeUnit.SECONDS)// 读取时间：10s超时
        writeTimeout(10, TimeUnit.SECONDS)// 写入时间：10s超时
        addInterceptor(BusinessErrorInterceptor())
    }.build()

    class AliIconCookiesInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val proceed = chain.proceed(chain.request())
            println(proceed.headers("Set-Cookie"))
            return proceed
        }

    }
}