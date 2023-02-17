package ly.android.material.code.tool.ui.adapter

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.fragments.notes.NotesFragment
import ly.android.material.code.tool.activities.notes.WriteNoteActivity
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.common.dip2px
import ly.android.material.code.tool.data.MainViewModel
import ly.android.material.code.tool.data.entity.NoteBean
import ly.android.material.code.tool.data.entity.NoteItemBean
import ly.android.material.code.tool.databinding.ItemNotesBinding
import ly.android.material.code.tool.ui.common.ItemTouchHelperAdapter
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotesListAdapter(
    private val viewModel: MainViewModel, private val activity: NotesFragment
) : Adapter<NotesListAdapter.MyHolder>(), ItemTouchHelperAdapter {

    private val list = ArrayList<NoteItemBean>()

    class MyHolder(
        val binding: ItemNotesBinding
    ) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemNotesBinding.bind(
                View.inflate(parent.context, R.layout.item_notes, null)
            )
        )
    }

    override fun getItemCount(): Int {
        return this.list.size
    }

    @SuppressLint("SimpleDateFormat")
    private val dateFormat =
        SimpleDateFormat(MaterialCodeToolApplication.application.getString(R.string.date_format))
    private val random = Random()
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val binding = holder.binding
        val noteBean = list[position].noteBean

        binding.title.text = noteBean.title
        binding.content.text = noteBean.content
        binding.date.text = dateFormat.format(noteBean.createDate)
        binding.langIcon.apply {
            setImageResource(
                when (noteBean.language) {
                    context.getString(R.string.lang_java) -> R.drawable.ic_java
                    context.getString(R.string.lang_kt) -> R.drawable.ic_kotlin
                    context.getString(R.string.lang_tie_lang) -> R.mipmap.ic_tie_code
                    context.getString(R.string.lang_iyu) -> R.mipmap.ic_iapp
                    context.getString(R.string.lang_xml) -> R.drawable.ic_xml
                    context.getString(R.string.lang_md) -> R.drawable.ic_md
                    context.getString(R.string.lang_html) -> R.drawable.ic_html
                    context.getString(R.string.lang_py) -> R.drawable.ic_python
                    context.getString(R.string.lang_lua) -> R.drawable.ic_lua
                    context.getString(R.string.lang_cpp) -> R.drawable.ic_cpp
                    context.getString(R.string.lang_js) -> R.drawable.ic_java_script
                    else -> R.drawable.ic_java
                }
            )
        }

        binding.radioButton.visibility = if (viewModel.noteLangClickState.value!!) {
            View.VISIBLE
        } else {
            View.GONE
        }

        binding.cardView.setOnFeedbackListener(
            clickable = true,
            callOnLongClick = true,
            click = {
                val state = viewModel.noteLangClickState.value!!
                if (state) {
                    val ic = !binding.radioButton.isChecked
                    binding.radioButton.isChecked = ic
                    list[position] = NoteItemBean(
                        binding.radioButton.isChecked,
                        noteBean
                    )
                    changeState()
                    checkAllState()
                } else {
                    activity.startActivityForResult(
                        Intent(it.context, WriteNoteActivity::class.java).apply {
                            putExtra("lang", noteBean.language)
                            putExtra("id", noteBean.id)
                        },
                        NotesFragment.NOTE_REQ_CODE
                    )
                }
            },
            onLongClick = {
                val state = viewModel.noteLangClickState.value!!
                if (!state) {
                    viewModel.noteLangClickState.value = true
                }
                val ic = !binding.radioButton.isChecked
                binding.radioButton.isChecked = ic
                list[position] = NoteItemBean(
                    binding.radioButton.isChecked,
                    noteBean
                )
                checkAllState()
            }
        )
        binding.radioButton.isChecked = list[position].isChecked
    }

    private fun checkAllState() {
        viewModel.isCheckAll.value = isCheckAll()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<NoteItemBean>) {
        this.list.clear()
        notifyDataSetChanged()
        for (index in list.indices) {
            this.list.add(list[index])
            notifyItemInserted(index)
            notifyItemRangeInserted(index - 1, 2)
        }
    }

    fun changeState() {
        for (index in this.list.indices) {
            notifyItemChanged(index)
        }
    }

    fun checkAll(checked: Boolean) {
        for (index in this.list.indices) {
            this.list[index].isChecked = checked
            notifyItemChanged(index)
        }
    }

    fun getCheckedNote(): List<NoteBean> {
        val noteBeans = ArrayList<NoteBean>()
        this.list.forEach {
            if (it.isChecked) {
                noteBeans.add(it.noteBean)
            }
        }
        return noteBeans
    }

    fun getList(): List<NoteItemBean> = this.list

    fun removeCheckedUI(){
        for (index in itemCount - 1 downTo  0) {
            if (this.list[index].isChecked) {
                this.list.removeAt(index)
                notifyItemRemoved(index)
                notifyItemRangeRemoved(index, itemCount - index)
            }
        }
    }

    fun isCheckAll(): Boolean {
        this.list.forEach {
            if (!it.isChecked) {
                return false
            }
        }
        return true
    }

    override fun onItemMove(source: ViewHolder?, target: ViewHolder?) {
        val fromPosition = source!!.adapterPosition
        //拿到当前拖拽到的item的viewHolder
        val toPosition = target!!.adapterPosition
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(list, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(list, i, i - 1)
            }
        }
        val fromBean = list[fromPosition]
        val toBean = list[toPosition]
        rankItem(fromBean.noteBean, toBean.noteBean)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(source: ViewHolder?) {
        val position = source!!.adapterPosition
        list.removeAt(position) //移除数据
        notifyItemRemoved(position) //刷新数据移除
    }

    override fun onItemSelect(source: ViewHolder?) {
        val binding = (source as MyHolder).binding

        val translationZ = ObjectAnimator.ofFloat(
            binding.cardView,
            "translationZ",
            binding.root.context.dip2px(4f) * 1f
        )
        translationZ.duration = 200
        translationZ.start()

        val scaleX = ObjectAnimator.ofFloat(binding.cardView, "scaleX", 0.8f)
        scaleX.duration = 200
        scaleX.start()

        val scaleY = ObjectAnimator.ofFloat(binding.cardView, "scaleY", 0.8f)
        scaleY.duration = 200
        scaleY.start()
    }

    override fun onItemClear(source: ViewHolder?) {
        val binding = (source as MyHolder).binding

        val translationZ = ObjectAnimator.ofFloat(
            binding.cardView,
            "translationZ",
            0f
        )
        translationZ.duration = 200
        translationZ.start()

        val scaleX = ObjectAnimator.ofFloat(binding.cardView, "scaleX", 1f)
        scaleX.duration = 200
        scaleX.start()

        val scaleY = ObjectAnimator.ofFloat(binding.cardView, "scaleY", 1f)
        scaleY.duration = 200
        scaleY.start()
    }

    private fun rankItem(fromBean: NoteBean, toBean: NoteBean) {
        val rank = fromBean.rank
        fromBean.rank = toBean.rank
        toBean.rank = rank
        val noteDao = DataBase.noteDataBase.noteDao()
        noteDao.updateNote(fromBean)
        noteDao.updateNote(toBean)
    }
}