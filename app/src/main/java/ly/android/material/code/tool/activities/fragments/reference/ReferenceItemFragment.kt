package ly.android.material.code.tool.activities.fragments.reference

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import ly.android.material.code.tool.R
import ly.android.material.code.tool.common.readByte
import ly.android.material.code.tool.data.ReferenceViewModel
import ly.android.material.code.tool.data.entity.ReferenceIntent
import ly.android.material.code.tool.data.enums.ReferenceLanguage
import ly.android.material.code.tool.data.enums.UrlType
import ly.android.material.code.tool.databinding.FragmentReferenceItemBinding
import ly.android.material.code.tool.ui.adapter.ReferenceListAdapter
import ly.android.material.code.tool.ui.base.BaseFragment
import ly.android.material.code.tool.ui.common.bind
import ly.android.material.code.tool.util.ToastUtils
import java.nio.charset.Charset


private const val ARG_TITLE = "title"

class ReferenceItemFragment : BaseFragment() {

    private lateinit var referenceIntent: ReferenceIntent
    private val viewModel by viewModels<ReferenceViewModel>()
    private val binding by bind(FragmentReferenceItemBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            referenceIntent = gson.fromJson(it.getString(ARG_TITLE), ReferenceIntent::class.java)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    companion object {
        private var gson = Gson()

        @JvmStatic
        fun newInstance(referenceIntent: ReferenceIntent) = ReferenceItemFragment().apply {
            val json = gson.toJson(referenceIntent)
            arguments = Bundle().apply {
                putString(ARG_TITLE, json)
            }
        }
    }

    private lateinit var adapter: ReferenceListAdapter
    override fun initView(root: View) {
        super.initView(root)
        binding.listView.apply {
            layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            this@ReferenceItemFragment.adapter = ReferenceListAdapter(
                when (referenceIntent.title) {
                    getString(R.string.tieCode) -> {
                        ReferenceLanguage.TIE_CODE
                    }
                    else -> {
                        ReferenceLanguage.JAVA
                    }
                }
            )
            adapter = this@ReferenceItemFragment.adapter
        }

        viewModel.items.observe(this) {
            it?.let {
                adapter.updateList(it)
                initAnimate(requireContext())
                binding.root.finishRefresh(true)
            }

            binding.noneData.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        }

        binding.root.setOnRefreshListener {
            when (referenceIntent.urlType) {
                //本地数据
                UrlType.ASSETS -> {
                    activity?.apply {
                        val data = String(readByte(referenceIntent.title), Charset.defaultCharset())
                        viewModel.updateAssets(data)
                    }
                }
                //在线数据
                UrlType.NET -> {
                    binding.root.finishRefresh(true)
                    //ToastUtils.toast(R.string.no_msg)
                }
            }
        }

        binding.searchView.setOnTextChangedListener {
            viewModel.searchAssets(it.toString())
        }
    }

    private fun initAnimate(context: Context) {
        val animation: Animation = AnimationUtils.loadAnimation(context, R.anim.animate_list)
        val layoutAnimationController = LayoutAnimationController(animation)
        layoutAnimationController.order = LayoutAnimationController.ORDER_NORMAL
        layoutAnimationController.delay = 0.2f
        binding.listView.layoutAnimation = layoutAnimationController
    }

    override fun loadSingleData() {
        super.loadSingleData()
        binding.root.autoRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.searchView.setState(View.GONE)
    }
}