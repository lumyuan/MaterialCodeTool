package ly.android.material.code.tool.activities.tools.postdev.request.body

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.databinding.FragmentFormUrlBinding
import ly.android.material.code.tool.ui.adapter.RequestParamsAdapter
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class FormUrlFragment : BaseFragment() {

    private val binding by bind(FragmentFormUrlBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = FormUrlFragment()
    }

    private val viewModel by activityViewModels<PostDevViewModel>()
    private lateinit var adapter : RequestParamsAdapter
    override fun initView(root: View) {
        super.initView(root)
        adapter = if (viewModel.bodyFormUrl.value == null){
            RequestParamsAdapter(title = getString(R.string.params))
        }else {
            RequestParamsAdapter(
                title = getString(R.string.params),
                list = viewModel.bodyFormUrl.value!!
            )
        }

        binding.list.apply {
            layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
            adapter = this@FormUrlFragment.adapter
        }

        viewModel.sendState.observe(this) {
            viewModel.bodyFormUrl.value = adapter.getList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.sendState.value = System.currentTimeMillis()
    }
}