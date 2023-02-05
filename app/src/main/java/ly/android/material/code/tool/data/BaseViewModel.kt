package ly.android.material.code.tool.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ly.android.material.code.tool.data.entity.LoadState

abstract class BaseViewModel : ViewModel() {
    // 加载状态
    open val loadState = MutableLiveData<LoadState>()
}