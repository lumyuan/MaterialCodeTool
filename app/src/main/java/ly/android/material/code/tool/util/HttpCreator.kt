package ly.android.material.code.tool.util

import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.documentfile.provider.DocumentFile
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.data.entity.BodyParamType
import ly.android.material.code.tool.data.entity.ResponseStateBean
import ly.android.material.code.tool.data.enums.RequestFunction
import okhttp3.*
import okio.BufferedSink
import okio.Okio
import okio.Source
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


class HttpCreator(
    private val viewModel: PostDevViewModel
) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .cookieJar(PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(MaterialCodeToolApplication.application)))
        .build()

    private val requestBuilder = Request.Builder()

    private val handler = Handler(Looper.getMainLooper())

    private fun createHeaders() {
        viewModel.headers.value?.onEach {
            val key = it.key
            val value = it.value
            if (it.isChecked && key != null && value != null) {
                requestBuilder.addHeader(key, value)
            }
        }
    }

    private fun createMethod() {
        val url = viewModel.url.value
        if (url == null) {
            ToastUtils.toast(R.string.please_input_url)
            return
        }
        when (viewModel.requestFunctionState.value!!) {
            RequestFunction.GET -> {
                val params = StringBuilder("?")
                viewModel.params.value?.apply {
                    this.indices.onEach { index ->
                        val it = this[index]
                        val key = it.key
                        val value = it.value
                        if (it.isChecked && key != null && value != null) {
                            params.append("$key=$value")
                        }
                        if (index < this.size - 1) {
                            params.append("&")
                        }
                    }
                }
                requestBuilder.url("$url$params")
                requestBuilder.get()
            }
            RequestFunction.POST -> {
                requestBuilder.url(url)
                requestBuilder.method(viewModel.requestFunctionState.value.toString(), setPart())
            }
            RequestFunction.PUT -> {
                requestBuilder.url(url)
                requestBuilder.method(viewModel.requestFunctionState.value.toString(), setPart())
            }
            RequestFunction.HEAD -> {
                requestBuilder.url(url)
                requestBuilder.method(viewModel.requestFunctionState.value.toString(), null)
            }
            RequestFunction.DELETE -> {
                requestBuilder.url(url)
                requestBuilder.method(viewModel.requestFunctionState.value.toString(), setPart())
            }
            RequestFunction.OPTIONS -> {
                requestBuilder.url(url)
                requestBuilder.method(viewModel.requestFunctionState.value.toString(), setPart())
            }
            RequestFunction.TRACE -> {
                requestBuilder.url(url)
                requestBuilder.method(viewModel.requestFunctionState.value.toString(), setPart())
            }
            RequestFunction.CONNECT -> {
                requestBuilder.url(url)
                requestBuilder.method(viewModel.requestFunctionState.value.toString(), setPart())
            }
        }
    }

    private fun setPart(): RequestBody? {
        return when (viewModel.bodyTypeState.value) {
            0 -> {
                val params = viewModel.params.value
                if (params.isNullOrEmpty()){
                    null
                }else {
                    val builder = FormBody.Builder()
                    params.onEach {
                        val key = it.key
                        val value = it.value
                        if (it.isChecked && key != null && value != null){
                            builder.add(key, value)
                        }
                    }
                    builder.build()
                }
            }
            1 -> {
                val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                val formDataBeans = viewModel.bodyFormData.value
                val params = viewModel.params.value
                if (!formDataBeans.isNullOrEmpty()) {
                    formDataBeans.onEach {
                        val key = it.key
                        val value = it.value
                        val file = it.file
                        when (it.bodyType) {
                            BodyParamType.TEXT -> {
                                if (it.isChecked && key != null && value != null) {
                                    builder.addFormDataPart(key, value)
                                }
                            }
                            BodyParamType.FILE -> {
                                if (file != null && it.isChecked && key != null) {
                                    val uri = Uri.parse(file)
                                    try {
                                        val application = MaterialCodeToolApplication.application
                                        val documentFile =
                                            DocumentFile.fromSingleUri(application, uri)
                                        val inputStream =
                                            application.contentResolver.openInputStream(
                                                uri
                                            )
                                        val requestBody = createRequestBody(
                                            documentFile?.type?.let { type -> MediaType.parse(type) },
                                            inputStream
                                        )
                                        documentFile?.name?.let { name ->
                                            builder.addFormDataPart(key, name, requestBody)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            }
                        }
                    }
                    if (params.isNullOrEmpty()){
                        return builder.build()
                    }
                }
                if (!params.isNullOrEmpty()) {
                    params.onEach {
                        val key = it.key
                        val value = it.value
                        if (it.isChecked && key != null && value != null){
                            builder.addFormDataPart(key, value)
                        }
                    }
                    return builder.build()
                }
                return null
            }
            2 -> {
                val beans = viewModel.bodyFormUrl.value
                val params = viewModel.params.value
                if (!beans.isNullOrEmpty()){
                    val builder = FormBody.Builder()
                    beans.onEach {
                        val key = it.key
                        val value = it.value
                        if (it.isChecked && key != null && value != null) {
                            builder.addEncoded(key, value)
                        }
                    }
                    if (params.isNullOrEmpty()){
                        return builder.build()
                    }
                }
                if (!params.isNullOrEmpty()){
                    val builder = FormBody.Builder()
                    params.onEach {
                        val key = it.key
                        val value = it.value
                        if (it.isChecked && key != null && value != null){
                            builder.addEncoded(key, value)
                        }
                    }
                    return builder.build()
                }
                return null
            }
            3 -> {
                val raw = viewModel.bodyRaw.value
                if (raw != null && !TextUtils.isEmpty(raw)){
                    RequestBody.create(
                        MediaType.parse("application/${viewModel.bodyRawType.value.toString().lowercase()}; charset=utf-8"), raw
                    )
                }else {
                    null
                }
            }
            4 -> {
                val dataBean = viewModel.binaryData.value
                val documentFile = DocumentFile.fromSingleUri(
                    MaterialCodeToolApplication.application,
                    Uri.parse(dataBean?.file)
                )
                if (dataBean?.file != null && documentFile != null){
                    createRequestBody(
                        documentFile.type?.let { MediaType.parse(it) },
                        MaterialCodeToolApplication.application.contentResolver.openInputStream(documentFile.uri)
                    )
                }else {
                    null
                }
            }
            else -> null
        }
    }

    private fun callback(point: Long, onComponent: () -> Unit = {}): Callback {
        return object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                handler.post {
                    viewModel.bodyData.value = e.toString()
                    viewModel.responseState.value = ResponseStateBean(
                        -1, System.currentTimeMillis() - point,  -1
                    )
                    onComponent()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val code = response.code()
                val bytes = response.body()?.bytes()
                handler.post {
                    viewModel.bodyData.value = String(bytes ?: "".toByteArray())
                    viewModel.responseState.value = ResponseStateBean(
                        code, System.currentTimeMillis() - point, bytes?.size ?: -1
                    )
                    onComponent()
                }
            }
        }
    }

    fun enqueue(onComponent: () -> Unit = {}) {
        createHeaders()
        createMethod()
        okHttpClient.newCall(requestBuilder.build()).enqueue(callback(System.currentTimeMillis(), onComponent))
    }

    private fun createRequestBody(mediaType: MediaType?, inputStream: InputStream?): RequestBody {
        return object : RequestBody() {
            override fun contentType(): MediaType? {
                return mediaType
            }

            @Throws(IOException::class)
            override fun contentLength(): Long {
                return inputStream?.available()?.toLong() ?: -1L
            }

            @Throws(IOException::class)
            override fun writeTo(sink: BufferedSink) {
                var source: Source? = null
                try {
                    source = inputStream?.let { Okio.source(it) }
                    source?.let { sink.writeAll(it) }
                } finally {
                    source?.close()
                }
            }
        }
    }
}