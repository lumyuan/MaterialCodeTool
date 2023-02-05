package ly.android.material.code.tool.core

import com.google.gson.Gson
import ly.android.io.File
import ly.android.io.common.IOUtils
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R

object Settings {
    data class Setting(
        var iconSavePath: String? = "/storage/emulated/0/Pictures/${MaterialCodeToolApplication.application.getString(R.string.app_name)}/ali icon"
    )

    private val dir = MaterialCodeToolApplication.application.filesDir.absolutePath + "/settings"

    private const val name = "settings.json"
    private val gson = Gson()

    fun loadSetting(): Setting{
        return try {
            val json = String(IOUtils.readBytes("$dir/$name"))
            gson.fromJson(json, Setting::class.java)
        }catch (e: Exception){
            Setting()
        }
    }

    fun saveSetting(setting: Setting): Boolean{
        return try {
            val file = File(dir)
            if (!file.exists()){
                file.mkdirs()
            }
            IOUtils.writeBytes("$dir/$name", gson.toJson(setting).toByteArray())
            return true
        }catch (e: Exception){
            false
        }
    }
}