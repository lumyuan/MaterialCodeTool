package ly.android.material.code.tool.activities.tools.postdev.response

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.getComponent
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.databinding.FragmentBodyResultBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.util.JsonUtils
import ly.android.material.code.tool.util.switchThemeIfRequired
import org.eclipse.tm4e.core.registry.IThemeSource
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class BodyResultFragment : BaseFragment() {

    private val binding by bind(FragmentBodyResultBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BodyResultFragment()
    }

    private val viewModel by activityViewModels<PostDevViewModel>()
    override fun initView(root: View) {
        super.initView(root)

        binding.editor.apply {
            typefaceText = Typeface.createFromAsset(this.context.assets, "JetBrainsMono-Regular.ttf")
            setLineSpacing(2f, 1.1f)
            nonPrintablePaintingFlags =
                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
            this.updateCompletionWindowPosition()
            isEditable = false
            getComponent<EditorAutoCompletion>()
                .setEnabledAnimation(true)
        }

        ensureTextmateTheme()
        loadDefaultThemes()
        loadDefaultLanguages()

        viewModel.bodyData.observe(this) {
            if (it != null){
                binding.editor.setEditorLanguage(
                    if (isJson(it)){
                        binding.editor.setText(
                            JsonUtils.stringToJSON(it)
                        )
                        TextMateLanguage.create(
                            "source.json", true
                        )
                    }else {
                        binding.editor.setText(
                            it
                        )
                        TextMateLanguage.create(
                            "text.html.basic", true
                        )
                    }
                )
                binding.editor.formatCodeAsync()
            }
        }

        switchThemeIfRequired(requireContext(), binding.editor)
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
                requireActivity().assets
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

    private fun isJson(content: String): Boolean {
        return try {
            if (content.startsWith("[") && content.endsWith("]")) {
                JSONArray(content)
                true
            } else {
                JSONObject(content)
                true
            }
        } catch (e: JSONException) {
            false
        }
    }
}