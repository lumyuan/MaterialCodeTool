package ly.android.material.code.tool.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ly.android.material.code.tool.data.entity.NoteItemBean

class NotesViewModel: ViewModel() {

    private val _list = MutableLiveData<List<NoteItemBean>>()

    val list: LiveData<List<NoteItemBean>> = _list

    fun setValues(list: List<NoteItemBean>){
        this._list.value = list
    }

    val addDialogState = MutableLiveData(false)

}