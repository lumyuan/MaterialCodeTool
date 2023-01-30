package ly.android.material.code.tool.common

import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ly.android.material.code.tool.data.entity.LoadState

/**
 * ViewModel扩展方法：启动协程
 * @param block 协程逻辑
 * @param onError 错误回调方法
 * @param onComplete 完成回调方法
 */
fun ViewModel.launch (block: suspend CoroutineScope.() -> Unit,
                      onError: (e: Throwable) -> Unit = {},
                      onComplete: () -> Unit = {}){
    viewModelScope.launch(
        CoroutineExceptionHandler { _, throwable ->
            //MainLooper
            run{
                //处理异常
                ExceptionUtil.catchException(throwable)
                onError(throwable)
            }
        }
    ){
        try {
            Looper.getMainLooper().let {
                block.invoke(this)
            }
        } finally {
            onComplete()
        }
    }
}

/**
 * ViewModel扩展
 */
val ViewModel.loadState: MutableLiveData<LoadState>
    get() = MutableLiveData()