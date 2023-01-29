package ly.android.material.code.tool.ui.common

import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.gyf.immersionbar.ImmersionBar
import ly.android.material.code.tool.ui.theme.Theme

inline fun <VB: ViewBinding> AppCompatActivity.bind(
    crossinline inflater: (LayoutInflater) -> VB
) = lazy {
    fullScreen(this)
    inflater(layoutInflater).apply {
        setContentView(this.root)
    }
}

inline fun <VB: ViewBinding> Fragment.bind(
    crossinline inflater: (LayoutInflater) -> VB
) = lazy {
    activity?.apply {
        fullScreen(this as AppCompatActivity)
    }
    inflater(layoutInflater)
}

fun fullScreen(activity: AppCompatActivity){
    val darkMode = !Theme.isDarkMode()
    ImmersionBar.with(activity)
        .transparentStatusBar()
        .transparentNavigationBar()
        .statusBarDarkFont(darkMode)
        .navigationBarDarkIcon(darkMode)
        .keyboardEnable(true)
        .init()
}