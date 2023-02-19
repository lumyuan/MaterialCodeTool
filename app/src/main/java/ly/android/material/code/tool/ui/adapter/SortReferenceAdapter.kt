package ly.android.material.code.tool.ui.adapter

import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.dip2px
import ly.android.material.code.tool.data.entity.HomePageDataBean
import ly.android.material.code.tool.databinding.ItemReferenceSortBinding
import ly.android.material.code.tool.ui.common.ItemTouchHelperAdapter
import java.util.Collections

class SortReferenceAdapter(
    private val list: ArrayList<HomePageDataBean>
): Adapter<SortReferenceAdapter.MyHolder>(), ItemTouchHelperAdapter {

    class MyHolder(val binding: ItemReferenceSortBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemReferenceSortBinding.bind(
                View.inflate(parent.context, R.layout.item_reference_sort, null)
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun getList(): ArrayList<HomePageDataBean> = ArrayList<HomePageDataBean>().apply {
        addAll(list)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val binding = holder.binding
        val pageDataBean = list[position]
        binding.title.text = pageDataBean.title
        binding.checkBox.isChecked = pageDataBean.isShow
        binding.cardView.setOnClickListener {
            binding.checkBox.isChecked = !binding.checkBox.isChecked
            pageDataBean.isShow = binding.checkBox.isChecked
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
            binding.root,
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

}