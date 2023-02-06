package ly.android.material.code.tool.data

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ColorPickerViewModel: ViewModel() {
    val actionState = MutableLiveData(0)
    val picUri = MutableLiveData<Uri>()
}