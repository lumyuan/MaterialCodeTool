package ly.android.material.code.tool.activities.notes

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import io.github.rosemoe.sora.event.*
import io.github.rosemoe.sora.lang.Language
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.text.LineSeparator
import io.github.rosemoe.sora.widget.CodeEditor
import io.github.rosemoe.sora.widget.component.EditorAutoCompletion
import io.github.rosemoe.sora.widget.getComponent
import io.github.rosemoe.sora.widget.subscribeEvent
import ly.android.material.code.langs.tiecode.TieCodeLanguage
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.common.dip2px
import ly.android.material.code.tool.data.WriteNoteViewModel
import ly.android.material.code.tool.data.entity.NoteBean
import ly.android.material.code.tool.databinding.ActivityWriteNoteBinding
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.util.switchThemeIfRequired
import org.eclipse.tm4e.core.registry.IThemeSource
import java.text.SimpleDateFormat


class WriteNoteActivity : AppCompatActivity() {

    private val binding by bind(ActivityWriteNoteBinding::inflate)

    private val viewModel by viewModels<WriteNoteViewModel>()

    private var langType: String? = null
    private var baseId: Long = -1
    private val noteDao by lazy { DataBase.noteDataBase.noteDao() }
    private var noteBean: NoteBean? = null

    private var keyBoardState = false

    @SuppressLint("SimpleDateFormat")
    private val dateFormat =
        SimpleDateFormat(MaterialCodeToolApplication.application.getString(R.string.date_format))
    private var createDate: Long = -1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            this.title = null
        }

        langType = intent.getStringExtra("lang")
        baseId = intent.getLongExtra("id", -1)

        //从数据库加载数据
        loadData()

        createDate = System.currentTimeMillis()

        binding.langType.text = "${getString(R.string.lang_tip)}$langType"
        binding.date.text = dateFormat.format(
            noteBean?.createDate ?: createDate
        )

        setSymbols()

        binding.title.addTextChangedListener {
            viewModel.title.value = it.toString()
        }

        binding.editor.apply {
            typefaceText = Typeface.createFromAsset(assets, "JetBrainsMono-Regular.ttf")
            setLineSpacing(2f, 1.1f)
            nonPrintablePaintingFlags =
                CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
            this.updateCompletionWindowPosition()
            postDelayedInLifecycle({
                formatCodeAsync()
            }, 1000)

            // Update display dynamically
            subscribeEvent<SelectionChangeEvent> { _, _ ->
                updatePositionText()
            }
            subscribeEvent<PublishSearchResultEvent> { _, _ ->

            }
            subscribeEvent<ContentChangeEvent> { _, _ ->
                postDelayedInLifecycle(
                    {
                        viewModel.content.value = this.text.toString()
                    },
                    50
                )
            }
            subscribeEvent<SideIconClickEvent> { _, _ ->

            }

            subscribeEvent<KeyBindingEvent> { event, _ ->
                if (event.eventType != EditorKeyEvent.Type.DOWN) {
                    return@subscribeEvent
                }

            }

            getComponent<EditorAutoCompletion>()
                .setEnabledAnimation(true)
        }

        ensureTextmateTheme()
        loadDefaultThemes()
        loadDefaultLanguages()

        switchThemeIfRequired(this, binding.editor)

        binding.editor.setEditorLanguage(getLanguage())

        noteBean?.apply {
            viewModel.title.value = this.title
            viewModel.content.value = this.content
        }

        binding.editor.setText(viewModel.content.value)
        binding.title.setText(viewModel.title.value)

        binding.root.viewTreeObserver
            .addOnGlobalLayoutListener {
                val heightDiff: Int = binding.root.rootView.height - binding.root.height
                keyBoardState = if (heightDiff > applicationContext.dip2px(200f)) {
                    invalidateOptionsMenu()
                    true
                } else {
                    invalidateOptionsMenu()
                    false
                }
            }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.write_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.finished).isVisible = keyBoardState
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.undo -> {
                binding.editor.undo()
            }
            R.id.redo -> {
                binding.editor.redo()
            }
            R.id.finished -> {
                val inputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.editor.windowToken, 0)
                saveData()
                loadData()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        saveData()
        loadData()
    }

    override fun onDestroy() {
        saveData()
        binding.editor.release()
        setResult(RESULT_OK, Intent().apply {
            putExtra("savaType", "finished")
        })
        super.onDestroy()
    }

    private fun loadData() {
        if (baseId != -1L) {
            noteBean = noteDao.queryNote(baseId)
        }
    }

    private fun saveData() {
        if (TextUtils.isEmpty(viewModel.title.value) && TextUtils.isEmpty(viewModel.content.value)) {
            // finish
        } else {
            val noteBeanList = noteDao.queryAllNote()
            if (noteBean == null) {
                val bean = NoteBean(
                    id = System.currentTimeMillis(),
                    title = viewModel.title.value,
                    content = viewModel.content.value,
                    createDate = createDate,
                    language = langType,
                    rank = if (noteBeanList == null || noteBeanList.isEmpty()) {
                        0
                    } else {
                        noteDao.queryAllNote()!![0].rank!! + 1
                    },
                    classify = 0
                )
                baseId = bean.id!!
                noteDao.addNote(bean)
            } else {
                noteBean?.let {
                    it.title = viewModel.title.value
                    it.content = viewModel.content.value
                    noteDao.updateNote(it)
                }
            }
        }
    }

    private fun setSymbols() {
        val inputView = binding.symbolInput
        inputView.bindEditor(binding.editor)
        val typeface = Typeface.createFromAsset(assets, "JetBrainsMono-Regular.ttf")
        when (langType) {
            getString(R.string.lang_java), getString(R.string.lang_kt), getString(R.string.lang_cpp), getString(
                R.string.lang_iyu
            ),
            getString(R.string.lang_tie_lang), getString(R.string.lang_js), getString(R.string.lang_py), getString(
                R.string.lang_lua
            ) -> inputView.addSymbols(
                arrayOf("->", "{", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"),
                arrayOf("\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/")
            )
            getString(R.string.lang_md), getString(R.string.lang_xml), getString(R.string.lang_html) -> inputView.addSymbols(
                arrayOf("->", "<>", "<", "/", ">", ",", ".", ";", "\"", "?", "+", "-", "*"),
                arrayOf("\t", "<>", "<", "/>", ">", ",", ".", ";", "\"", "?", "+", "-", "*")
            )
            else -> inputView.addSymbols(
                arrayOf("->", "{", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/"),
                arrayOf("\t", "{}", "}", "(", ")", ",", ".", ";", "\"", "?", "+", "-", "*", "/")
            )
        }
        inputView.forEachButton {
            it.typeface = typeface
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

    private fun getLanguage(): Language = when (langType) {
        getString(R.string.lang_java) -> TextMateLanguage.create(
            "source.java", true
        )
        getString(R.string.lang_kt) -> TextMateLanguage.create(
            "source.kotlin", true
        )
        getString(R.string.lang_tie_lang) -> TieCodeLanguage()
        getString(R.string.lang_iyu) -> TextMateLanguage.create(
            "source.java", true
        )
        getString(R.string.lang_cpp) -> TextMateLanguage.create(
            "source.cpp", true
        )
        getString(R.string.lang_lua) -> TextMateLanguage.create(
            "source.lua", true
        )
        getString(R.string.lang_xml) -> TextMateLanguage.create(
            "text.xml", true
        )
        getString(R.string.lang_html) -> TextMateLanguage.create(
            "text.html.basic", true
        )
        getString(R.string.lang_py) -> TextMateLanguage.create(
            "source.python", true
        )
        getString(R.string.lang_js) -> TextMateLanguage.create(
            "source.js", true
        )
        getString(R.string.lang_md) -> TextMateLanguage.create(
            "text.html.markdown", true
        )
        else -> TextMateLanguage.create(
            "source.java", true
        )
    }

    private fun updatePositionText() {
        val cursor = binding.editor.cursor
        var text =
            (1 + cursor.leftLine).toString() + ":" + cursor.leftColumn + ";" + cursor.left + " "
        text += if (cursor.isSelected) {
            "(" + (cursor.right - cursor.left) + " chars)"
        } else {
            val content = binding.editor.text
            if (content.getColumnCount(cursor.leftLine) == cursor.leftColumn) {
                "(<" + content.getLine(cursor.leftLine).lineSeparator.let {
                    if (it == LineSeparator.NONE) {
                        "EOF"
                    } else {
                        it.name
                    }
                } + ">)"
            } else {
                val char = binding.editor.text.charAt(
                    cursor.leftLine,
                    cursor.leftColumn
                )
                if (char.isLowSurrogate() && cursor.leftColumn > 0) {
                    "(" + String(
                        charArrayOf(
                            binding.editor.text.charAt(
                                cursor.leftLine,
                                cursor.leftColumn - 1
                            ), char
                        )
                    ) + ")"
                } else if (char.isHighSurrogate() && cursor.leftColumn + 1 < binding.editor.text.getColumnCount(
                        cursor.leftLine
                    )
                ) {
                    "(" + String(
                        charArrayOf(
                            char, binding.editor.text.charAt(
                                cursor.leftLine,
                                cursor.leftColumn + 1
                            )
                        )
                    ) + ")"
                } else {
                    "(" + escapeIfNecessary(
                        binding.editor.text.charAt(
                            cursor.leftLine,
                            cursor.leftColumn
                        )
                    ) + ")"
                }
            }
        }
        val searcher = binding.editor.searcher
        if (searcher.hasQuery()) {
            val idx = searcher.currentMatchedPositionIndex
            val matchText = when (val count = searcher.matchedPositionCount) {
                0 -> {
                    "no match"
                }
                1 -> {
                    "1 match"
                }
                else -> {
                    "$count matches"
                }
            }
            text += if (idx == -1) {
                "($matchText)"
            } else {
                "(${idx + 1} of $matchText)"
            }
        }
        binding.textPosition.text = text
    }

    private fun escapeIfNecessary(c: Char): String {
        return when (c) {
            '\n' -> "\\n"
            '\t' -> "\\t"
            '\r' -> "\\r"
            ' ' -> "<ws>"
            else -> c.toString()
        }
    }
}