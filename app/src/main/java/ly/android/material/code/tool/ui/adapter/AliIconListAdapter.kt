package ly.android.material.code.tool.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lxj.xpopup.XPopup
import com.pixplicity.sharp.Sharp
import ly.android.material.code.tool.R
import ly.android.material.code.tool.databinding.ItemAliIconBinding
import ly.android.material.code.tool.net.pojo.response.Icon
import ly.android.material.code.tool.ui.dialogs.AliIconSaveDialog
import ly.android.material.code.tool.ui.view.setOnFeedbackListener

class AliIconListAdapter: Adapter<AliIconListAdapter.MyHolder>() {

    private val list = ArrayList<Icon>()

    class MyHolder(val binding: ItemAliIconBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemAliIconBinding.bind(
                View.inflate(parent.context, R.layout.item_ali_icon, null)
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val icon = list[position]
        val binding = holder.binding
        val loadString = Sharp.loadString(icon.show_svg)

        loadString.into(binding.icon)
        binding.iconName.text = icon.name

        binding.root.setOnFeedbackListener {
            XPopup.Builder(it.context)
                .moveUpToKeyboard(true)
                .hasBlurBg(true)
                .isViewMode(true)
                .isDestroyOnDismiss(true)
                .asCustom(AliIconSaveDialog(it.context, icon))
                .show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(list: List<Icon>){
        for (index in list.indices) {
            this.list.add(list[index])
            notifyItemInserted(itemCount - 1)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clear(){
        this.list.clear()
        notifyDataSetChanged()
    }

}