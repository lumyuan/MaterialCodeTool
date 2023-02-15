package ly.android.material.code.tool.activities.tools.postdev.request.body

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import io.reactivex.rxjava3.core.Observable
import ly.android.io.common.IOUtils
import ly.android.io.util.FileUtil
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.databinding.FragmentFormDataBinding
import ly.android.material.code.tool.ui.adapter.FormDataAdapter
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.util.ToastUtils
import java.text.SimpleDateFormat

class FormDataFragment : BaseFragment() {

    private val binding by bind(FragmentFormDataBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = FormDataFragment()
    }

    private lateinit var adapter: FormDataAdapter
    private val viewModel by activityViewModels<PostDevViewModel>()
    override fun initView(root: View) {
        super.initView(root)
        adapter = if (viewModel.bodyFormData.value == null){
            FormDataAdapter(this)
        }else {
            FormDataAdapter(this, viewModel.bodyFormData.value!!)
        }

        binding.list.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@FormDataFragment.adapter
        }

        viewModel.sendState.observe(this) {
            viewModel.bodyFormData.value = adapter.getList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.sendState.value = System.currentTimeMillis()
    }

    @SuppressLint("SimpleDateFormat")
    private val dataFormat = SimpleDateFormat(MaterialCodeToolApplication.application.getString(R.string.date_format))
    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) return
        val uri = data?.data ?: return
        val documentFile = DocumentFile.fromSingleUri(requireContext(), uri)
        val position = requestCode / 100 - 1
        documentFile?.apply {
            adapter.getParams()[position].file = this.uri.toString()
            adapter.notifyItemChanged(position)
        }
    }
}