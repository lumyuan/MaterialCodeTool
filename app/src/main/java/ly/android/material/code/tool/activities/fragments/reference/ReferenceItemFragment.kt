package ly.android.material.code.tool.activities.fragments.reference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ly.android.material.code.tool.databinding.FragmentReferenceItemBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class ReferenceItemFragment : BaseFragment() {

    private val binding by bind(FragmentReferenceItemBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReferenceItemFragment()
    }

    override fun initView(root: View) {
        super.initView(root)

    }

    override fun loadSingleData() {
        super.loadSingleData()
        binding.root.autoRefresh()
    }
}