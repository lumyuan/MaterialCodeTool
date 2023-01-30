package ly.android.material.code.tool.data.entity

/**
 * 请求状态
 */
sealed class LoadState(val message: String) {
    /**
     * 加载中
     */
    class Loading(msg: String = ""): LoadState(msg)

    /**
     * 成功
     */
    class Success(msg: String = ""): LoadState(msg)

    /**
     * 失败
     */
    class Fail(msg: String = ""): LoadState(msg)
}