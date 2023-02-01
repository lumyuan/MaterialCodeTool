package ly.android.material.code.tool.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.fragments.MainDrawerFragment
import ly.android.material.code.tool.activities.fragments.MainFragment
import ly.android.material.code.tool.common.dip2px
import ly.android.material.code.tool.common.height
import ly.android.material.code.tool.common.width
import ly.android.material.code.tool.data.MainViewModel
import ly.android.material.code.tool.databinding.ActivityMainBinding
import ly.android.material.code.tool.ui.common.*
import ly.android.material.code.tool.util.ToastUtils
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private val binding by bind(
        ActivityMainBinding::inflate
    )

    private val viewMode by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.root,
            binding.toolbar,
            R.string.app_name,
            R.string.app_name
        )
        toggle.syncState()
        binding.root.addDrawerListener(toggle)

        val min = min(height(), width())
        binding.mainDrawer.apply {
            layoutParams.width = min(min, dip2px(250f))
            requestLayout()
        }
        replaceFragment(
            id = R.id.contentView,
            fragment = MainFragment.newInstance()
        )
        replaceFragment(
            id = R.id.mainDrawer,
            fragment = MainDrawerFragment.newInstance()
        )

        viewMode.pageCurrent.observe(this){
            it?.let {
                val title = viewMode.titles.value?.get(it)
                supportActionBar?.title = title
            }
        }

        viewMode.drawerState.observe(this){
            if (it == false){
                binding.root.closeDrawer(GravityCompat.START)
            }
        }
    }

    @SuppressLint("CommitTransaction")
    private fun replaceFragment(@IdRes id: Int, fragment: Fragment) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
            .replace(id, fragment)
            .commit()
    }

    private var keyDownTimer = 0L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                val currentTimeMillis = System.currentTimeMillis()
                if (currentTimeMillis - keyDownTimer >= 2000){
                    ToastUtils.toast(R.string.kill_self, Toast.LENGTH_SHORT)
                    keyDownTimer = currentTimeMillis
                }else {
                    keyDownTimer = 0
                    this.finish()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}