package ly.android.material.code.tool.activities.fragments.toos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guardanis.imageloader.ImageRequest
import ly.android.material.code.tool.databinding.FragmentToolsBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class ToolsFragment : BaseFragment() {

    private val binding by bind(FragmentToolsBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = ToolsFragment()
    }

    override fun initView(root: View) {
        super.initView(root)

        ImageRequest.create(binding.imageView)
            .setTargetAsset("test.svg")
            .execute();
    }
}