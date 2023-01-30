package ly.android.material.code.tool.common

import android.accounts.NetworkErrorException
import androidx.annotation.StringRes
import com.google.gson.JsonSyntaxException
import com.google.gson.stream.MalformedJsonException
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.net.ApiException
import ly.android.material.code.tool.util.ToastUtils
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 异常工具类
 * @author ssq
 */
object ExceptionUtil {

    /**
     * 处理异常，toast提示错误信息
     */
    fun catchException(e: Throwable) {
        e.printStackTrace()
        when (e) {
            is ApiException -> showToast(
                e.message!!
            )
            is HttpException -> {
                catchHttpException(e)
                return
            }
            is SocketTimeoutException -> showToast(
                R.string.common_error_net_time_out
            )
            is UnknownHostException, is NetworkErrorException -> showToast(
                R.string.common_error_net
            )
            is MalformedJsonException, is JsonSyntaxException -> showToast(
                R.string.common_error_server_json
            )
            // 接口异常
            else -> showToast(
                "${MaterialCodeToolApplication.application.getString(
                    R.string.common_error_do_something_fail
                )}：${e::class.java.name}"
            )
        }
    }

    /**
     * 处理网络异常
     */
    private fun catchHttpException(httpException: HttpException) {
        if (httpException.code() in 200 until 300) return// 成功code则不处理
        showToast(
            catchHttpExceptionCode(
                httpException.code()
            ), httpException.code()
        )
    }

    /**
     * toast提示
     */
    private fun showToast(@StringRes errorMsg: Int, errorCode: Int = -1) {
        showToast(
            MaterialCodeToolApplication.application.getString(
                errorMsg
            ), errorCode
        )
    }

    /**
     * toast提示
     */
    private fun showToast(errorMsg: String, errorCode: Int = -1) {
        if (errorCode == -1) {
            ToastUtils.toast(errorMsg)
        } else {
            ToastUtils.toast("$errorCode：$errorMsg")
        }
    }

    /**
     * 处理网络异常
     */
    private fun catchHttpExceptionCode(errorCode: Int): Int = when (errorCode) {
        in 500..600 -> R.string.common_error_server
        in 400 until 500 -> R.string.common_error_request
        else -> R.string.common_error_request
    }
}