package ly.android.material.code.tool.activities.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.fragments.notes.NotesFragment
import ly.android.material.code.tool.activities.fragments.reference.ReferenceFragment
import ly.android.material.code.tool.activities.fragments.toos.ToolsFragment
import ly.android.material.code.tool.data.MainViewModel
import ly.android.material.code.tool.databinding.FragmentMainBinding
import ly.android.material.code.tool.ui.adapter.PagerAdapterForFragment
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class MainFragment : BaseFragment() {

    private val binding by bind(FragmentMainBinding::inflate)
    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainFragment()
    }

    private lateinit var pages: Array<PagerAdapterForFragment.Page>
    override fun initView(root: View) {
        super.initView(root)
        pages = arrayOf(
            PagerAdapterForFragment.Page(
                fragment = ReferenceFragment.newInstance(),
                title = activity?.getString(R.string.reference)
            ),
            PagerAdapterForFragment.Page(
                fragment = ToolsFragment.newInstance(),
                title = activity?.getString(R.string.tools)
            ),
            PagerAdapterForFragment.Page(
                fragment = NotesFragment.newInstance(),
                title = activity?.getString(R.string.notes)
            )
        )
        binding.viewpager.apply {
            adapter = PagerAdapterForFragment(
                pages, childFragmentManager
            )
            offscreenPageLimit = if (MaterialCodeToolApplication.highPerformanceMode){
                pages.size
            }else {
                1
            }
        }

        binding.viewpager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                viewModel.setCurrent(position)
            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })

        viewModel.pageCurrent.observe(this){
            it?.let {
                binding.viewpager.currentItem = it
            }
        }

        viewModel.noteLangClickState.observe(this){
            binding.viewpager.setScroll(it)
        }
    }

}