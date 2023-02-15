package ly.android.material.code.tool.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ly.android.material.code.tool.data.entity.FormDataBean
import ly.android.material.code.tool.data.entity.ParamType
import ly.android.material.code.tool.data.entity.RequestParamBean
import ly.android.material.code.tool.data.entity.ResponseStateBean
import ly.android.material.code.tool.data.enums.BodyRawType
import ly.android.material.code.tool.data.enums.RequestFunction

class PostDevViewModel: ViewModel() {

    val sendState = MutableLiveData(System.currentTimeMillis())

    /*--------------Req Fun-----------------*/
    val requestFunctionState = MutableLiveData(RequestFunction.GET)
    val url = MutableLiveData<String>()

    /*-----------Params Model------------*/
    val requestViewNavigationState = MutableLiveData(0)
    val params = MutableLiveData<ArrayList<RequestParamBean>>()

    /*-----------Header Model------------*/
    val headers = MutableLiveData(
        ArrayList<RequestParamBean>().apply {
            add(
                RequestParamBean(
                    isChecked = true,
                    type = ParamType.PARAM,
                    key = "User-Agent",
                    value = "android"
                )
            )
            add(
                RequestParamBean(
                    isChecked = false,
                    type = ParamType.ADD
                )
            )
        }
    )

    /*-----------Body Model------------*/
    val bodyTypeState = MutableLiveData(0)
    val bodyFormData = MutableLiveData<ArrayList<FormDataBean>>()
    val bodyFormUrl = MutableLiveData<ArrayList<RequestParamBean>>()

    val bodyRawType = MutableLiveData(BodyRawType.Text)
    val bodyRaw = MutableLiveData<String>()

    val binaryData = MutableLiveData<FormDataBean>()

    /*------------------Body Result---------------------*/
    val pageState = MutableLiveData(0)
    val bodyData = MutableLiveData<String>()
    val responseState = MutableLiveData<ResponseStateBean>()
}