package ly.android.material.code.tool.data

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.promeg.pinyinhelper.Pinyin
import io.reactivex.rxjava3.core.Observable
import ly.android.material.code.tool.data.entity.ReferenceBean
import ly.android.material.code.tool.data.entity.ReferenceFileType

class ReferenceViewModel: ViewModel() {

    private val _items = MutableLiveData<ArrayList<ReferenceBean>>()
    private val _date = MutableLiveData<ArrayList<ReferenceBean>>()

    val items: LiveData<ArrayList<ReferenceBean>> = _items

    @SuppressLint("CheckResult")
    fun updateAssets(referenceFileType: ReferenceFileType, data: String){
        Observable.create {
            val array = data.split("【")
            val list = ArrayList<ReferenceBean>()
            for (index in 1 until array.size) {
                val item = array[index]
                val itemList = item.split("】")
                val title = itemList[0]
                val content = itemList[1]
                list.add(
                    ReferenceBean(
                        title,
                        content,
                        array[0],
                        referenceFileType = referenceFileType
                    )
                )
            }
            it.onNext(list)
            it.onComplete()
        }.subscribe {
            _date.value = it
            _items.value = it
        }
    }

    @SuppressLint("CheckResult")
    fun searchAssets(regex: String){
        Observable.create {
            val list = ArrayList<ReferenceBean>()
            _date.value?.forEach {bean ->
                if (find("${bean.title}${bean.content}", regex)){
                    list.add(bean)
                }
            }
            it.onNext(list)
            it.onComplete()
        }.subscribe {
            _items.value = it
        }
    }

    private fun toPinyin(content: String): String {
        return Pinyin.toPinyin(content, "")
    }

    private fun find(content: String, regex: String): Boolean {
        return toPinyin(content).lowercase().contains(toPinyin(regex).lowercase().trim())
    }
}