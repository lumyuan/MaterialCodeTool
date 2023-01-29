package ly.android.material.code.tool.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import androidx.activity.viewModels
import ly.android.io.common.Permissions
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.PermissionCheckViewModel
import ly.android.material.code.tool.databinding.ActivityPermissionCheckBinding
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.util.ToastUtils

class PermissionCheckActivity : AppCompatActivity(), OnClickListener {

    private val binding by bind(ActivityPermissionCheckBinding::inflate)
    private val viewModel by viewModels<PermissionCheckViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Permissions.hasCallExternalStorage() && Permissions.hasCallAllFile()){
            start()
            return
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel.externalStoragePermission.observe(this){
            it?.let {
                binding.esp.isChecked = it
            }
        }

        viewModel.manageStoragePermission.observe(this){
            it?.let {
                binding.msp.isChecked = it
            }
        }

        binding.getESP.setOnClickListener(this)
        binding.getMSP.setOnClickListener(this)
        binding.start.setOnClickListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.getESP -> {
                if (!Permissions.hasCallExternalStorage()){
                    Permissions.getCallExternalStorage(this)
                }
            }
            binding.getMSP -> {
                if (!Permissions.hasCallAllFile()){
                    Permissions.getCallAllFile()
                }
            }
            binding.start -> {
                if (viewModel.externalStoragePermission.value == true && viewModel.manageStoragePermission.value == true){
                    start()
                }else {
                    ToastUtils.toast(R.string.please_allow_permission)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus){
            viewModel.setExternalStoragePermissionState(Permissions.hasCallExternalStorage())
            viewModel.setManageStoragePermissionState(Permissions.hasCallAllFile())
        }
    }

    private fun start(){
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}