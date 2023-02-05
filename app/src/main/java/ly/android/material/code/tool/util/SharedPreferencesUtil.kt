package ly.android.material.code.tool.util

import android.content.Context
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R

object SharedPreferencesUtil {

    fun save(key: String?, value: String?): Boolean{
        val sharedPreferences =
            MaterialCodeToolApplication.application.getSharedPreferences(
                MaterialCodeToolApplication.application.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
        return sharedPreferences.edit().putString(key, value).commit()
    }

    fun load(key: String?): String?{
        val sharedPreferences =
            MaterialCodeToolApplication.application.getSharedPreferences(
                MaterialCodeToolApplication.application.getString(R.string.app_name),
                Context.MODE_PRIVATE
            )
        return sharedPreferences.getString(key, null)
    }

}