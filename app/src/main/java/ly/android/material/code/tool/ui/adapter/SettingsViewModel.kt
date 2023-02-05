package ly.android.material.code.tool.ui.adapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ly.android.material.code.tool.core.Settings

class SettingsViewModel: ViewModel() {

    val setting = MutableLiveData<Settings.Setting>()

}