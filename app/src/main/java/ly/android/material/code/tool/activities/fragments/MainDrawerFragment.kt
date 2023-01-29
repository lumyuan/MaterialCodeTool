package ly.android.material.code.tool.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.MainViewModel
import ly.android.material.code.tool.databinding.FragmentMainDrawerBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class MainDrawerFragment : BaseFragment() {

    private val binding by bind(FragmentMainDrawerBinding::inflate)
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainDrawerFragment()
    }

    override fun initView(root: View) {
        super.initView(root)

        binding.navigationView.setNavigationItemSelectedListener {
            viewModel.setDrawerState(false)
            viewModel.setCurrent(
                when (it.itemId) {
                    R.id.nav_reference-> {
                        0
                    }
                    R.id.nav_tools -> {
                        1
                    }
                    else -> 0
                }
            )
            true
        }

        viewModel.pageCurrent.observe(this){
            binding.navigationView.setCheckedItem(
                when (it) {
                    0 -> {
                        R.id.nav_reference
                    }
                    1 -> {
                        R.id.nav_tools
                    }
                    else -> {
                        R.id.nav_reference
                    }
                }
            )
        }
    }
}