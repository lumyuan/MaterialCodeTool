package ly.android.material.code.tool.activities.notes

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import com.just.agentweb.AgentWeb
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import ly.android.material.code.tool.R
import ly.android.material.code.tool.databinding.ActivityPreviewBinding
import ly.android.material.code.tool.ui.common.bind

class PreviewActivity : AppCompatActivity() {

    private val binding by bind(ActivityPreviewBinding::inflate)

    private var langType: String? = null
    private var text: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        langType = intent.getStringExtra("lang")
        text = intent.getStringExtra("text")

        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            this.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.setTitle(R.string.note_preview)

        when (langType) {
            getString(R.string.lang_md) -> {
                binding.markdownView.visibility = View.GONE
                binding.textView.visibility = View.VISIBLE

                val markdown = Markwon.builder(this)
                    .usePlugin(GlideImagesPlugin.create(this))
                    .usePlugin(HtmlPlugin.create())
                    .usePlugin(LinkifyPlugin.create(true))
                    .build()
                text?.let { markdown.setMarkdown(binding.textView, it) }
            }
            else -> {
                binding.markdownView.visibility = View.VISIBLE
                binding.textView.visibility = View.GONE
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