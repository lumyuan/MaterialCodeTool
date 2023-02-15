package ly.android.material.code.tool.activities.tools.postdev.request.body

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ly.android.material.code.tool.R
import ly.android.material.code.tool.databinding.FragmentNoneBodyBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class NoneBodyFragment : BaseFragment() {

    private val binding by bind(FragmentNoneBodyBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = NoneBodyFragment()
    }
}