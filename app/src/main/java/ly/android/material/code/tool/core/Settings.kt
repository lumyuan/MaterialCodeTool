package ly.android.material.code.tool.core

import com.google.gson.Gson
import ly.android.io.File
import ly.android.io.common.IOUtils
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.entity.FragmentBean
import ly.android.material.code.tool.data.entity.HomePageBean
import ly.android.material.code.tool.data.entity.HomePageDataBean
import ly.android.material.code.tool.data.entity.ReferenceFileType
import ly.android.material.code.tool.data.entity.State
import ly.android.material.code.tool.data.enums.ReferenceLanguage
import ly.android.material.code.tool.data.enums.UrlType

object Settings {
    data class Setting(
        var iconSavePath: String? = "/storage/emulated/0/Pictures/${MaterialCodeToolApplication.application.getString(R.string.app_name)}/ali icon",
        var isVibrate: Boolean = true,
        var homePageBean: HomePageBean = HomePageBean(
            0, State.POSITION
        ),
        var homePages: ArrayList<HomePageDataBean> = pages
    )

    private val dir = MaterialCodeToolApplication.application.filesDir.absolutePath + "/settings"

    private const val name = "settings.json"
    private val gson = Gson()

    fun loadSetting(): Setting{
        return try {
            val json = String(IOUtils.readBytes("$dir/$name"))
            gson.fromJson(json, Setting::class.java)
        }catch (e: Exception){
            Setting(
                homePages = pages
            )
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

    private val pages = ArrayList<HomePageDataBean>().apply {
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    "http://www.tiecode.cn/",
                    null,
                    null,
                    null,
                    null
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.tieCode)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    "https://developer.android.google.cn/training/basics/firstapp",
                    null,
                    null,
                    null,
                    null
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.android)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    "https://developer.android.google.cn/courses/pathways/compose",
                    null,
                    null,
                    null,
                    null
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.compose)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    "Java.txt",
                    ReferenceLanguage.JAVA,
                    UrlType.ASSETS,
                    ReferenceFileType.MD
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.lang_java)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    "Lua教程.txt",
                    ReferenceLanguage.LUA,
                    UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.lang_lua)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "iyu-helpV5.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv5)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "iyu-helpV6.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv6)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "iyu-helpV3.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv3)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "ijava-helpV3.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv3_java)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "ijs-helpV3.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv3_js)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "ilua-helpV3.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv3_lua)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "igame-helpV1.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iGame)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "iyu-中文编程V5.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv5_cn)
            )
        )
        add(
            HomePageDataBean(
                fragmentBean = FragmentBean(
                    null,
                    title = "igame-中文编程V1.0.txt",
                    lang = ReferenceLanguage.IYU,
                    urlType = UrlType.ASSETS,
                    ReferenceFileType.CODE
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iGame_cn)
            )
        )
    }
}