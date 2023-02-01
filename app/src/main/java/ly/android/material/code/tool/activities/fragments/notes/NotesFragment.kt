package ly.android.material.code.tool.activities.fragments.notes

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.notes.WriteNoteActivity
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.data.NotesViewModel
import ly.android.material.code.tool.data.entity.ChooseLangBean
import ly.android.material.code.tool.databinding.FragmentNotesBinding
import ly.android.material.code.tool.ui.adapter.NotesListAdapter
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.SimpleItemTouchHelperCallback
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.theme.MaterialCodeToolTheme
import ly.android.material.code.tool.ui.view.setOnFeedbackListener

class NotesFragment : BaseFragment() {

    private val binding by bind(FragmentNotesBinding::inflate)
    private val viewModel by viewModels<NotesViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            NotesFragment()

        val languages = arrayOf(
            ChooseLangBean(
                icon = R.mipmap.ic_tie_code,
                title = R.string.lang_tie_lang
            ),
            ChooseLangBean(
                icon = R.mipmap.ic_iapp,
                title = R.string.lang_iyu
            ),
            ChooseLangBean(
                icon = R.drawable.ic_java,
                title = R.string.lang_java
            ),
            ChooseLangBean(
                icon = R.drawable.ic_kotlin,
                title = R.string.lang_kt
            ),
            ChooseLangBean(
                icon = R.drawable.ic_cpp,
                title = R.string.lang_cpp
            ),
            ChooseLangBean(
                icon = R.drawable.ic_html,
                title = R.string.lang_html
            ),
            ChooseLangBean(
                icon = R.drawable.ic_xml,
                title = R.string.lang_xml
            ),
            ChooseLangBean(
                icon = R.drawable.ic_python,
                title = R.string.lang_py
            ),
            ChooseLangBean(
                icon = R.drawable.ic_lua,
                title = R.string.lang_lua
            ),
            ChooseLangBean(
                icon = R.drawable.ic_java_script,
                title = R.string.lang_js
            ),
            ChooseLangBean(
                icon = R.drawable.ic_md,
                title = R.string.lang_md
            )
        )

    }

    private lateinit var adapter: NotesListAdapter
    override fun initView(root: View) {
        super.initView(root)

        binding.composeView.setContent {
            MaterialCodeToolTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AddNoteDialog()
                }
            }
        }

        binding.listView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            this@NotesFragment.adapter = NotesListAdapter()
            adapter = this@NotesFragment.adapter

            val callback: ItemTouchHelper.Callback =
                SimpleItemTouchHelperCallback(this@NotesFragment.adapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(this)
        }

        binding.addButton.setOnFeedbackListener(
            clickable = true
        ) {
            viewModel.addDialogState.value = true
        }

        viewModel.list.observe(this){
            adapter.updateList(it)
            binding.noData.visibility = if (it.isEmpty()){
                View.VISIBLE
            }else {
                View.GONE
            }
        }
    }

    override fun loadData() {
        super.loadData()
        DataBase.noteDataBase.noteDao().queryAllNote()?.let {
            viewModel.setValues(
                it
            )
        }
    }

    @Composable
    private fun AddNoteDialog(){
        val state = viewModel.addDialogState.observeAsState()
        if (state.value == true){
            Dialog(
                onDismissRequest = { viewModel.addDialogState.value = false },
                content = {
                    DialogContentView{
                        val intent = Intent(requireActivity(), WriteNoteActivity::class.java).apply {
                            putExtra("lang", it)
                        }
                        requireActivity().startActivity(intent)
                    }
                }
            )
        }
    }

    @SuppressLint("ResourceType")
    @Composable
    private fun DialogContentView(onClick: (String) -> Unit = {}) = Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(30.dp)
                )
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.choose_note_type),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.size(8.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(
                        languages
                    ) { bean ->
                        LangItem(
                            bean, onClick
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun LangItem(
        langBean: ChooseLangBean,
        onClick: (String) -> Unit
    ){
        val title = stringResource(id = langBean.title)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.addDialogState.value = false
                    onClick(title)
                }
        ) {
            Spacer(modifier = Modifier.size(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(modifier = Modifier.size(16.dp))
                Image(
                    painter = painterResource(id = langBean.icon),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = stringResource(id = langBean.title),
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.size(16.dp))
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun DefaultPreview(){
        MaterialCodeToolTheme {
            DialogContentView()
        }
    }
}