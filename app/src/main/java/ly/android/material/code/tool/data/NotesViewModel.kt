package ly.android.material.code.tool.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ly.android.material.code.tool.data.entity.NoteBean

class NotesViewModel: ViewModel() {

    private val _list = MutableLiveData<List<NoteBean>>()

    val list: LiveData<List<NoteBean>> = _list

    fun setValues(list: List<NoteBean>){
        this._list.value = list
    }

    val addDialogState = MutableLiveData(false)

}