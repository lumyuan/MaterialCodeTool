package ly.android.material.code.tool.ui.windows

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.databinding.WindowClipBoardBinding
import ly.android.material.code.tool.sql.dao.ClipDao
import ly.android.material.code.tool.util.SharedPreferencesUtil
import ly.android.material.code.tool.util.ToastUtils
import kotlin.math.abs

class ClipBoardWindow(
    private val context: Context
) {

    private var isShow = false

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val binding = WindowClipBoardBinding.bind(
        View.inflate(context, R.layout.window_clip_board, null)
    )
    private val layoutParams = WindowManager.LayoutParams()
    private val keyX = "${this.javaClass.name}X"
    private val keyY = "${this.javaClass.name}Y"

    private var tag: String? = null

    private var clipboardManager: ClipboardManager? = null
    private lateinit var clipDao: ClipDao
    private val onPrimaryClipChangedListener = ClipboardManager.OnPrimaryClipChangedListener {
        println("new data: ${getNewData()}")
    }

    private fun getNewData(): CharSequence? {
        return clipboardManager?.primaryClip?.getItemAt(0)?.text
    }

    fun showWindow(){
        if (!getPermission()) return
        layoutParams.format = PixelFormat.TRANSLUCENT
        layoutParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.gravity = Gravity.TOP or Gravity.START
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT

        val loadX = SharedPreferencesUtil.load(keyX)
        val loadY = SharedPreferencesUtil.load(keyY)

        val x = loadX?.toInt() ?: 0
        val y = loadY?.toInt() ?: 0

        rX = x
        rY = y

        layoutParams.x = rX
        layoutParams.y = rY

        //set type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else
            layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT

        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_FULLSCREEN

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        windowManager.addView(binding.root, layoutParams)
        isShow = true
        tag = "true"

        initView()
    }

    private var rX = 0
    private var rY = 0
    private var touchStartRawX = 0f
    private var touchStartRawY = 0f
    private var x = 0f
    private var y = 0f
    private fun initView() {
        clipDao = DataBase.clipDataBase.clipDao()
        initClipManager()
        binding.root.apply {
            setOnTouchListener { _, event ->

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        this@ClipBoardWindow.x = event.rawX
                        this@ClipBoardWindow.y = event.rawY
                        touchStartRawX = event.rawX
                        touchStartRawY = event.rawY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val nowX = event.rawX.toInt()
                        val nowY = event.rawY.toInt()
                        val movedX = nowX - this@ClipBoardWindow.x
                        val movedY = nowY - this@ClipBoardWindow.y
                        this@ClipBoardWindow.x = nowX.toFloat()
                        this@ClipBoardWindow.y = nowY.toFloat()
                        this@ClipBoardWindow.layoutParams.x += movedX.toInt()
                        this@ClipBoardWindow.layoutParams.y += movedY.toInt()
                        windowManager.updateViewLayout(binding.root, this@ClipBoardWindow.layoutParams)
                    }
                    MotionEvent.ACTION_UP -> {
                        if (abs(event.rawX - touchStartRawX) < 15 && abs(event.rawY - touchStartRawY) < 15) {
                            performClick()
                        }
                        SharedPreferencesUtil.save(keyX, this@ClipBoardWindow.layoutParams.x.toString())
                        SharedPreferencesUtil.save(keyY, this@ClipBoardWindow.layoutParams.y.toString())
                    }
                }
                true
            }

            setOnClickListener {
                ToastUtils.toast("点击了")
            }
        }
    }

    private fun initClipManager(){
        clipboardManager = binding.root.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager?.addPrimaryClipChangedListener(onPrimaryClipChangedListener)
    }

    fun hideWindow(){
        clipboardManager?.removePrimaryClipChangedListener(onPrimaryClipChangedListener)
        if (tag != null){
            windowManager.removeView(binding.root)
        }
        tag = null
        isShow = false
    }

    fun isShow(): Boolean {
        return isShow
    }

    private fun getPermission(): Boolean{
        // 申请悬浮窗权限
        return if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(context)) {
                context.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + context.packageName)
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                )
                false
            }else {
                true
            }
        }else {
            true
        }
    }

}