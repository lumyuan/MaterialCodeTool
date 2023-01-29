package ly.android.material.code.tool.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.fragments.MainDrawerFragment
import ly.android.material.code.tool.activities.fragments.MainFragment
import ly.android.material.code.tool.data.MainViewModel
import ly.android.material.code.tool.databinding.ActivityMainBinding
import ly.android.material.code.tool.ui.common.*
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

    private fun replaceFragment(@IdRes id: Int, fragment: Fragment) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
            .replace(id, fragment)
            .commit()
    }
}