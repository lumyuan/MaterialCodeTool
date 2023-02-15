package ly.android.material.code.tool.activities.tools.postdev.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.data.entity.ParamType
import ly.android.material.code.tool.data.entity.RequestParamBean
import ly.android.material.code.tool.databinding.FragmentHeadersBinding
import ly.android.material.code.tool.ui.adapter.RequestParamsAdapter
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.util.AndroidInfo

class HeadersFragment : BaseFragment() {

    private val binding by bind(FragmentHeadersBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = HeadersFragment()
    }

    private lateinit var adapter: RequestParamsAdapter
    private val viewModel by activityViewModels<PostDevViewModel>()
    private val androidInfo by lazy {
        AndroidInfo(requireContext())
    }
    override fun initView(root: View) {
        super.initView(root)
        val titles = getString(R.string.headers)
        adapter = RequestParamsAdapter(viewModel.headers.value!!, titles)
        binding.list.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@HeadersFragment.adapter
        }

        viewModel.sendState.observe(this) {
            viewModel.headers.value = adapter.getList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.sendState.value = System.currentTimeMillis()
    }
}