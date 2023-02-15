package ly.android.material.code.tool.activities.tools.postdev.request

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import ly.android.material.code.tool.activities.tools.postdev.request.body.*
import ly.android.material.code.tool.common.replaceFragment
import ly.android.material.code.tool.data.PostDevViewModel
import ly.android.material.code.tool.databinding.FragmentBodyBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind

class BodyFragment : BaseFragment() {

    private val binding by bind(FragmentBodyBinding::inflate)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = BodyFragment()
    }

    private val viewModel by activityViewModels<PostDevViewModel>()
    override fun initView(root: View) {
        super.initView(root)

        viewModel.bodyTypeState.observe(this){
            when (it) {
                0 -> {
                    binding.bodyNone.isChecked = true
                    replaceFragment(
                        binding.bodyFrame.id,
                        NoneBodyFragment.newInstance()
                    )
                }
                1 -> {
                    binding.bodyFormData.isChecked = true
                    replaceFragment(
                        binding.bodyFrame.id,
                        FormDataFragment.newInstance()
                    )
                }
                2 -> {
                    binding.bodyXWwwFormUrlencoded.isChecked = true
                    replaceFragment(
                        binding.bodyFrame.id,
                        FormUrlFragment.newInstance()
                    )
                }
                3 -> {
                    binding.bodyRaw.isChecked = true
                    replaceFragment(
                        binding.bodyFrame.id,
                        RawFragment.newInstance()
                    )
                }
                4 -> {
                    binding.bodyBinary.isChecked = true
                    replaceFragment(
                        binding.bodyFrame.id,
                        BinaryFragment.newInstance()
                    )
                }
            }
        }

        binding.bodyNone.setOnCheckedChangeListener { _, b ->
            if (b){
                viewModel.bodyTypeState.value = 0
            }
        }

        binding.bodyFormData.setOnCheckedChangeListener { _, b ->
            if (b){
                viewModel.bodyTypeState.value = 1
            }
        }

        binding.bodyXWwwFormUrlencoded.setOnCheckedChangeListener { _, b ->
            if (b){
                viewModel.bodyTypeState.value = 2
            }
        }

        binding.bodyRaw.setOnCheckedChangeListener { _, b ->
            if (b){
                viewModel.bodyTypeState.value = 3
            }
        }

        binding.bodyBinary.setOnCheckedChangeListener { _, b ->
            if (b) {
                viewModel.bodyTypeState.value = 4
            }
        }
    }
}