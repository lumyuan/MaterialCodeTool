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
import ly.android.material.code.tool.activities.notes.WriteNoteActivity
import ly.android.material.code.tool.common.DataBase
import ly.android.material.code.tool.common.dip2px
import ly.android.material.code.tool.data.entity.NoteBean
import ly.android.material.code.tool.databinding.ItemNotesBinding
import ly.android.material.code.tool.ui.common.ItemTouchHelperAdapter
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import java.text.SimpleDateFormat
import java.util.*

class NotesListAdapter : Adapter<NotesListAdapter.MyHolder>(), ItemTouchHelperAdapter {

    private val list = ArrayList<NoteBean>()

    class MyHolder(
        val binding: ItemNotesBinding
    ): ViewHolder(binding.root)

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
    private val dateFormat = SimpleDateFormat(MaterialCodeToolApplication.application.getString(R.string.date_format))
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val binding = holder.binding
        val noteBean = list[position]

        binding.title.text = noteBean.title
        binding.content.text = noteBean.content
        binding.date.text = dateFormat.format(noteBean.createDate)
        binding.langIcon.apply {
            setImageResource(
                when(noteBean.language){
                    context.getString(R.string.lang_java) -> R.drawable.ic_java
                    context.getString(R.string.lang_kt) -> R.drawable.ic_kotlin
                    context.getString(R.string.lang_tie_lang) -> R.mipmap.ic_tie_code
                    else -> R.drawable.ic_java
                }
            )
        }

        binding.cardView.setOnFeedbackListener(
            clickable = true,
            callOnLongClick = true,
            click = {
                val intent = Intent(it.context, WriteNoteActivity::class.java).apply {
                    putExtra("lang", noteBean.language)
                    putExtra("id", noteBean.id)
                }
                it.context.startActivity(intent)
            },
            onLongClick = {
                binding.radioButton.isChecked = !binding.radioButton.isChecked
            }
        )
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: List<NoteBean>){
        this.list.clear()
        notifyDataSetChanged()
        for (index in list.indices) {
            this.list.add(list[index])
            notifyItemInserted(index)
            notifyItemRangeInserted(index - 1, 2)
        }
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
        rankItem(fromBean, toBean)
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(source: ViewHolder?) {
        val position = source!!.adapterPosition
        list.removeAt(position) //移除数据
        notifyItemRemoved(position) //刷新数据移除
    }

    override fun onItemSelect(source: ViewHolder?) {
        val binding = (source as MyHolder).binding

        binding.root.postDelayed(
            {
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
            },
        500)
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

    private fun rankItem(fromBean: NoteBean, toBean: NoteBean){
        val rank = fromBean.rank
        fromBean.rank = toBean.rank
        toBean.rank = rank
        val noteDao = DataBase.noteDataBase.noteDao()
        noteDao.updateNote(fromBean)
        noteDao.updateNote(toBean)
    }
}