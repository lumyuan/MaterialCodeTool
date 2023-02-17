package ly.android.material.code.tool.ui.adapter

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.core.Settings
import ly.android.material.code.tool.data.entity.HomePageBean

class SettingsViewModel: ViewModel() {

    val setting = MutableLiveData<Settings.Setting>()

}