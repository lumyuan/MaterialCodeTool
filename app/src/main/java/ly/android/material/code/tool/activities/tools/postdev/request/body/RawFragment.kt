package ly.android.material.code.tool.activities.tools.postdev.request.body

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.lxj.xpopup.XPopup
import io.github.rosemoe.sora.event.*
import io.github.rosemoe.sora.lang.Language
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
import io.github.rosemoe.sora.widget.subscribeEvent
import ly.android.material.code.langs.tiecode.TieCodeLanguage
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.data.entity.BodyParamType
import ly.android.material.code.tool.data.enums.BodyRawType
import ly.android.material.code.tool.databinding.FragmentRawBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.theme.MyTheme
import ly.android.material.code.tool.util.switchThemeIfRequired
import org.eclipse.tm4e.core.registry.IThemeSource

class RawFragment : BaseFragment() {

    private val binding by bind(FragmentRawBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = RawFragment()
    }

    private val rawTypeArray = BodyRawType.values()
    private val rawTypeArrayString by lazy {
        val array = arrayOfNulls<String>(rawTypeArray.size)
        for (index in rawTypeArray.indices) {
            array[index] = rawTypeArray[index].toString()
        }
        array
    }
    private val viewModel by activityViewModels<PostDevViewModel>()

    private val popupView by lazy {
        XPopup.Builder(requireActivity())
            .hasShadowBg(false)
            .isCoverSoftInput(true)
            .atView(binding.typeChip)
            .isDarkTheme(MyTheme.isDarkMode())
            .asAttachList(
                rawTypeArrayString, null
            ) { i, _ ->
                viewModel.bodyRawType.value = rawTypeArray[i]
            }
    }

    @SuppressLint("SetTextI18n")
    override fun initView(root: View) {
        super.initView(root)

        viewModel.bodyRaw.observe(this){
            binding.editor.setText(it)
        }

        binding.typeChip.setOnClickListener {
            popupView.show()
        }

        binding.editor.apply {
            typefaceText = Typeface.createFromAsset(this.context.assets, "JetBrainsMono-Regular.ttf")
            setLineSpacing(2f, 1.1f)
            nonPrintablePaintingFlags =
                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
            this.updateCompletionWindowPosition()

            getComponent<EditorAutoCompletion>()
                .setEnabledAnimation(true)
        }

        ensureTextmateTheme()
        loadDefaultThemes()
        loadDefaultLanguages()

        switchThemeIfRequired(requireContext(), binding.editor)

        viewModel.bodyRawType.observe(this) {
            binding.typeChip.text = "${getString(R.string.raw_type)}$it"
            ensureTextmateTheme()
            binding.editor.setEditorLanguage(getLanguage(it))
        }

        viewModel.sendState.observe(this) {
            viewModel.bodyRaw.value = binding.editor.text.toString()
        }
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

    private fun getLanguage(langType: BodyRawType): Language = when (langType) {
        BodyRawType.Text -> TextMateLanguage.create(
            "text.html.markdown", true
        )
        BodyRawType.JavaScript -> TextMateLanguage.create(
            "source.js", true
        )
        BodyRawType.JSON -> TextMateLanguage.create(
            "source.json", true
        )
        BodyRawType.HTML -> TextMateLanguage.create(
            "text.html.basic", true
        )
        BodyRawType.XML -> TextMateLanguage.create(
            "text.xml", true
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.sendState.value = System.currentTimeMillis()
    }
}