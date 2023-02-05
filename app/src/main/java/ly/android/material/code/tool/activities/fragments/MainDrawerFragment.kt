package ly.android.material.code.tool.activities.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import ly.android.material.code.tool.R
import ly.android.material.code.tool.activities.SettingsActivity
import ly.android.material.code.tool.data.MainViewModel
import ly.android.material.code.tool.databinding.FragmentMainDrawerBinding
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.ui.view.setOnFeedbackListener
import ly.android.material.code.tool.util.ToastUtils

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
                    R.id.nav_notes -> {
                        2
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
                    2 -> R.id.nav_notes
                    else -> {
                        R.id.nav_reference
                    }
                }
            )
        }

        binding.joinUs.setOnFeedbackListener(
            clickable = true,
            click = {
                val url =
                    "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=749196050&card_type=group&source=qrcode"
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }catch (e: Exception){
                    e.printStackTrace()
                    ToastUtils.toast(e.toString())
                }
            }
        )

        binding.settings.setOnFeedbackListener(
            clickable = true
        ) {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            requireActivity().startActivity(intent)
        }
    }
}