package ly.android.material.code.tool.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R

class MainViewModel: ViewModel() {

    private val _pageCurrent = MutableLiveData(0)
    val pageCurrent: LiveData<Int> = _pageCurrent

    fun setCurrent(current: Int){
        _pageCurrent.value = current
    }

    private val _titles = MutableLiveData<Array<String>>().apply {
        value = arrayOf(
            MaterialCodeToolApplication.application.getString(R.string.reference),
            MaterialCodeToolApplication.application.getString(R.string.tools)
        )
    }
    val titles: LiveData<Array<String>> = _titles

    private val _drawerState = MutableLiveData(false)
    val drawerState: LiveData<Boolean> = _drawerState

    fun setDrawerState(state: Boolean){
        _drawerState.value = state
    }
}