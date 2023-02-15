package ly.android.material.code.tool.activities.tools

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.materialswitch.MaterialSwitch
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.dip2px
import ly.android.material.code.tool.data.ClipServiceViewModel
import ly.android.material.code.tool.databinding.ActivityQuickShearingBoardBinding
import ly.android.material.code.tool.ui.common.bind

class QuickShearingBoardActivity : AppCompatActivity() {

    private val binding by bind(ActivityQuickShearingBoardBinding::inflate)
    private lateinit var materialSwitch: MaterialSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            this.setTitle(R.string.shearing_board)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.quick_shearing_board_menu, menu)
        materialSwitch = menu.findItem(R.id.showWindow).actionView as MaterialSwitch
        materialSwitch.apply {
            isChecked = ClipServiceViewModel.isShow.value.let {
                it ?: false
            }
            setText(R.string.show_window)
            setPadding(0, 0, dip2px(16f), 0)
            setOnCheckedChangeListener { _, isChecked ->
                ClipServiceViewModel.isShow.value = isChecked
            }
        }
        return super.onCreateOptionsMenu(menu)
    }
}