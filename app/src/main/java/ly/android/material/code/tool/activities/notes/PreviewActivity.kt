package ly.android.material.code.tool.activities.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import com.just.agentweb.AgentWeb
import ly.android.material.code.tool.R
import ly.android.material.code.tool.databinding.ActivityPreviewBinding
import ly.android.material.code.tool.ui.common.bind

class PreviewActivity : AppCompatActivity() {

    private val binding by bind(ActivityPreviewBinding::inflate)

    private lateinit var agentWeb: AgentWeb
    private var langType: String? = null
    private var text: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        langType = intent.getStringExtra("lang")
        text = intent.getStringExtra("text")

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            this.setTitle(R.string.note_preview)
            this.subtitle = langType
            this.setDisplayHomeAsUpEnabled(true)
        }

        when (langType) {
            getString(R.string.lang_md) -> {
                binding.markdownView.setText(text)
            }
            else -> {
//                binding.frameLayout.removeAllViews()
//                agentWeb = AgentWeb.with(this)
//                    .setAgentWebParent(binding.frameLayout,
//                        LinearLayout.LayoutParams(
//                            LinearLayout.LayoutParams.MATCH_PARENT,
//                            LinearLayout.LayoutParams.MATCH_PARENT
//                        )
//                    )
//                    .useDefaultIndicator()
//                    .createAgentWeb()
//                    .ready()
//                    .get()
//                text?.let { agentWeb.webCreator.webView.loadDataWithBaseURL(null,
//                    it, "text/html", "utf-8", null) }
                text?.let {
                    binding.markdownView.loadDataWithBaseURL(
                        null,
                        it, "text/html", "utf-8", null
                    )
                }
            }
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

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (!binding.markdownView.canGoBack()) {
                    finish()
                } else {
                    binding.markdownView.goBack()
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

}