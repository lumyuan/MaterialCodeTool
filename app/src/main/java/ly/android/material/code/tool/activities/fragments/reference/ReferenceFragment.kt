package ly.android.material.code.tool.activities.fragments.reference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.entity.ReferenceFileType
import ly.android.material.code.tool.data.entity.ReferenceIntent
import ly.android.material.code.tool.data.enums.ReferenceLanguage
import ly.android.material.code.tool.data.enums.UrlType
import ly.android.material.code.tool.databinding.FragmentReferenceBinding
import ly.android.material.code.tool.ui.adapter.PagerAdapterForFragment
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class ReferenceFragment : BaseFragment() {

    private val binding by bind(FragmentReferenceBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ReferenceFragment()
    }

    private val pageList: ArrayList<PagerAdapterForFragment.Page> = ArrayList()
    override fun initView(root: View) {
        super.initView(root)

        MaterialCodeToolApplication.setting?.apply {
            this.homePages.onEach {
                if (it.isShow){
                    it.fragmentBean?.also { fragmentBean ->
                        if (fragmentBean.url == null){
                            pageList.add(
                                PagerAdapterForFragment.Page(
                                    ReferenceItemFragment.newInstance(
                                        ReferenceIntent(
                                            title = fragmentBean.title.toString(),
                                            lang = fragmentBean.lang!!,
                                            urlType = fragmentBean.urlType!!,
                                            referenceFileType = fragmentBean.referenceFileType!!
                                        )
                                    ),
                                    title = it.title
                                )
                            )
                        }else {
                            pageList.add(
                                PagerAdapterForFragment.Page(
                                    fragment = AndroidDeveloperFragment.newInstance(fragmentBean.url.toString()),
                                    title = it.title
                                )
                            )
                        }
                    }
                }
            }
        }

        binding.viewpager.apply {
            adapter = PagerAdapterForFragment(
                pageList.toArray(arrayOf<PagerAdapterForFragment.Page>()),
                childFragmentManager
            )
            offscreenPageLimit = if (MaterialCodeToolApplication.highPerformanceMode){
                pageList.size
            }else {
                2
            }
        }

        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }
}