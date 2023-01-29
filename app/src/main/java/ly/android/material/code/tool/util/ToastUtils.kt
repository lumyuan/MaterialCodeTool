package ly.android.material.code.tool.util

import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import ly.android.material.code.tool.MaterialCodeToolApplication

object ToastUtils {

    fun toast(charSequence: CharSequence){
        toast(charSequence, Toast.LENGTH_SHORT)
    }

    fun toast(@StringRes id: Int){
        toast(id, Toast.LENGTH_SHORT)
    }

    fun toast(charSequence: CharSequence, time: Int){
        Toast.makeText(MaterialCodeToolApplication.application, charSequence, time).show()
    }

    fun toast(@StringRes id: Int, time: Int){
        Toast.makeText(MaterialCodeToolApplication.application, id, time).show()
    }

}