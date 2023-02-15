package ly.android.material.code.tool.common

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import ly.android.material.code.tool.R
import ly.android.material.code.tool.ui.base.BaseFragment

fun Fragment.replaceFragment(@IdRes id: Int, fragment: BaseFragment) {
    val beginTransaction = childFragmentManager.beginTransaction()
    beginTransaction.setCustomAnimations(R.anim.fragment_enter_pop, R.anim.fragment_exit_pop)
    beginTransaction.replace(id, fragment)
    beginTransaction.commit()
}