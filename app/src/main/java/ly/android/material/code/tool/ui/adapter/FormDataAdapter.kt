package ly.android.material.code.tool.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.lxj.xpopup.XPopup
import ly.android.io.util.FileUtil
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.entity.BodyParamType
import ly.android.material.code.tool.data.entity.FormDataBean
import ly.android.material.code.tool.data.entity.ParamType
import ly.android.material.code.tool.data.entity.RequestParamBean
import ly.android.material.code.tool.databinding.ItemRequestBodyFormDataBinding
import ly.android.material.code.tool.ui.theme.MyTheme
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import java.text.SimpleDateFormat


class FormDataAdapter(
    private val fragment: Fragment,
    private val list: ArrayList<FormDataBean> = ArrayList<FormDataBean>().apply {
        add(
            FormDataBean(
                isChecked = false,
                type = ParamType.ADD,
                bodyType = BodyParamType.TEXT
            )
        )
    }
): Adapter<FormDataAdapter.MyHolder>() {

    class MyHolder(val binding: ItemRequestBodyFormDataBinding)
        : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        return MyHolder(
            ItemRequestBodyFormDataBinding.bind(
                View.inflate(parent.context, R.layout.item_request_body_form_data, null)
            )
        )
    }

    override fun getItemCount(): Int = list.size
    private val bodyParamTypes = BodyParamType.values()
    private val bodyParamTypeStrings by lazy {
        val arrayOfNulls = arrayOfNulls<String>(bodyParamTypes.size)
        for (index in bodyParamTypes.indices) {
            arrayOfNulls[index] = bodyParamTypes[index].toString()
        }
        arrayOfNulls
    }

    @SuppressLint("SimpleDateFormat")
    private val dataFormat = SimpleDateFormat(MaterialCodeToolApplication.application.getString(R.string.date_format))
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val binding = holder.binding
        val formDataBean = list[position]

        binding.root.tag = position

        when (formDataBean.type) {
            ParamType.ADD -> {
                binding.addView.visibility = View.VISIBLE
                binding.paramLayout.visibility = View.GONE
                binding.addTitle.text = binding.root.context.getString(R.string.add) + binding.root.context.getString(R.string.params)
            }
            ParamType.PARAM -> {

                binding.addView.visibility = View.GONE
                binding.paramLayout.visibility = View.VISIBLE

                binding.checkBox.isChecked = formDataBean.isChecked
                binding.key.setText(formDataBean.key)
                binding.value.setText(formDataBean.value)

                when (formDataBean.bodyType) {
                    BodyParamType.TEXT -> {
                        binding.chooseFileLayout.visibility = View.GONE
                        binding.paramTextLayout.visibility = View.VISIBLE
                        binding.fileInfo.visibility = View.GONE
                    }
                    BodyParamType.FILE -> {
                        binding.paramTextLayout.visibility = View.GONE
                        if (formDataBean.file != null){
                            binding.fileInfo.visibility = View.VISIBLE
                            val documentFile = DocumentFile.fromSingleUri(binding.root.context, Uri.parse(formDataBean.file))
                            documentFile?.apply {
                                val fileInfo = binding.root.context.getString(R.string.file_info)
                                val format = String.format(
                                    fileInfo,
                                    name,
                                    FileUtil.readableFileSize(length()),
                                    dataFormat.format(lastModified())
                                )
                                binding.fileInfo.text = format
                            }
                            binding.chooseFileLayout.visibility = View.VISIBLE
                        }else {
                            binding.fileInfo.visibility = View.GONE
                        }
                    }
                }

                binding.type.text = formDataBean.bodyType.toString()
                binding.type.setOnFeedbackListener {
                    XPopup.Builder(it.context)
                        .hasShadowBg(false)
                        .isCoverSoftInput(true)
                        .atView(it)
                        .isDarkTheme(MyTheme.isDarkMode())
                        .asAttachList(
                            bodyParamTypeStrings, null
                        ) { i, text ->
                            binding.type.text = text
                            formDataBean.bodyType = bodyParamTypes[i]
                            notifyItemChanged(position)
                        }.show()
                }

                binding.checkBox.setOnClickListener {
                    formDataBean.isChecked = binding.checkBox.isChecked
                }

                binding.key.addTextChangedListener {
                    if (binding.key.hasFocus() && binding.root.tag == position){
                        formDataBean.key = it.toString()
                    }
                }

                binding.value.addTextChangedListener {
                    if (binding.value.hasFocus() && binding.root.tag == position){
                        formDataBean.value = it.toString()
                    }
                }

                binding.remove.setOnFeedbackListener {
                    remove(position)
                }

                binding.chooseFile.setOnFeedbackListener {
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "*/*"
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    fragment.startActivityForResult(intent, 100 * (position + 1))
                }
            }
        }

        binding.root.setOnClickListener {
            when (formDataBean.type) {
                ParamType.ADD -> {
                    add(
                        FormDataBean(
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
    fun add(formDataBean: FormDataBean){
        this.list.add(itemCount - 1, formDataBean)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun remove(position: Int){
        this.list.removeAt(position)
        notifyDataSetChanged()
    }

    fun getParams(): ArrayList<FormDataBean>{
        val arrayList = ArrayList<FormDataBean>()
        this.list.onEach {
            if (it.type == ParamType.PARAM && it.isChecked) {
                arrayList.add(it)
            }
        }
        return arrayList
    }

    fun getList(): ArrayList<FormDataBean> = ArrayList(this.list)

}