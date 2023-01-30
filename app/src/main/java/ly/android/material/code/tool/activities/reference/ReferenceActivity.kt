package ly.android.material.code.tool.activities.reference

import android.content.res.Configuration
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.gson.Gson
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.widget.CodeEditor
import ly.android.material.code.langs.tiecode.TieCodeLanguage
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.clip
import ly.android.material.code.tool.data.entity.ReferenceBean
import ly.android.material.code.tool.data.enums.ReferenceLanguage
import ly.android.material.code.tool.databinding.ActivityReferenceBinding
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import ly.android.material.code.tool.util.ToastUtils
import ly.android.material.code.tool.util.switchThemeIfRequired
import org.eclipse.tm4e.core.registry.IThemeSource

class ReferenceActivity : AppCompatActivity() {

    private val gson = Gson()
    private val binding by bind(ActivityReferenceBinding::inflate)
    private var language: String? = null
    private lateinit var referenceBean: ReferenceBean

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        language = intent.getStringExtra("lang")
        val msg = intent.getStringExtra("reference") ?: "[]"
        referenceBean = gson.fromJson(msg, ReferenceBean::class.java)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            this.title = referenceBean.title
            this.subtitle = "${getString(R.string.author)}${referenceBean.author}"
        }

        binding.editor.apply {
            typefaceText = Typeface.createFromAsset(assets, "JetBrainsMono-Regular.ttf")
            setText(referenceBean.content)
            isEditable = false
            setLineSpacing(2f, 1.1f)
            nonPrintablePaintingFlags =
                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
            this.updateCompletionWindowPosition()
            postDelayedInLifecycle({
                formatCodeAsync()
            }, 1000)
        }

        loadDefaultThemes()
        loadDefaultLanguages()
        ensureTextmateTheme()

        val language: Language = when(language){
            ReferenceLanguage.TIE_CODE.toString() -> TieCodeLanguage()
            ReferenceLanguage.KOTLIN.toString() -> TextMateLanguage.create("source.kotlin", true)
            ReferenceLanguage.JAVA.toString() -> TextMateLanguage.create("source.java", true)
            ReferenceLanguage.C.toString() -> TextMateLanguage.create("source.c", true)
            ReferenceLanguage.CPP.toString() -> TextMateLanguage.create("source.cpp", true)
            ReferenceLanguage.IYU.toString() -> TextMateLanguage.create("source.java", true)
            ReferenceLanguage.HTML.toString() -> TextMateLanguage.create("text.html.basic", true)
            else -> TextMateLanguage.create("source.java", true)
        }
        binding.editor.setEditorLanguage(language)
        switchThemeIfRequired(this, binding.editor)

        binding.action.setOnFeedbackListener {
            it.context.clip(binding.editor.text)
            ToastUtils.toast(R.string.clip)
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        switchThemeIfRequired(this, binding.editor)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.editor.release()
    }

    private fun ensureTextmateTheme() {
        val editor = binding.editor
        var editorColorScheme = editor.colorScheme
        if (editorColorScheme !is TextMateColorScheme) {
            editorColorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance())
            editor.colorScheme = editorColorScheme
        }
    }

    private fun loadDefaultThemes() {
        //add assets file provider
        FileProviderRegistry.getInstance().addFileProvider(
            AssetsFileResolver(
                applicationContext.assets
            )
        )
        val themes = arrayOf("darcula", "quietlight")
        val themeRegistry = ThemeRegistry.getInstance()
        themes.forEach { name ->
            val path = "textmate/$name.json"
            themeRegistry.loadTheme(
                ThemeModel(
                    IThemeSource.fromInputStream(
                        FileProviderRegistry.getInstance().tryGetInputStream(path), path, null
                    ), name
                )
            )
        }
        themeRegistry.setTheme("quietlight")
    }

    private fun loadDefaultLanguages() {
        GrammarRegistry.getInstance().loadGrammars("textmate/languages.json")
    }

}