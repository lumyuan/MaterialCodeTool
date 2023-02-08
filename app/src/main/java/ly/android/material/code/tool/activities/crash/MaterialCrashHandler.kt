package ly.android.material.code.tool.activities.crash

import android.annotation.SuppressLint
import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver
import io.github.rosemoe.sora.widget.CodeEditor
import ly.android.material.code.tool.R
import ly.android.material.code.tool.ui.common.fullScreen
import ly.android.material.code.tool.util.ToastUtils
import ly.android.material.code.tool.util.switchThemeIfRequired
import org.eclipse.tm4e.core.registry.IThemeSource
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess

object MaterialCrashHandler {

    val DEFAULT_UNCAUGHT_EXCEPTION_HANDLER: Thread.UncaughtExceptionHandler? = Thread.getDefaultUncaughtExceptionHandler()

    fun init(app: Application) {
        init(app, null)
    }

    fun init(app: Application, crashDir: String?) {
        Thread.setDefaultUncaughtExceptionHandler(object : Thread.UncaughtExceptionHandler {
            override fun uncaughtException(thread: Thread, throwable: Throwable) {
                try {
                    tryUncaughtException(thread, throwable)
                } catch (e: Throwable) {
                    e.printStackTrace()
                    DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(thread, throwable)
                }
            }

            private fun tryUncaughtException(thread: Thread, throwable: Throwable) {
                @SuppressLint("SimpleDateFormat") val time =
                    SimpleDateFormat(app.getString(R.string.date_format)).format(
                        Date()
                    )
                val crashFile = File(
                    if (TextUtils.isEmpty(crashDir)) File(
                        app.filesDir,
                        "crash"
                    ) else crashDir?.let { File(it) },
                    "crash_$time.txt"
                )
                var versionName = "unknown"
                var versionCode: Long = 0
                try {
                    val packageInfo = app.packageManager.getPackageInfo(app.packageName, 0)
                    versionName = packageInfo.versionName
                    versionCode =
                        if (Build.VERSION.SDK_INT >= 28) packageInfo.longVersionCode else packageInfo.versionCode.toLong()
                } catch (ignored: PackageManager.NameNotFoundException) {
                    ignored.printStackTrace()
                }
                var fullStackTrace: String
                run {
                    val sw = StringWriter()
                    val pw = PrintWriter(sw)
                    throwable.printStackTrace(pw)
                    fullStackTrace = sw.toString()
                    pw.close()
                }
                val appError = app.getString(R.string.app_error)
                val errorLog = "************* $appError ****************\n" +
                        "Time Of Crash      : $time\n" +
                        "Device Manufacturer: ${Build.MANUFACTURER}\n" +
                        "Device Model       : ${Build.MODEL}\n" +
                        "Android Version    : ${Build.VERSION.RELEASE}\n" +
                        "Android SDK        : ${Build.VERSION.SDK_INT}\n" +
                        "App VersionName    : $versionName\n" +
                        "App VersionCode    : ${versionCode}\n" +
                        "${app.getString(R.string.feedback_bug)}\n" +
                        "************* $appError ****************\n$fullStackTrace"
                try {
                    writeFile(crashFile, errorLog)
                } catch (ignored: IOException) {
                    ignored.printStackTrace()
                }
                val intent = Intent(app, CrashActivity::class.java)
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
                intent.putExtra(CrashActivity.EXTRA_CRASH_INFO, errorLog)
                try {
                    app.startActivity(intent)
                    Process.killProcess(Process.myPid())
                    exitProcess(0)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    DEFAULT_UNCAUGHT_EXCEPTION_HANDLER?.uncaughtException(thread, throwable)
                }
            }

            @Throws(IOException::class)
            private fun writeFile(file: File, content: String) {
                val parentFile = file.parentFile
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs()
                }
                file.createNewFile()
                val fos = FileOutputStream(file)
                fos.write(content.toByteArray())
                try {
                    fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
    }

    class CrashActivity : AppCompatActivity() {
        private var mLog: String? = null

        @SuppressLint("ClickableViewAccessibility")
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            fullScreen()
            mLog = intent.getStringExtra(EXTRA_CRASH_INFO)
            val contentView = LinearLayout(this)
            contentView.fitsSystemWindows = true
            contentView.orientation = LinearLayout.VERTICAL
            val materialToolbar = MaterialToolbar(this)
            materialToolbar.title = getString(R.string.app_name) + " " + getString(R.string.crash_handler)
            materialToolbar.setSubtitle(R.string.app_error)
            contentView.addView(materialToolbar)
            setSupportActionBar(materialToolbar)
            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
            }
            val codeEditor = CodeEditor(this).apply {
                typefaceText = Typeface.createFromAsset(assets, "JetBrainsMono-Regular.ttf")
                setText(mLog)
                isEditable = false
                setLineSpacing(2f, 1.1f)
                nonPrintablePaintingFlags =
                    CodeEditor.FLAG_DRAW_WHITESPACE_LEADING or CodeEditor.FLAG_DRAW_LINE_SEPARATOR or CodeEditor.FLAG_DRAW_WHITESPACE_IN_SELECTION
                this.updateCompletionWindowPosition()
                postDelayedInLifecycle({
                    formatCodeAsync()
                }, 1000)
                isEditable = false
            }

            loadDefaultThemes()
            loadDefaultLanguages()
            ensureTextmateTheme(codeEditor)

            val language = TextMateLanguage.create("source.java", true)
            codeEditor.setEditorLanguage(language)
            switchThemeIfRequired(this, codeEditor)

            contentView.addView(codeEditor)
            setContentView(contentView)
        }

        override fun onDestroy() {
            super.onDestroy()
//            restart()
        }

        private fun restart() {
            val pm = packageManager
            val intent = pm.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
                )
                startActivity(intent)
            }
            finish()
            Process.killProcess(Process.myPid())
            exitProcess(0)
        }

        override fun onOptionsItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.copy -> {
                    val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText(packageName, mLog))
                    ToastUtils.toast(R.string.copy_error)
                }
                android.R.id.home -> {
                    finish()
                }
            }
            return super.onOptionsItemSelected(item)
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.crash_menu, menu)
            return true
        }

        companion object {
            const val EXTRA_CRASH_INFO = "crashInfo"
        }

        private fun ensureTextmateTheme(editor: CodeEditor) {
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
                    assets
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

}