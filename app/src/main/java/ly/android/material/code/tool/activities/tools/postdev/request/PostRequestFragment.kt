package ly.android.material.code.tool.activities.tools.postdev.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.Tab
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.replaceFragment
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.databinding.FragmentPostRequestBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class PostRequestFragment : BaseFragment() {

    private val binding by bind(FragmentPostRequestBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PostRequestFragment()
    }

    private val fragments by lazy {
        arrayOf(
            ParamsFragment.newInstance(),
            HeadersFragment.newInstance(),
            BodyFragment.newInstance()
        )
    }

    private val tabs by lazy {
        arrayOf(
            binding.paramsTabLayout.newTab().apply {
                setText(R.string.params)
            },
            binding.paramsTabLayout.newTab().apply {
                setText(R.string.headers)
            },
            binding.paramsTabLayout.newTab().apply {
                setText(R.string.request_body)
            }
        )
    }

    private val viewModel by activityViewModels<PostDevViewModel>()

    override fun initView(root: View) {
        super.initView(root)

        binding.paramsTabLayout.apply {

            tabs.forEach {
                addTab(it)
            }

            addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: Tab) {
                    val tabPosition = getTabPosition(tab)
                    val state = viewModel.requestViewNavigationState.value
                    if (tabPosition != state){
                        viewModel.requestViewNavigationState.value = tabPosition
                    }
                }

                override fun onTabUnselected(tab: Tab) {

                }

                override fun onTabReselected(tab: Tab) {

                }

            })
        }

        viewModel.requestViewNavigationState.observe(this) {
            replaceFragment(
                binding.requestFrame.id,
                fragments[it]
            )
            binding.paramsTabLayout.selectTab(tabs[it])
        }
    }

    private fun getTabPosition(tab: Tab): Int {
        val tabCount = binding.paramsTabLayout.tabCount
        for (i in 0 until tabCount) {
            if (binding.paramsTabLayout.getTabAt(i) == tab) {
                return i
            }
        }
        return 0
    }
}