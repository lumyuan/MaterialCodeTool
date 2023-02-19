package ly.android.material.code.tool.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.gson.Gson
import io.noties.markwon.Markwon
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.image.glide.GlideImagesPlugin
import io.noties.markwon.linkify.LinkifyPlugin
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.reference.ReferenceActivity
import ly.android.material.code.tool.data.entity.ReferenceBean
import ly.android.material.code.tool.data.enums.ReferenceLanguage
import ly.android.material.code.tool.databinding.ItemReferenceAssetsBinding
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import java.util.Random

class ReferenceListAdapter(
    private val language: ReferenceLanguage
): Adapter<ReferenceListAdapter.MyHolder>() {

    class MyHolder(val binding: ItemReferenceAssetsBinding) : ViewHolder(binding.root)

    private val gson = Gson()
    private val list: ArrayList<ReferenceBean> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemReferenceAssetsBinding.bind(
                View.inflate(
                    parent.context, R.layout.item_reference_assets, null
                )
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private val random = Random()
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val binding = holder.binding
        val referenceBean = list[position]
        referenceBean.title?.let {
            if (it.length > 1){
                binding.avatarLabel.text = it.substring(0, 1).uppercase()
            }
        }
        binding.title.text = referenceBean.title
        val context = binding.root.context
        val markdown = Markwon.builder(context)
            .usePlugin(GlideImagesPlugin.create(context))
            .usePlugin(HtmlPlugin.create())
            .usePlugin(LinkifyPlugin.create(true))
            .build()
        markdown.setMarkdown(binding.content, referenceBean.content.toString())
        binding.content.maxLines = random.nextInt(3) + 5
        binding.action.setOnFeedbackListener {
            val intent = Intent(it.context, ReferenceActivity::class.java).apply {
                putExtra("lang", language.toString())
                putExtra("reference", gson.toJson(referenceBean))
            }
            it.context.startActivity(intent)
        }
        if (position >= itemCount - 2){
            binding.perchView.visibility = View.VISIBLE
        }else {
            binding.perchView.visibility = View.GONE
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<ReferenceBean>){
        this.list.clear()
        notifyDataSetChanged()
        for (index in list.indices) {
            this.list.add(list[index])
            notifyItemInserted(index)
            notifyItemChanged(index - 1)
        }
    }
}