package ly.android.material.code.tool.activities

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.google.android.material.checkbox.MaterialCheckBox
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.fragments.MainDrawerFragment
import ly.android.material.code.tool.activities.fragments.MainFragment
import ly.android.material.code.tool.activities.fragments.notes.NoteCatalogueFragment
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.common.dip2px
import ly.android.material.code.tool.common.height
import ly.android.material.code.tool.common.width
import ly.android.material.code.tool.core.Settings
import ly.android.material.code.tool.data.MainViewModel
import ly.android.material.code.tool.data.NotesViewModel
import ly.android.material.code.tool.data.entity.NoteItemBean
import ly.android.material.code.tool.data.entity.State
import ly.android.material.code.tool.databinding.ActivityMainBinding
import ly.android.material.code.tool.ui.common.*
import ly.android.material.code.tool.util.ToastUtils
import kotlin.math.min

class MainActivity : AppCompatActivity() {

    private val binding by bind(
        ActivityMainBinding::inflate
    )

    private val viewMode by viewModels<MainViewModel>()
    private val notesViewModel by viewModels<NotesViewModel>()
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
        replaceFragment(
            id = R.id.noteCatalogue,
            fragment = NoteCatalogueFragment.newInstance()
        )

        viewMode.pageCurrent.observe(this) {
            it?.let {
                val title = viewMode.titles.value?.get(it)
                supportActionBar?.title = title
                try {
                    if (it == 2) {
                        binding.root.addView(binding.noteCatalogue)
                    } else {
                        binding.root.removeView(binding.noteCatalogue)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            invalidateOptionsMenu()
        }

        viewMode.drawerState.observe(this) {
            if (it == false) {
                binding.root.closeDrawer(GravityCompat.START)
            }
        }

        viewMode.noteLangClickState.observe(this) {
            invalidateOptionsMenu()
            if (it == false) {
                viewMode.isCheckAll.value = false
            }
        }

        viewMode.searchState.observe(this) {
            invalidateOptionsMenu()
        }
    }

    @SuppressLint("CommitTransaction")
    private fun replaceFragment(@IdRes id: Int, fragment: Fragment) {
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.setCustomAnimations(R.anim.fragment_enter, R.anim.fragment_exit)
            .replace(id, fragment)
            .commit()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && viewMode.pageCurrent.value == 2) {
            invalidateOptionsMenu()
        }

        if (!hasFocus) {

            //保存页面数据
            MaterialCodeToolApplication.setting?.apply {
                when (this.homePageBean.state) {
                    State.POSITION -> {}
                    State.FLOW -> {
                        Settings.saveSetting(
                            MaterialCodeToolApplication.setting!!.apply {
                                this.homePageBean.position = viewMode.pageCurrent.value!!
                            }
                        )
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        val checkBox = menu.findItem(R.id.checkAll).actionView as MaterialCheckBox
        viewMode.isCheckAll.observe(this) {
            checkBox.isChecked = it
        }
        checkBox.setOnClickListener {
            viewMode.checkAllBoxState.value = checkBox.isChecked
        }
        val searchView = menu.findItem(R.id.searchView).actionView as SearchView
        searchView.apply {
            this.queryHint = getString(R.string.search_note)
            setOnQueryTextListener(object : OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    DataBase.noteDataBase.noteDao().queryNotes(newText.toString().trim())?.let {
                        val list = ArrayList<NoteItemBean>()
                        it.forEach { item ->
                            list.add(
                                NoteItemBean(
                                    false, item
                                )
                            )
                        }
                        notesViewModel.setValues(
                            list = list
                        )
                    }
                    return true
                }

            })

            //反射调用关闭方法
            try {
                val javaClass = this.javaClass
                val declaredMethod = javaClass.getDeclaredMethod("onCloseClicked")
                declaredMethod.isAccessible = true
                setOnQueryTextFocusChangeListener { _, b ->
                    if (!b) {
                        declaredMethod.invoke(this)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.checkMore -> {
                viewMode.noteLangClickState.value = true
            }

            R.id.checkAll -> {
                val checkBox = item.actionView as MaterialCheckBox
                viewMode.checkAllBoxState.value = checkBox.isChecked
            }

            R.id.close -> {
                viewMode.noteLangClickState.value = false
            }

            R.id.checkedRemove -> {
                viewMode.removeCheckedState.value = System.currentTimeMillis()
            }

            R.id.noteCatalogue -> {
                binding.root.openDrawer(GravityCompat.END)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val checkAll = menu.findItem(R.id.checkAll)
        val checkedRemove = menu.findItem(R.id.checkedRemove)
        val close = menu.findItem(R.id.close)
        val checkMore = menu.findItem(R.id.checkMore)
        val searchView = menu.findItem(R.id.searchView)
        val noteCatalogue = menu.findItem(R.id.noteCatalogue)
        if (viewMode.pageCurrent.value == 2) {
            if (DataBase.noteDataBase.noteDao().queryAllNote()?.isEmpty() == true) {
                checkAll.isVisible = false
                checkedRemove.isVisible = false
                close.isVisible = false
                checkMore.isVisible = false
                searchView.isVisible = false
                noteCatalogue.isVisible = false
                try {
                    binding.root.removeView(binding.noteCatalogue)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                if (viewMode.noteLangClickState.value == true) {
                    checkAll.isVisible = true
                    checkedRemove.isVisible = true
                    close.isVisible = true
                    checkMore.isVisible = false
                    searchView.isVisible = false
                    noteCatalogue.isVisible = false
                    try {
                        binding.root.removeView(binding.noteCatalogue)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    checkAll.isVisible = false
                    checkedRemove.isVisible = false
                    close.isVisible = false
                    checkMore.isVisible = true
                    searchView.isVisible = true
                    noteCatalogue.isVisible = true
                    try {
                        binding.root.addView(binding.noteCatalogue)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        } else {
            checkAll.isVisible = false
            checkedRemove.isVisible = false
            close.isVisible = false
            checkMore.isVisible = false
            searchView.isVisible = false
            noteCatalogue.isVisible = false
            try {
                binding.root.removeView(binding.noteCatalogue)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    private var keyDownTimer = 0L
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                val currentTimeMillis = System.currentTimeMillis()
                if (currentTimeMillis - keyDownTimer >= 2000) {
                    if (viewMode.noteLangClickState.value == true) {
                        viewMode.noteLangClickState.value = false
                    } else if (binding.root.isDrawerOpen(GravityCompat.END)) {
                        binding.root.closeDrawer(GravityCompat.END)
                    } else if (!binding.root.isDrawerOpen(GravityCompat.START)) {
                        binding.root.openDrawer(GravityCompat.START)
                    } else {
                        ToastUtils.toast(R.string.kill_self, Toast.LENGTH_SHORT)
                        keyDownTimer = currentTimeMillis
                    }
                } else {
                    keyDownTimer = 0
                    this.finish()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}