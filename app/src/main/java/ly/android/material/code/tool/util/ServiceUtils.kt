package ly.android.material.code.tool.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils

object ServiceUtils {
    fun isServiceRunning(context: Context, serviceName: String): Boolean {
        if (TextUtils.isEmpty(serviceName)) {
            return false
        }
        val myManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningService =
            myManager.getRunningServices(30) as ArrayList<ActivityManager.RunningServiceInfo>
        for (i in runningService.indices) {
            if (runningService[i].service.className == serviceName) {
                return true
            }
        }
        return false
    }

    fun startForegroundService(context: Context, clazz: Class<*>){
        if (!isServiceRunning(context, clazz.name)){
            val intent = Intent(context, clazz)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            }else {
                context.startService(intent)
            }
        }
    }

    fun stopForegroundService(context: Context, clazz: Class<*>){
        context.stopService(Intent(context, clazz))
    }
}