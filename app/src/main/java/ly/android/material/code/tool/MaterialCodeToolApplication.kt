package ly.android.material.code.tool

import android.app.Application
import com.google.android.material.color.DynamicColors
import ly.android.io.FileApplication
import ly.android.material.code.tool.core.Settings


class MaterialCodeToolApplication : Application() {

    companion object {
        lateinit var application: Application
        const val isEncode = true
        var highPerformanceMode = true
        var setting: Settings.Setting? = null
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        //配置动态颜色主题
        DynamicColors.applyToActivitiesIfAvailable(this)
        FileApplication.init(this)
        setting = Settings.loadSetting()
    }

}