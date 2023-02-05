package ly.android.material.code.tool.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.w3c.dom.Element

class AliIconSetColorViewModel: ViewModel() {

    val svg = MutableLiveData<String>()
    val element = MutableLiveData<ArrayList<Element>>()

}