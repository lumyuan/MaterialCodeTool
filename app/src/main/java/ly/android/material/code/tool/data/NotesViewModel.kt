package ly.android.material.code.tool.data

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Observable
import ly.android.material.code.tool.data.entity.NoteItemBean

class NotesViewModel: ViewModel() {

    private val _list = MutableLiveData<List<NoteItemBean>>()

    val list: LiveData<List<NoteItemBean>> = _list

    fun setValues(list: List<NoteItemBean>){
        this._list.value = list
    }

    val addDialogState = MutableLiveData(false)

    val langMapLiveData = MutableLiveData<LinkedHashMap<String?, ArrayList<NoteItemBean>>>()

    @SuppressLint("CheckResult")
    fun setLangMap(list: List<NoteItemBean>){
        Observable.create<LinkedHashMap<String?, ArrayList<NoteItemBean>>> {
            val langMap = LinkedHashMap<String?, ArrayList<NoteItemBean>>()
            for (index in list.indices) {
                val langValue = langMap[list[index].noteBean.language]
                if (langValue == null){
                    langMap[list[index].noteBean.language] = ArrayList<NoteItemBean>().apply {
                        add(list[index])
                    }
                }else {
                    langValue.add(list[index])
                }
            }
            it.onNext(langMap)
            it.onComplete()
        }.subscribe {
            langMapLiveData.value = it
        }
    }
}