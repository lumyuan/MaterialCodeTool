package ly.android.material.code.tool.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.entity.ParamType
import ly.android.material.code.tool.data.entity.RequestParamBean
import ly.android.material.code.tool.databinding.ItemRequestParamsBinding
import ly.android.material.code.tool.ui.view.setOnFeedbackListener

class RequestParamsAdapter(
    private val list: ArrayList<RequestParamBean> = ArrayList<RequestParamBean>().apply {
        add(
            RequestParamBean(
                isChecked = false,
                type = ParamType.ADD
            )
        )
    },
    private val title: String
): Adapter<RequestParamsAdapter.MyHolder>() {

    class MyHolder(val binding: ItemRequestParamsBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemRequestParamsBinding.bind(
                View.inflate(parent.context, R.layout.item_request_params, null)
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val binding = holder.binding
        val paramBean = list[position]

        binding.root.tag = position

        when (paramBean.type) {
            ParamType.ADD -> {
                binding.addView.visibility = View.VISIBLE
                binding.paramLayout.visibility = View.GONE
                binding.addTitle.text = binding.root.context.getString(R.string.add) + title
            }
            ParamType.PARAM -> {

                binding.title.text = "${this.title}${position + 1}"

                binding.addView.visibility = View.GONE
                binding.paramLayout.visibility = View.VISIBLE

                binding.checkBox.isChecked = paramBean.isChecked
                binding.key.setText(paramBean.key)
                binding.value.setText(paramBean.value)

                binding.checkBox.setOnClickListener {
                    paramBean.isChecked = binding.checkBox.isChecked
                }

                binding.key.addTextChangedListener {
                    if (binding.key.hasFocus() && binding.root.tag == position){
                        paramBean.key = it.toString()
                    }
                }

                binding.value.addTextChangedListener {
                    if (binding.value.hasFocus() && binding.root.tag == position){
                        paramBean.value = it.toString()
                    }
                }

                binding.remove.setOnFeedbackListener {
                    remove(position)
                }

            }
        }

        binding.root.setOnClickListener {
            when (paramBean.type) {
                ParamType.ADD -> {
                    add(
                        RequestParamBean(
                            isChecked = true,
                            type = ParamType.PARAM
                        )
                    )
                }
                ParamType.PARAM -> {}
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(requestParamBean: RequestParamBean){
        this.list.add(itemCount - 1, requestParamBean)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun remove(position: Int){
        this.list.removeAt(position)
        notifyDataSetChanged()
    }

    fun getParams(): ArrayList<RequestParamBean> {
        val arrayList = ArrayList<RequestParamBean>()
        this.list.onEach {
            if (it.type == ParamType.PARAM && it.isChecked) {
                arrayList.add(it)
            }
        }
        return arrayList
    }

    fun getList(): ArrayList<RequestParamBean> = ArrayList<RequestParamBean>().apply {
        addAll(list)
    }
}