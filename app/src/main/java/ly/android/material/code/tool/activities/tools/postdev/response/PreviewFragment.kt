package ly.android.material.code.tool.activities.tools.postdev.response

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.databinding.FragmentPreviewBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class PreviewFragment : BaseFragment() {

    private val binding by bind(FragmentPreviewBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PreviewFragment()
    }

    private val viewModel by activityViewModels<PostDevViewModel>()
    override fun initView(root: View) {
        super.initView(root)
        viewModel.bodyData.observe(this) {
            it?.let {
                binding.webView.loadDataWithBaseURL(null, it, "text/html", "utf-8", null)
            }
        }
    }
}