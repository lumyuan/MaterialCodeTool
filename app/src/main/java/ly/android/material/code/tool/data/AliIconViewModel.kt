package ly.android.material.code.tool.data

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import ly.android.material.code.tool.common.launch
import ly.android.material.code.tool.data.entity.LoadState
import ly.android.material.code.tool.net.Repository
import ly.android.material.code.tool.net.pojo.SearchAliIconBean
import ly.android.material.code.tool.net.pojo.response.AliIconBean
import ly.android.material.code.tool.util.AliIconHttpUtils
import ly.android.material.code.tool.util.SharedPreferencesUtil
import java.util.LinkedHashMap

class AliIconViewModel : BaseViewModel() {

    val cookie = MutableLiveData<String>()

    val cookieState = MutableLiveData<String>()

    @SuppressLint("CheckResult")
    fun requestCookie() {
        Observable.create<String> {
            try {
                val get = AliIconHttpUtils.get("https://www.iconfont.cn/", null)
//                try {
//                    Thread.sleep(1000)
//                }catch (e: Exception){
//                    e.printStackTrace()
//                }
                it.onNext(get.toString())
            } catch (e: Throwable) {
                e.printStackTrace()
            }
            it.onComplete()
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                cookieState.value = it
            }
    }

    val searchIcon = MutableLiveData<AliIconBean?>()

    val page = MutableLiveData(1)

}