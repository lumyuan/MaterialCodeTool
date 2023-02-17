package ly.android.material.code.tool.activities.tools.postdev.request.body

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toFile
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.activityViewModels
import com.lxj.xpopup.XPopup
import com.lxj.xpopup.impl.LoadingPopupView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import ly.android.io.common.IOUtils
import ly.android.io.util.FileUtil
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.data.entity.FormDataBean
import ly.android.material.code.tool.data.entity.ParamType
import ly.android.material.code.tool.databinding.FragmentBinaryBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import ly.android.material.code.tool.util.ToastUtils
import java.text.SimpleDateFormat

class BinaryFragment : BaseFragment() {

    private val binding by bind(FragmentBinaryBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BinaryFragment()
    }

    private val viewModel by activityViewModels<PostDevViewModel>()
    override fun initView(root: View) {
        super.initView(root)

        binding.chooseFile.setOnFeedbackListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, 100)
        }

        binding.remove.setOnClickListener {
            viewModel.binaryData.value = null
        }

        viewModel.binaryData.observe(this) {
            if (it?.file == null) {
                binding.fileInfo.visibility = View.GONE
                binding.remove.visibility = View.GONE
            }else {
                val documentFile = DocumentFile.fromSingleUri(requireContext(), Uri.parse(it.file))
                documentFile?.apply {
                    val fileInfo = getString(R.string.file_info)
                    val format = String.format(
                        fileInfo,
                        name,
                        FileUtil.readableFileSize(length()),
                        dataFormat.format(lastModified())
                    )
                    binding.fileInfo.text = format
                    binding.fileInfo.visibility = View.VISIBLE
                    binding.remove.visibility = View.VISIBLE
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private val dataFormat = SimpleDateFormat(MaterialCodeToolApplication.application.getString(R.string.date_format))
    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK || requestCode != 100) return
        val uri = data?.data ?: return

        val documentFile = DocumentFile.fromSingleUri(requireContext(), uri)
        documentFile?.apply {
            viewModel.binaryData.value = FormDataBean(
                isChecked = true,
                file = documentFile.uri.toString(),
                type = ParamType.PARAM
            )
        }
    }
}