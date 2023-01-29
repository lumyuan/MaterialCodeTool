package ly.android.material.code.tool.ui.theme

import android.app.UiModeManager
import android.content.Context
import ly.android.material.code.tool.MaterialCodeToolApplication

class Theme {

    companion object {
        fun isDarkMode(): Boolean {
            val uiModeManager: UiModeManager = MaterialCodeToolApplication.application.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            return uiModeManager.nightMode == UiModeManager.MODE_NIGHT_YES
        }
    }

}