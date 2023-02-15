package ly.android.material.code.tool.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipboardManager
import android.content.ClipboardManager.OnPrimaryClipChangedListener
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.*
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.tools.QuickShearingBoardActivity
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.data.ClipServiceViewModel
import ly.android.material.code.tool.sql.dao.ClipDao
import ly.android.material.code.tool.ui.windows.ClipBoardWindow


class MaterialClipBoardService : LifecycleService() {

    private val viewModel : ClipServiceViewModel = ClipServiceViewModel
    private lateinit var clipBoardWindow: ClipBoardWindow
    override fun onCreate() {
        super.onCreate()
        //发送前台通知
        setForegroundService(getString(R.string.clip_service_title), null)

        clipBoardWindow = ClipBoardWindow(this)

        viewModel.isShow.observe(this){
            if (it){
                clipBoardWindow.showWindow()
            }else {
                clipBoardWindow.hideWindow()
            }
        }
    }

    private fun setForegroundService(title: String?, subtitle: String?) {
        val id = getString(R.string.clip_service_id)

        val intent = Intent(this, QuickShearingBoardActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else PendingIntent.FLAG_UPDATE_CURRENT)
        val notification: NotificationCompat.Builder //创建服务对象

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(id, getString(R.string.process_name), NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(channel)
            notification = NotificationCompat.Builder(this).setChannelId(id)
        } else {
            notification = NotificationCompat.Builder(this)
        }
        notification.setContentTitle(title)
            .setContentText(subtitle)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_logo)
            .setContentIntent(pendingIntent)
            .build()
        val notification1: Notification = notification.build()
        startForeground(1, notification1)
    }

}