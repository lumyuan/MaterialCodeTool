package ly.android.material.code.tool.activities.tools.postdev.response

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.tabs.TabLayout
import ly.android.io.util.FileUtil
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.replaceFragment
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.databinding.FragmentPostResponseBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.view.setOnFeedbackListener

class PostResponseFragment : BaseFragment() {

    private val binding by bind(FragmentPostResponseBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = PostResponseFragment()
    }


    private val viewModel by activityViewModels<PostDevViewModel>()
    private val fragments by lazy {
        arrayOf(
            BodyResultFragment.newInstance()
        )
    }
    private val previewFragment by lazy {
        PreviewFragment.newInstance()
    }
    override fun initView(root: View) {
        super.initView(root)
        binding.tabLayout.apply {
            addTab(
                newTab().apply {
                    this.text = getString(R.string.body_result)
//                    this.view.setOnClickListener {
//                        for (i in 0 until tabCount){
//                            if (getTabAt(i) == this) {
//                                viewModel.pageState.value = i
//                            }
//                        }
//                    }
                }
            )
            addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    for (i in 0 until tabCount){
                        if (getTabAt(i) == tab) {
                            viewModel.pageState.value = i
                        }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {

                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    for (i in 0 until tabCount){
                        if (getTabAt(i) == tab) {
                            viewModel.pageState.value = i
                        }
                    }
                }

            })
        }

        viewModel.pageState.observe(this){
            replaceFragment(
                binding.bodyFrame.id,
                fragments[it]
            )
        }

        viewModel.responseState.observe(this) {
            if (it == null){
                binding.responseState.visibility = View.GONE
                replaceFragment(
                    binding.bodyFrame.id,
                    fragments[viewModel.pageState.value ?: 0]
                )
            }else {
                binding.responseState.visibility = View.VISIBLE
                val format = String.format(
                    getString(R.string.response_state),
                    it.code.toString(),
                    it.time.toString(),
                    FileUtil.readableFileSize(
                        it.size.toLong()
                    )
                )
                binding.responseState.text = format
            }
        }

        binding.preview.setOnFeedbackListener {
            replaceFragment(
                binding.bodyFrame.id, previewFragment
            )
        }
    }
}