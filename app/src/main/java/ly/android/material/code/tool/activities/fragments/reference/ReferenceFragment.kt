package ly.android.material.code.tool.activities.fragments.reference

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ly.android.material.code.tool.MaterialCodeToolApplication
import ly.android.material.code.tool.R
import ly.android.material.code.tool.data.entity.ReferenceIntent
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

    private lateinit var pages: Array<PagerAdapterForFragment.Page>
    override fun initView(root: View) {
        super.initView(root)
        pages = arrayOf(
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "",
                        urlType = UrlType.NET
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.tieCode)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "",
                        urlType = UrlType.NET
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.android)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "",
                        urlType = UrlType.NET
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.compose)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "iyu-helpV5.0.txt",
                        urlType = UrlType.ASSETS
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv5)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "iyu-helpV3.0.txt",
                        urlType = UrlType.ASSETS
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv3)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "ijava-helpV3.0.txt",
                        urlType = UrlType.ASSETS
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv3_java)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "ijs-helpV3.0.txt",
                        urlType = UrlType.ASSETS
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv3_js)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "ilua-helpV3.0.txt",
                        urlType = UrlType.ASSETS
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv3_lua)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "igame-helpV1.0.txt",
                        urlType = UrlType.ASSETS
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iGame)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "iyu-中文编程V5.0.txt",
                        urlType = UrlType.ASSETS
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iv5_cn)
            ),
            PagerAdapterForFragment.Page(
                fragment = ReferenceItemFragment.newInstance(
                    ReferenceIntent(
                        title = "igame-中文编程V1.0.txt",
                        urlType = UrlType.ASSETS
                    )
                ),
                title = MaterialCodeToolApplication.application.getString(R.string.iGame_cn)
            )
        )

        binding.viewpager.apply {
            adapter = PagerAdapterForFragment(
                pages,
                childFragmentManager
            )
        }

        binding.tabLayout.setupWithViewPager(binding.viewpager)
    }
}