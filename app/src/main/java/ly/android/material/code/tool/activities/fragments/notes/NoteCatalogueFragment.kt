package ly.android.material.code.tool.activities.fragments.notes

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.activityViewModels
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.notes.WriteNoteActivity
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.data.NotesViewModel
import ly.android.material.code.tool.data.entity.NoteItemBean
import ly.android.material.code.tool.databinding.FragmentNoteCatalogueBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.theme.MaterialCodeToolTheme
import ly.android.material.code.tool.util.SharedPreferencesUtil
import java.util.ArrayList

class NoteCatalogueFragment : BaseFragment() {

    private val binding by bind(FragmentNoteCatalogueBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoteCatalogueFragment()
    }

    private val viewModel by activityViewModels<NotesViewModel>()
    override fun initView(root: View) {
        super.initView(root)

        viewModel.list.observe(this) {
            it?.apply {
                viewModel.setLangMap(this)
            }
        }

        binding.composeView.setContent {
            MaterialCodeToolTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        ToolbarView()
                        Spacer(modifier = Modifier.size(8.dp))
                        //ScrollView
                        CatalogueView()
                    }
                }
            }
        }
    }

    @Composable
    private fun ToolbarView() {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.size(56.dp))
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(id = R.string.note_catalogue),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }

    @Composable
    private fun CatalogueView() {

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .weight(1f)
            ) {
                val mapState by viewModel.langMapLiveData.observeAsState()
                if ((mapState?.entries?.size ?: 0) < 1) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_note),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    mapState?.entries?.forEach {
                        Spacer(modifier = Modifier.size(8.dp))
                        ClassBox(it)
                        Spacer(modifier = Modifier.size(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.size(16.dp))
        }

    }

    @Composable
    private fun ClassBox(entry: MutableMap.MutableEntry<String?, ArrayList<NoteItemBean>>) {
        val key = "catalogue_${entry.key}"
        val state = SharedPreferencesUtil.load(key = key)
        var openState by remember {
            mutableStateOf(
                state?.toBoolean() ?: true
            )
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .background(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp)
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            openState = !openState
                            SharedPreferencesUtil.save(key, "$openState")
                        }
                ) {
                    Spacer(modifier = Modifier.size(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Spacer(modifier = Modifier.size(16.dp))
                        Image(
                            painter = painterResource(id = getImageId(entry.key.toString())),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = entry.key.toString(),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                }
                AnimatedVisibility(visible = openState) {
                    ClassNotesView(entry.value)
                }
            }
        }
    }

    @Composable
    private fun ClassNotesView(list: ArrayList<NoteItemBean>) {
        Column {
            list.onEach {
                ItemTitleView(noteItemBean = it)
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }

    @Composable
    private fun ItemTitleView(noteItemBean: NoteItemBean) {
        val noteBean = noteItemBean.noteBean

        val title = if (noteBean.title == null || noteBean.title == "") {
            getString(R.string.no_title)
        } else {
            noteBean.title ?: ""
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    startActivityForResult(
                        Intent(requireContext(), WriteNoteActivity::class.java).apply {
                            putExtra("lang", noteBean.language)
                            putExtra("id", noteBean.id)
                        },
                        NotesFragment.NOTE_REQ_CODE
                    )
                }
        ) {
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Spacer(modifier = Modifier.size(16.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_note),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodySmall,
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
        }
    }

    @DrawableRes
    private fun getImageId(lang: String): Int {
        return when (lang) {
            getString(R.string.lang_java) -> R.drawable.ic_java
            getString(R.string.lang_kt) -> R.drawable.ic_kotlin
            getString(R.string.lang_tie_lang) -> R.mipmap.ic_tie_code
            getString(R.string.lang_iyu) -> R.mipmap.ic_iapp
            getString(R.string.lang_xml) -> R.drawable.ic_xml
            getString(R.string.lang_md) -> R.drawable.ic_md
            getString(R.string.lang_html) -> R.drawable.ic_html
            getString(R.string.lang_py) -> R.drawable.ic_python
            getString(R.string.lang_lua) -> R.drawable.ic_lua
            getString(R.string.lang_cpp) -> R.drawable.ic_cpp
            getString(R.string.lang_js) -> R.drawable.ic_java_script
            else -> R.drawable.ic_java
        }
    }

    private fun queryData() {
        DataBase.noteDataBase.noteDao().queryAllNote()?.let {
            val list = ArrayList<NoteItemBean>()
            it.forEach { item ->
                list.add(
                    NoteItemBean(
                        false, item
                    )
                )
            }
            viewModel.setValues(
                list = list
            )
        }
    }

    private val handler = Handler(Looper.getMainLooper())

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        handler.postDelayed(
            this::queryData, 500
        )
    }

}