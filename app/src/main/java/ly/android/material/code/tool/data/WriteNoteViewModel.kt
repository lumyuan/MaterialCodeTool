package ly.android.material.code.tool.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class WriteNoteViewModel: ViewModel() {

    val title = MutableLiveData<String>()
    val content = MutableLiveData<String>()

}