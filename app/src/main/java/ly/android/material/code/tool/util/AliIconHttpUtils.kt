package ly.android.material.code.tool.util

import android.text.TextUtils
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.google.gson.Gson
import com.google.gson.JsonParser
import ly.android.material.code.tool.MaterialCodeToolApplication
import okhttp3.*
import java.io.IOException


object AliIconHttpUtils {

    private val httpClient = OkHttpClient.Builder().addInterceptor { chain -> // 获取 Cookie
        val resp: Response = chain.proceed(chain.request())
        val cookies: List<String> = resp.headers("Set-Cookie")
        val cookieStr = StringBuilder()
        if (cookies.isNotEmpty()) {
            for (i in cookies.indices) {
                cookieStr.append(cookies[i])
            }
            val value = cookieStr.toString()
            if (value.contains(";")) {
                SharedPreferencesUtil.save("cookie", value.substring("ctoken=".length, value.indexOf(";")))
            }
        }
        resp
    }.cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(MaterialCodeToolApplication.application)))
        .build()

    private var jsonParser: JsonParser? = null

    private val gson = Gson()

    fun toParams(params: LinkedHashMap<String, String>): String {
        val stringBuilder = StringBuilder()
        params.forEach { (t, u) ->
            stringBuilder.append("&")
                .append(t)
                .append("=")
                .append(u)
        }
        return "?${
            if (stringBuilder.toString().indexOf("&") == 0) {
                stringBuilder.toString().substring(1)
            } else {
                stringBuilder.toString()
            }
        }"
    }


    @Throws(Exception::class)
    fun get(url: String, params: LinkedHashMap<String, String>?): String? {
        val request =
            Request.Builder().apply {
                url(url + if (params != null) toParams(params) else "")
                addHeader("User-Agent", "Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||accept-language=zh-CN")
            }.build()
        return httpClient.newCall(request).execute().body()?.string()
    }

    @Throws(Exception::class)
    fun post(url: String, params: LinkedHashMap<String, String>?): String?{
        val builder = FormBody.Builder().apply {
            params?.forEach { (t, u) ->
                add(t, u)
            }
        }.build()
        val request = Request.Builder().post(builder).url(url).build()
        val execute = httpClient.newCall(request).execute()
        return execute.body()?.string()
    }

    fun syncPostJson(url: String, json: String): String? {
        val requestBody =
            RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json)
        val request = Request.Builder().url(url).post(requestBody).build()
        return try {
            val string: String = httpClient.newCall(request).execute().body()!!.string()
            string
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}