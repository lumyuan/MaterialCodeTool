package ly.android.material.code.tool.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PermissionCheckViewModel : ViewModel() {

    private val esp = MutableLiveData(false)
    private val msp = MutableLiveData(false)

    val externalStoragePermission: LiveData<Boolean> = esp
    val manageStoragePermission: LiveData<Boolean> = msp

    fun setExternalStoragePermissionState(boolean: Boolean){
        esp.value = boolean
    }

    fun setManageStoragePermissionState(boolean: Boolean){
        msp.value = boolean
    }

}