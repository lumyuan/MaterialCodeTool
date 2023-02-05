package ly.android.material.code.tool.net.factory

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.util.AesUtil
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type

class AliIconJsonConverterFactory(private val gson: Gson): Converter.Factory() {

    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String> {
        return EncryptionSingleConverter<Any?>(type)
    }

    class EncryptionSingleConverter<T>(val type: Type) : Converter<T, String> {
        override fun convert(value: T): String{
            val string = `value`.toString()
            return string
        }
    }
}

/**
 * 自定义请求RequestBody
 */
open class AliIconJsonRequestBodyConverter<T>(
    private val gson: Gson
) : Converter<T, RequestBody> {

    @Throws(IOException::class)
    override fun convert(value: T): RequestBody {
        val postBody = gson.toJson(value)
        return RequestBody.create(
            MediaType.parse("application/json; charset=UTF-8"),
            postBody
        )
    }
}

/**
 * 自定义响应ResponseBody
 */
class AliIconJsonResponseBodyConverter<T>(
    private val gson: Gson,
    private val adapter: TypeAdapter<T>
) : Converter<ResponseBody, T> {
    @Throws(IOException::class)
    override fun convert(responseBody: ResponseBody): T {
        return adapter.fromJson(responseBody.string())
    }
}
