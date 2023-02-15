package ly.android.material.code.tool.activities.tools.postdev.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.databinding.FragmentParamsBinding
import ly.android.material.code.tool.ui.adapter.RequestParamsAdapter
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class ParamsFragment : BaseFragment() {

    private val binding by bind(FragmentParamsBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ParamsFragment()
    }

    private lateinit var adapter: RequestParamsAdapter
    private val viewModel by activityViewModels<PostDevViewModel>()
    override fun initView(root: View) {
        super.initView(root)
        val titles = getString(R.string.params)
        adapter = if (viewModel.params.value == null){
            RequestParamsAdapter(
                title = titles
            )
        }else {
            RequestParamsAdapter(viewModel.params.value!!, title = titles)
        }
        binding.list.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@ParamsFragment.adapter
        }

        viewModel.sendState.observe(this) {
            viewModel.params.value = adapter.getList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.sendState.value = System.currentTimeMillis()
    }
}