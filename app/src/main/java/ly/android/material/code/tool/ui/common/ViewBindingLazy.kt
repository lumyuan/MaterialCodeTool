package ly.android.material.code.tool.ui.common

import android.app.Activity
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import ly.android.material.code.tool.ui.theme.MyTheme

inline fun <VB: ViewBinding> AppCompatActivity.bind(
    crossinline inflater: (LayoutInflater) -> VB
) = lazy {
    fullScreen()
    inflater(layoutInflater).apply {
        setContentView(this.root)
    }
}

inline fun <VB: ViewBinding> Fragment.bind(
    crossinline inflater: (LayoutInflater) -> VB
) = lazy {
    activity?.apply {
        fullScreen()
    }
    inflater(layoutInflater)
}

fun Activity.fullScreen(){
    val darkMode = !MyTheme.isDarkMode()
    ImmersionBar.with(this)
        .transparentStatusBar()
        .transparentNavigationBar()
        .navigationBarEnable(false)
        .statusBarDarkFont(darkMode)
        .navigationBarDarkIcon(darkMode)
        .keyboardEnable(true)
        .init()
}