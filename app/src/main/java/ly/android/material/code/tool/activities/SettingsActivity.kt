package ly.android.material.code.tool.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.core.Settings
import ly.android.material.code.tool.data.entity.State
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

        //同步内存、持久层的数据
        viewModel.setting.observe(this) {
            MaterialCodeToolApplication.setting = it
            if (Settings.saveSetting(it)) {
                println("成功")
            } else {
                println("失败")
            }
        }

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
        Spacer(modifier = Modifier.size(8.dp))
        IconSavePath()
        SetVibrateState()
        SetHomePage()
    }

    @Composable
    private fun IconSavePath() {
        //图标保存路径
        val aliIconSavePathDialogState = remember {
            mutableStateOf(false)
        }
        var pathState by remember {
            mutableStateOf(MaterialCodeToolApplication.setting!!.iconSavePath!!)
        }

        viewModel.setting.observe(this) {
            pathState = it.iconSavePath!!
        }

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
                                if (TextUtils.isEmpty(pathState)) {
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

    @Composable
    private fun SetVibrateState() {
        val vibrateState = remember {
            mutableStateOf(viewModel.setting.value?.isVibrate == true)
        }

        //振动
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    vibrateState.value = !vibrateState.value
                }
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_vibrate),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.can_vibrate),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(id = R.string.can_vibrate_tip),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Switch(checked = vibrateState.value, onCheckedChange = {
                    vibrateState.value = !vibrateState.value
                })
                SaveVibrateState(state = vibrateState)
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }

    @Composable
    private fun SaveVibrateState(
        state: MutableState<Boolean>
    ) {
        viewModel.setting.value = MaterialCodeToolApplication.setting?.apply {
            this.isVibrate = state.value
        }
    }

    private val pageNames by lazy {
        arrayOf(
            getString(R.string.reference),
            getString(R.string.tools),
            getString(R.string.notes),
            getString(R.string.page_flow)
        )
    }

    private val homeIconIds = arrayOf(
        R.drawable.ic_reference_code,
        R.drawable.ic_tools,
        R.drawable.ic_notes,
        R.drawable.ic_last
    )

    @Composable
    private fun SetHomePage() {
        val homePageDialogState = remember {
            mutableStateOf(false)
        }

        val homePosition = remember {
            mutableStateOf(
                when (viewModel.setting.value?.homePageBean?.state) {
                    State.POSITION -> viewModel.setting.value?.homePageBean?.position ?: 0
                    State.FLOW -> pageNames.size - 1
                    null -> 0
                }
            )
        }

        val homePageName = remember {
            mutableStateOf(pageNames[homePosition.value])
        }

        //自定义首页
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    homePageDialogState.value = true
                }
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_home),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(id = R.string.set_home_page),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = stringResource(id = R.string.set_home_page_tip),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = homePageName.value,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground,
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
        }

        SetHomePageDialog(homePageDialogState, homePosition, homePageName)

    }

    @Composable
    private fun SetHomePageDialog(
        homePageDialogState: MutableState<Boolean>,
        homePosition: MutableState<Int>,
        homePageName: MutableState<String>
    ) {

        if (homePageDialogState.value) {
            Dialog(
                onDismissRequest = {
                    homePageDialogState.value = false
                },
                content = {
                    Card(
                        modifier = Modifier
                            .clip(
                                shape = RoundedCornerShape(30.dp)
                            )
                            .wrapContentSize()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            LazyColumn(
                                modifier = Modifier.wrapContentHeight()
                            ) {
                                items(pageNames.size) { index: Int ->
                                    HomePageView(
                                        index = index,
                                        homePageDialogState = homePageDialogState,
                                        homePosition = homePosition,
                                        homePageName = homePageName
                                    )
                                }
                            }
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun HomePageView(
        index: Int,
        homePageDialogState: MutableState<Boolean>,
        homePosition: MutableState<Int>,
        homePageName: MutableState<String>
    ) {
        Column(
            modifier = Modifier.clickable {
                homePosition.value = index
                homePageName.value = pageNames[index]
                viewModel.setting.value = viewModel.setting.value?.apply {
                    if (index == pageNames.size - 1) {
                        this.homePageBean.apply {
                            this.state = State.FLOW
                            this.position = index
                        }
                    } else {
                        this.homePageBean.apply {
                            this.position = index
                            this.state = State.POSITION
                        }
                    }
                }
                homePageDialogState.value = false
            }
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = homeIconIds[index]),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = pageNames[index],
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
                Spacer(modifier = Modifier.size(8.dp))
                AnimatedVisibility(visible = homePosition.value == index) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_radio),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
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

    @Preview(showBackground = true)
    @Composable
    private fun DefaultPreview() {
        MaterialCodeToolTheme {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                ColumView()
            }
        }
    }
}