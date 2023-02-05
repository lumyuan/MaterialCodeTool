package ly.android.material.code.tool.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.core.Settings
import ly.android.material.code.tool.databinding.ActivitySettingsBinding
import ly.android.material.code.tool.ui.adapter.SettingsViewModel
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.theme.MaterialCodeToolTheme
import ly.android.material.code.tool.util.ToastUtils

class SettingsActivity : AppCompatActivity() {

    private val binding by bind(ActivitySettingsBinding::inflate)

    private val viewModel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }

        viewModel.setting.value = MaterialCodeToolApplication.setting

        binding.composeView.setContent {
            MaterialCodeToolTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        ColumView()
                    }
                }
            }
        }

    }

    @Composable
    fun ColumView() {
        val aliIconSavePathDialogState = remember {
            mutableStateOf(false)
        }
        var pathState by remember {
            mutableStateOf(MaterialCodeToolApplication.setting!!.iconSavePath!!)
        }
        viewModel.setting.observe(this) {
            MaterialCodeToolApplication.setting = it
            if (Settings.saveSetting(it)) {
                println("成功")
            }else {
                println("失败")
            }
            pathState = it.iconSavePath!!
        }
        Spacer(modifier = Modifier.size(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    aliIconSavePathDialogState.value = true
                }
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_file_open),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.ali_icon_path),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = pathState,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
        }

        AliIconPathDialog(state = aliIconSavePathDialogState)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AliIconPathDialog(
        state: MutableState<Boolean>
    ) {
        if (state.value) {
            val ss = viewModel.setting.observeAsState()
            var pathState by remember {
                mutableStateOf(ss.value!!.iconSavePath!!)
            }
            Dialog(
                onDismissRequest = { state.value = false },
                content = {
                    Card(
                        modifier = Modifier
                            .clip(
                                shape = RoundedCornerShape(30.dp)
                            )
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.set_path),
                                style = MaterialTheme.typography.titleLarge
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            OutlinedTextField(value = pathState, onValueChange = {
                                pathState = it
                            }, label = {
                                Text(text = stringResource(id = R.string.path))
                            },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(onClick = {
                                if (TextUtils.isEmpty(pathState)){
                                    ToastUtils.toast(R.string.please_input_path)
                                    return@Button
                                }
                                val setting = viewModel.setting.value!!.apply {
                                    this.iconSavePath = pathState
                                }
                                viewModel.setting.value = setting
                                state.value = false
                            }, modifier = Modifier.align(Alignment.End)) {
                                Text(text = stringResource(id = R.string.save))
                            }
                        }
                    }
                }
            )
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}