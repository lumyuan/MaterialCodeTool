package ly.android.material.code.tool.activities.fragments.notes

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
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
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.notes.WriteNoteActivity
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.data.MainViewModel
import ly.android.material.code.tool.data.NotesViewModel
import ly.android.material.code.tool.data.entity.ChooseLangBean
import ly.android.material.code.tool.data.entity.NoteItemBean
import ly.android.material.code.tool.databinding.FragmentNotesBinding
import ly.android.material.code.tool.ui.adapter.NotesListAdapter
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.SimpleItemTouchHelperCallback
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.theme.MaterialCodeToolTheme
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import ly.android.material.code.tool.util.ToastUtils

class NotesFragment : BaseFragment() {

    private val binding by bind(FragmentNotesBinding::inflate)
    private val viewModel by viewModels<NotesViewModel>()
    private val activityViewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        const val NOTE_REQ_CODE = 202302

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
            this@NotesFragment.adapter = NotesListAdapter(activityViewModel, this@NotesFragment)
            adapter = this@NotesFragment.adapter

            val callback: ItemTouchHelper.Callback =
                SimpleItemTouchHelperCallback(this@NotesFragment.adapter)
            val touchHelper = ItemTouchHelper(callback)
            touchHelper.attachToRecyclerView(this)
        }

        binding.addButton.setOnFeedbackListener {
            viewModel.addDialogState.value = true
        }

        viewModel.list.observe(this) {
            if (activityViewModel.noteLangClickState.value == false) {
                //TODO

            }
            adapter.updateList(it)
            initAnimate(requireContext())
            binding.noData.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        activityViewModel.noteLangClickState.observe(this) {
            if (it) {
                binding.addButton.visibility = View.GONE
            } else {
                binding.addButton.visibility = View.VISIBLE
                adapter.checkAll(false)
            }
            adapter.changeState()
        }

        activityViewModel.checkAllBoxState.observe(this){
            adapter.checkAll(it)
        }

        activityViewModel.removeCheckedState.value = 0
        activityViewModel.removeCheckedState.observe(this) {
            val checkedNote = adapter.getCheckedNote()
            if (it != 0L){
                if (checkedNote.isEmpty()){
                    ToastUtils.toast(R.string.no_checked)
                }else {
                    MaterialAlertDialogBuilder(requireContext()).apply {
                        this.setTitle(R.string.toast)
                        this.setMessage(R.string.arrow_remove)
                        this.setPositiveButton(R.string.delete){_,_ ->
                            adapter.removeCheckedUI()
                            checkedNote.forEach {bean ->
                                DataBase.noteDataBase.noteDao().deleteNote(bean)
                            }
                            ToastUtils.toast(R.string.delete_finished)
                            activityViewModel.noteLangClickState.value = false
                            viewModel.setValues(adapter.getList())
                        }
                        this.setNegativeButton(R.string.cancel, null)
                    }.show()
                }
            }
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

    override fun loadSingleData() {
        super.loadSingleData()
        queryData()
    }

    private fun queryData(){
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

    private fun initAnimate(context: Context) {
        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.animate_list)
        val layoutAnimationController = LayoutAnimationController(animation)
        layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
        layoutAnimationController.delay = 0.2f
        binding.listView.layoutAnimation = layoutAnimationController
    }

    @Composable
    private fun AddNoteDialog() {
        val state = viewModel.addDialogState.observeAsState()
        if (state.value == true) {
            Dialog(
                onDismissRequest = { viewModel.addDialogState.value = false },
                content = {
                    DialogContentView {
                        startActivityForResult(
                            Intent(requireActivity(), WriteNoteActivity::class.java).apply {
                                putExtra("lang", it)
                            },
                            NOTE_REQ_CODE
                        )
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
    ) {
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
            ) {
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
    private fun DefaultPreview() {
        MaterialCodeToolTheme {
            DialogContentView()
        }
    }
}