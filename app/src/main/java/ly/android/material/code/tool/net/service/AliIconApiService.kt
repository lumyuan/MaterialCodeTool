package ly.android.material.code.tool.net.service

import ly.android.material.code.tool.net.pojo.SearchAliIconBean
import retrofit2.http.GET
import retrofit2.http.POST

@JvmSuppressWildcards
interface AliIconApiService {

    @GET("/")
    suspend fun getCookie(): String

    @POST("/api/icon/search.json")
//    @Headers("User-Agent:Mozilla/5.0 (iPad; U; CPU OS 6_0 like Mac OS X; zh-CN; iPad2)||accept-language=zh-CN")
    suspend fun searchIcon(searchAliIconBean: SearchAliIconBean): Map<String, String>

}