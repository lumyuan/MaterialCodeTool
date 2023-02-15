package ly.android.material.code.tool

import android.app.Application
import com.google.android.material.color.DynamicColors
import kotlinx.coroutines.*
import ly.android.io.FileApplication
import ly.android.material.code.tool.activities.crash.MaterialCrashHandler
import ly.android.material.code.tool.core.Settings
import ly.android.material.code.tool.services.MaterialClipBoardService
import ly.android.material.code.tool.util.ServiceUtils


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
        if (!packageName.contains("debug")){
            MaterialCrashHandler.init(this, "${filesDir.absolutePath}/logs/error")
        }
        //配置动态颜色主题
        DynamicColors.applyToActivitiesIfAvailable(this)
        FileApplication.init(this)
        setting = Settings.loadSetting()

//        if (!ServiceUtils.isServiceRunning(application, MaterialClipBoardService::class.java.name)){
//            ServiceUtils.startForegroundService(application, MaterialClipBoardService::class.java)
//        }
    }

}